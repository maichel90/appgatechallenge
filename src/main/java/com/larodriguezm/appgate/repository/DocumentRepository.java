package com.larodriguezm.appgate.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.larodriguezm.appgate.model.Document;

public interface DocumentRepository extends JpaRepository<Document, Integer>{

}
