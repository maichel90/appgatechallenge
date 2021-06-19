package com.larodriguezm.appgate.dto;

import java.util.Date;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DocumentDTO {

	private Integer documentId;

	private String documentName;

	private String documentFormat;

	private Date creationDate;

}
