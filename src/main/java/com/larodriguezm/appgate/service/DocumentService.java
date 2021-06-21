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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.larodriguezm.appgate.dto.DocumentDTO;
import com.larodriguezm.appgate.exception.FileStorageException;
import com.larodriguezm.appgate.mapper.IDocumentMapper;
import com.larodriguezm.appgate.model.Document;
import com.larodriguezm.appgate.model.ProcessStatus;
import com.larodriguezm.appgate.repository.DocumentRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DocumentService {

	@Value("${file.upload-dir}")
	private String uploadDir;

	@Autowired
	private DocumentRepository documentRepository;

	@Autowired
	private ProcessDocument processDocument;

	/**
	 * Metodo que controla el flujo para la carga de un archivo y almacenarlo en la
	 * base de datos
	 * 
	 * @param file Multipart el cual almacena el archivo
	 * @return DTO que muestra la informacion almacenda en la fuenta de datos
	 */
	public DocumentDTO uploadDocument(MultipartFile file) {
		log.debug("Starting the document upload process");
		String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
		String[] split = originalFileName.split("\\.");
		String uuid = UUID.randomUUID().toString();
		String finalFileName = uuid + "." + split[split.length - 1];
		saveDocumentFileSystem(finalFileName, uuid, file);
		Integer amountSplit = splitDocumentFileSystem(finalFileName, uuid);
		Document document = saveDocument(finalFileName, uuid, amountSplit, file);
		return IDocumentMapper.INSTANCE.entityToDTO(document);
	}

	/**
	 * Metodo que permite almacenar el archivo en el filesystem en una ubicacion
	 * especifica y con un nombre UUID
	 * 
	 * @param originalFileName nombre del archivo
	 * @param folderUUID       Carpeta donde se va almacenar el archivo
	 * @param file             Archivo que se quiere cargar
	 */
	private void saveDocumentFileSystem(String originalFileName, String folderUUID, MultipartFile file) {
		try {
			log.debug("Starting document upload to file system");
			Path fileStorageLocation = Paths.get(uploadDir + folderUUID + "/").toAbsolutePath().normalize();
			Path targetLocation = fileStorageLocation.resolve(originalFileName);
			Files.createDirectories(fileStorageLocation);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			log.debug("Unable to save file" + originalFileName + ". Try again!");
			throw new FileStorageException("Unable to save file" + originalFileName + ". Try again!",
					HttpStatus.BAD_REQUEST, e);
		}
	}

	/**
	 * Metodo que permite tomar un archivo del filesystem y dividirlo en pequechos
	 * archivos de 3M basado en el sistema operativo linux
	 * 
	 * @param originalFileName nombre del archivo
	 * @param folderUUID       Carpeta donde se va almacenar el archivo
	 * @return Cantidad de archivos en los que fue dividido el principal
	 */
	private Integer splitDocumentFileSystem(String originalFileName, String folderUUID) {
		log.debug("Starting document split in microfiles of size 3M");
		String filePath = uploadDir + folderUUID + "/";
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.command("sh", "-c",
				"cd " + filePath + " && split -d -C 3M " + originalFileName + " segment && ls segment* | wc -l");
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
		} catch (Exception e) {
			e.printStackTrace();
			log.debug("An error was generated contact the administrator or Try again!");
			throw new FileStorageException("An error was generated contact the administrator or Try again!",
					HttpStatus.INTERNAL_SERVER_ERROR, e);
		}
		return 0;
	}

	/**
	 * Metodo que permite almacenar la informacion del archvio en la base de datos
	 * 
	 * @param originalFileName nombre del archivo
	 * @param folderUUID       Carpeta donde se va almacenar el archivo
	 * @param amountSplit      Cantidad de archivos en los que fue dividido el
	 *                         principal
	 * @param file             Archivo que se quiere cargar
	 * @return DTO del registro almacenado
	 */
	private Document saveDocument(String originalFileName, String folderName, Integer amountSplit, MultipartFile file) {
		log.debug("Creating record of file upload in DB");
		Document document = new Document();
		document.setDocumentName(originalFileName);
		document.setCreationDate(new Date());
		document.setAmountSplit(amountSplit);
		document.setDocumentFolderName(folderName);
		document.setDocumentFormat(file.getContentType());
		document.setProcessStatus(ProcessStatus.LOADED);
		return documentRepository.save(document);
	}

	/**
	 * Metodo que permite realizar la consulta de los documentos almacenados en la
	 * base de datos
	 * 
	 * @return retorna una lista de DTO de los registros almacenados
	 */
	public List<DocumentDTO> getDocuments() {
		return IDocumentMapper.INSTANCE.entityToDTOList(documentRepository.findAll());
	}

	/**
	 * Metodo que permite realizar el lanzamiento del procesamiento del archivo. Se
	 * realiza el lanzamiento de procesamientos asincronos basado en la cantidad de
	 * diviciones que se le realizo al archivo en el proceso de carga.
	 * 
	 * @param documentId Id del documento que se quiere procesar
	 * @return DTO del registro almacenado con estado actualizado
	 */
	public DocumentDTO launchProcessDocument(Integer documentId) {
		log.debug("Starting the loading process in the db. DOUMENTID:{}", documentId);
		Document document = null;
		Optional<Document> optional = documentRepository.findByDocumentId(documentId);
		if (optional.isPresent() && optional.get().getProcessStatus() == ProcessStatus.LOADED) {
			document = optional.get();
			for (int a = 0; a < document.getAmountSplit(); a++) {
				DecimalFormat df = new DecimalFormat("#00");
				processDocument.launchProcessDocument(document, df.format(a),
						(document.getAmountSplit() - 1) == a ? true : false);
			}
			document.setProcessStatus(ProcessStatus.PROCESSING);
			document = documentRepository.save(document);
			return IDocumentMapper.INSTANCE.entityToDTO(document);
		} else {
			log.debug("There is no record with that identifier or the record has already been processed!");
			throw new FileStorageException(
					"There is no record with that identifier or the record has already been processed!",
					HttpStatus.ACCEPTED, null);
		}
	}
}
