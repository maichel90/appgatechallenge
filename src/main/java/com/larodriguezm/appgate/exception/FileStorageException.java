package com.larodriguezm.appgate.exception;

import org.springframework.http.HttpStatus;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileStorageException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	String message;
	HttpStatus code;
	Throwable cause;
}
