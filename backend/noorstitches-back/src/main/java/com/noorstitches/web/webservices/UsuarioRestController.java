package com.noorstitches.web.webservices;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.noorstitches.model.dto.LineaPedidoDTO;
import com.noorstitches.model.dto.PedidoDTO;
import com.noorstitches.model.dto.UsuarioDTO;
import com.noorstitches.repository.dao.SessionRepository;
import com.noorstitches.repository.entity.Session;
import com.noorstitches.repository.entity.Usuario;
import com.noorstitches.service.PedidoService;
import com.noorstitches.service.UsuarioService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/usuarios")
public class UsuarioRestController {
	
	private static final Logger log = LoggerFactory.getLogger(UsuarioRestController.class);		
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private PedidoService pedidoService;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private SessionRepository sessionRepository;
	
	// Listar los usuarios 
	 @GetMapping("")
	 public ResponseEntity<List<UsuarioDTO>> findAll() {
		 log.info(UsuarioRestController.class.getSimpleName() + " - findAll: Mostramos todos los usuarios");  
		 
		 List<UsuarioDTO> listaUsuariosDTO = usuarioService.findAll();
		 
		 return new ResponseEntity<>(listaUsuariosDTO, HttpStatus.OK);
	}
	 
	// Listar todos los pedidos dado un id de usuario
	@GetMapping("/{idUsuario}/pedidos")
	public ResponseEntity<List<PedidoDTO>> findPedidosByUsuario(@PathVariable("idUsuario") Long idUsuario) {

		log.info(UsuarioRestController.class.getSimpleName() + " - findPedidosByUsuario: Mostramos la información de los pedidos de un usuario:" + idUsuario);

		UsuarioDTO usuarioDTO = new UsuarioDTO();
		usuarioDTO.setId(idUsuario);
		usuarioDTO = usuarioService.findById(usuarioDTO);

		List<PedidoDTO> listaPedidosDTO = new ArrayList<>();

		if (usuarioDTO != null) {
			listaPedidosDTO = pedidoService.findAllByUsuario(idUsuario);
		}

		if (listaPedidosDTO == null) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>(listaPedidosDTO, HttpStatus.OK);
		}
	}
	
	// Visualizar la informacion de un usuario
	@GetMapping("/{idUsuario}")
	public ResponseEntity<UsuarioDTO> findById(@PathVariable("idUsuario") Long idUsuario) {
		
		log.info(UsuarioRestController.class.getSimpleName() + " - findById: Mostramos la informacion del usuario:" + idUsuario);
		
		// Obtenemos el usuario y lo pasamos al modelo
		UsuarioDTO usuarioDTO = new UsuarioDTO();
		usuarioDTO.setId(idUsuario);
		usuarioDTO = usuarioService.findById(usuarioDTO);
				
		if (usuarioDTO == null) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>(usuarioDTO, HttpStatus.OK);
		}
	}
	
	// Salvar usuarios
	@PostMapping("/registro")
	public ResponseEntity<UsuarioDTO> registro(@RequestBody UsuarioDTO usuarioDTO) {
	    log.info(UsuarioRestController.class.getSimpleName() + " - add: Salvamos los datos del usuario:" + usuarioDTO.toString());

	    try {
	        UsuarioDTO usuarioInsertado = usuarioService.save(usuarioDTO);
	        return new ResponseEntity<>(usuarioInsertado, HttpStatus.OK);
	    } catch (DataIntegrityViolationException e) {
	        log.error("Error al registrar el usuario: posible duplicado de email - " + e.getMessage());
	        return new ResponseEntity<>(HttpStatus.CONFLICT); // 409
	    } catch (Exception e) {
	        log.error("Error inesperado al registrar el usuario: " + e.getMessage());
	        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	    }
	}

	
	 // Actualizacion de clientes
	 @PutMapping("/update")
	 public ResponseEntity<UsuarioDTO> update(@RequestBody UsuarioDTO usuarioDTO) {

		 log.info(UsuarioRestController.class.getSimpleName() + "- update: Modificamos el usuario: " + usuarioDTO.getId());
	
		 // Obtenemos el usuario para verificar que existe
		 UsuarioDTO usuarioExDTO = new UsuarioDTO();
		 usuarioExDTO.setId(usuarioDTO.getId());
		 usuarioExDTO = usuarioService.findById(usuarioExDTO);
	
		 if(usuarioExDTO == null) {
			 return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		 } else {
			 usuarioDTO = usuarioService.save(usuarioDTO);
			 return new ResponseEntity<>(usuarioDTO, HttpStatus.OK);
		 }
	 }
	
	 // Borrar un usuario
	 @DeleteMapping("/delete/{idUsuario}")
	 public ResponseEntity<String> delete(@PathVariable("idUsuario") Long idUsuario) {

		 log.info(UsuarioRestController.class.getSimpleName() + " - delete: Borramos el usuario:" + idUsuario);
	
		 // Creamos un usuario y le asignamos el id. Este usuaio es el que se va a borrar
		 UsuarioDTO usuarioDTO = new UsuarioDTO();
		 usuarioDTO.setId(idUsuario);
		 usuarioService.delete(usuarioDTO);
	
		 return new ResponseEntity<>("Usuario borrado satisfactoriamente", HttpStatus.OK);
	 }	
	 
	 //Login usuario
	 @PostMapping("/login")
	 public ResponseEntity<UsuarioDTO> login(@RequestBody UsuarioDTO usuarioDTO, HttpServletResponse response) {
	     String email = usuarioDTO.getEmail();
	     String password = usuarioDTO.getPassword();

	     UsuarioDTO usuarioBuscadoDTO = usuarioService.findByEmail(email);

	     if (usuarioBuscadoDTO != null && passwordEncoder.matches(password, usuarioBuscadoDTO.getPassword())) {
	    	 Session session = new Session();
	         session.setId(UUID.randomUUID().toString()); // genera ID único
	         session.setIdUser(usuarioBuscadoDTO.getId());
	         sessionRepository.save(session);
	         return new ResponseEntity<>(usuarioBuscadoDTO, HttpStatus.OK);
	     } else {
	         return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
	     }
	 }
	 
	 /*
	 //Cookies
	 @GetMapping("/change-username")
	 public String setCookie(HttpServletResponse response) {
	     // create a cookie
	     Cookie cookie = new Cookie("username", "Jovan");

	     //add cookie to response
	     response.addCookie(cookie);

	     return "Username is changed!";
	 }
	 */
}
