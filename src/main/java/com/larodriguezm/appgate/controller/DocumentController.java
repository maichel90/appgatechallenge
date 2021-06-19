package com.larodriguezm.appgate.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.larodriguezm.appgate.dto.DocumentDTO;
import com.larodriguezm.appgate.service.DocumentService;

@RestController
@RequestMapping("/document")
public class DocumentController {
	
	@Autowired
	private DocumentService documentService;
	
	@PostMapping
    public ResponseEntity<Object> uploadDocument(@RequestParam("file") MultipartFile file) {
		DocumentDTO document = documentService.uploadDocument(file);
    	return new ResponseEntity<>(document,HttpStatus.OK);
    }
	
	@GetMapping
    public ResponseEntity<Object> getDocuments() {
		List<DocumentDTO> documents = documentService.getDocuments();
    	return new ResponseEntity<>(documents,HttpStatus.OK);
    }
	
	@PostMapping("/{documentId}")
    public ResponseEntity<Object> launchProcessDocument(@PathVariable String documentId) {
		DocumentDTO document = documentService.launchProcessDocument(documentId);
    	return new ResponseEntity<>(document,HttpStatus.OK);
    }
	
}
