package com.larodriguezm.appgate.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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
import com.larodriguezm.appgate.model.ProcessStatus;
import com.larodriguezm.appgate.repository.DocumentRepository;

@Service
public class DocumentService {
	
	@Value("${file.upload-dir}")
	private String uploadDir;
	
	@Autowired
	private DocumentRepository documentRepository;
	
	@Autowired
	private ProcessDocument processDocument;

	public DocumentDTO uploadDocument(MultipartFile file) {
		String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
		String[] split = originalFileName.split("\\.");
		String uuid = UUID.randomUUID().toString();
		String finalFileName = uuid+"."+split[split.length-1];
		saveDocumentFileSystem(finalFileName,uuid, file);
		Integer amountSplit = splitDocumentFileSystem(finalFileName,uuid);
		Document document =  saveDocument(finalFileName,uuid,amountSplit, file);
		return IDocumentMapper.INSTANCE.entityToDTO(document);
	}
	
	private void saveDocumentFileSystem(String originalFileName, String folderUUID, MultipartFile file) {
		try {
			Path fileStorageLocation = Paths.get(uploadDir+folderUUID+"/").toAbsolutePath().normalize();
			Path targetLocation = fileStorageLocation.resolve(originalFileName);
			Files.createDirectories(fileStorageLocation);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new FileStorageException("No se puede guardar el archivo " + originalFileName + ". Intente nuevamente!", e);
		}
	}
	
	private Integer splitDocumentFileSystem(String originalFileName, String folderUUID) {
		String filePath = uploadDir+folderUUID+"/";
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.command("bash", "-c", "cd "+filePath+" && split -d -C 3M "+originalFileName+" segment && ls segment* | wc -l");
		try {
			Process process = processBuilder.start();
	        StringBuilder output = new StringBuilder();
	        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
	        String line;
	        while ((line = reader.readLine()) != null) {
	            output.append(line);
	        }
	        int exitVal = process.waitFor();
	        if (exitVal == 0) {
	        	return Integer.parseInt(output.toString());
	        }
		}catch (Exception e) {
	        e.printStackTrace();
	    }
		return 0;
	}
	
	private Document saveDocument(String originalFileName, String folderName, Integer amountSplit, MultipartFile file) {
		Document document = new Document();
		document.setDocumentName(originalFileName);
		document.setCreationDate(new Date());
		document.setAmountSplit(amountSplit);
		document.setDocumentFolderName(folderName);
		document.setDocumentFormat(file.getContentType());
		document.setProcessStatus(ProcessStatus.LOADED);
		return documentRepository.save(document);
	}

	public List<DocumentDTO> getDocuments() {
		return IDocumentMapper.INSTANCE.entityToDTOList(documentRepository.findAll());
	}

	public DocumentDTO launchProcessDocument(Integer documentId) {
		Document document = null;
		Optional<Document> optional = documentRepository.findByDocumentId(documentId);
		if(optional.isPresent() && optional.get().getProcessStatus()==ProcessStatus.LOADED) {
			document = optional.get();
			for(int a = 0; a < document.getAmountSplit(); a++ ) {
				DecimalFormat df = new DecimalFormat("#00");
				processDocument.launchProcessDocument(document,df.format(a),(document.getAmountSplit()-1)==a?true:false);
			}
		}
		document.setProcessStatus(ProcessStatus.PROCESSING);
		document = documentRepository.save(document);
		return IDocumentMapper.INSTANCE.entityToDTO(document);
	}
}
