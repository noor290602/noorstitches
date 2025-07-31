package com.noorstitches.model.dto;

import java.io.Serializable;

import com.noorstitches.repository.entity.Session;
import lombok.Data;

@Data
public class SessionDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String id;
	private Long idUser;
	
	// Convierte una entidad a un objeto DTO
    public static SessionDTO convertToDTO(Session session) {
    	
    	if (session == null) {
            return null;
        }

        // Creamos el SessionDTO y asignamos los valores básicos
        SessionDTO sDTO = new SessionDTO();
        sDTO.setId(session.getId());
        sDTO.setIdUser(session.getIdUser());

        // Retorna el DTO
        return sDTO;
    }

    // Convierte un objeto DTO a una entidad
    public static Session convertToEntity(SessionDTO sessionDTO) {
    	
    	if (sessionDTO == null) {
            return null; 
        }
    	
        // Creamos la entidad Usuario y le asignamos los valores
        Session session = new Session();
        session.setId(sessionDTO.getId());
        session.setIdUser(sessionDTO.getIdUser());

        // Retorna la entidad
        return session;
    }
}
