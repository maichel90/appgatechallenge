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
public class IPAddressException extends RuntimeException{

	private static final long serialVersionUID = 5129272446183079036L;
	
	String message;
	HttpStatus code;
	Throwable cause;
	
}
