package com.larodriguezm.appgate.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.larodriguezm.appgate.dto.DocumentDTO;
import com.larodriguezm.appgate.exception.FileStorageException;
import com.larodriguezm.appgate.mapper.IDocumentMapper;
import com.larodriguezm.appgate.model.Document;
import com.larodriguezm.appgate.repository.DocumentRepository;

@Service
public class DocumentService {
	
	@Value("${file.upload-dir}")
	private String uploadDir;
	
	@Autowired
	private DocumentRepository documentRepository;

	public DocumentDTO uploadDocument(MultipartFile file) {
		String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
		String[] split = originalFileName.split("\\.");
		String finalFileName = UUID.randomUUID().toString()+"."+split[split.length-1];
		saveDocumentFileSystem(finalFileName, file);
		Document document =  saveDocument(finalFileName, file);
		return IDocumentMapper.INSTANCE.entityToDTO(document);
	}
	
	private void saveDocumentFileSystem(String originalFileName, MultipartFile file) {
		try {
			Path fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
			Path targetLocation = fileStorageLocation.resolve(originalFileName);
			Files.createDirectories(fileStorageLocation);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new FileStorageException("No se puede guardar el archivo " + originalFileName + ". Intente nuevamente!", e);
		}
	}
	
	private Document saveDocument(String originalFileName, MultipartFile file) {
		Document document = new Document();
		document.setDocumentName(originalFileName);
		document.setCreationDate(new Date());
		document.setDocumentFormat(file.getContentType());;
		return documentRepository.save(document);
	}

	public List<DocumentDTO> getDocuments() {
		// TODO Auto-generated method stub
		return null;
	}

	public DocumentDTO launchProcessDocument(Integer documentId) {
		// TODO Auto-generated method stub
		return null;
	}

}
