package com.larodriguezm.appgate.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.larodriguezm.appgate.dto.DocumentDTO;
import com.larodriguezm.appgate.model.Document;

@Mapper
public interface IDocumentMapper {

	IDocumentMapper INSTANCE  = Mappers.getMapper( IDocumentMapper.class ); 
	
	DocumentDTO entityToDTO(Document entity);
	
	Document dtoToEntity(DocumentDTO dto);
	
	List<DocumentDTO> entityToDTOList(List<Document > entity);
}
