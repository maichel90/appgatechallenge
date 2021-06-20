package com.larodriguezm.appgate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.larodriguezm.appgate.dto.DocumentDTO;
import com.larodriguezm.appgate.dto.IPAddressDTO;
import com.larodriguezm.appgate.service.IPAddressService;

@RestController
@RequestMapping("/ipaddress")
public class IPAddressController {
	
	@Autowired
	private IPAddressService ipAddressController;
	
	@PostMapping
    public ResponseEntity<Object> searchIpAddresLocation(@RequestBody IPAddressDTO ipAddressDTO) {
		DocumentDTO document = ipAddressController.searchIpAddresLocation(ipAddressDTO);
    	return new ResponseEntity<>(document,HttpStatus.OK);
    }

}
