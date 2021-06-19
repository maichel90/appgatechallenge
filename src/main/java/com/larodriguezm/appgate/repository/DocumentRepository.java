package com.larodriguezm.appgate.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.larodriguezm.appgate.model.Document;

public interface DocumentRepository extends JpaRepository<Document, Integer>{
	
	Optional<Document> findByDocumentId(Integer documentId);

}
