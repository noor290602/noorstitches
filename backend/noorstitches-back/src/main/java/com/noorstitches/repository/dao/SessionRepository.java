package com.noorstitches.repository.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.noorstitches.repository.entity.Session;

@Repository
public interface SessionRepository extends JpaRepository<Session, String> {

	
}
