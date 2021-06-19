package com.larodriguezm.appgate.mapper;

import com.larodriguezm.appgate.dto.DocumentDTO;
import com.larodriguezm.appgate.model.Document;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-06-19T12:37:25-0500",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 11.0.9 (Red Hat, Inc.)"
)
public class IDocumentMapperImpl implements IDocumentMapper {

    @Override
    public DocumentDTO entityToDTO(Document entity) {
        if ( entity == null ) {
            return null;
        }

        DocumentDTO documentDTO = new DocumentDTO();

        documentDTO.setDocumentId( entity.getDocumentId() );
        documentDTO.setDocumentName( entity.getDocumentName() );
        documentDTO.setDocumentFormat( entity.getDocumentFormat() );
        documentDTO.setCreationDate( entity.getCreationDate() );

        return documentDTO;
    }

    @Override
    public Document dtoToEntity(DocumentDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Document document = new Document();

        document.setDocumentId( dto.getDocumentId() );
        document.setDocumentName( dto.getDocumentName() );
        document.setDocumentFormat( dto.getDocumentFormat() );
        document.setCreationDate( dto.getCreationDate() );

        return document;
    }

    @Override
    public List<DocumentDTO> entityToDTOList(List<Document> entity) {
        if ( entity == null ) {
            return null;
        }

        List<DocumentDTO> list = new ArrayList<DocumentDTO>( entity.size() );
        for ( Document document : entity ) {
            list.add( entityToDTO( document ) );
        }

        return list;
    }
}
