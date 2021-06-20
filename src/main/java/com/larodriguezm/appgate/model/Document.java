package com.larodriguezm.appgate.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Document {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer documentId;
		
	private String documentName;
	
	private String documentFolderName;
		
    private String documentFormat;
    
    private Integer amountSplit;
        
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDate;
	
	@Enumerated(EnumType.STRING)
	private ProcessStatus processStatus;
	
}
