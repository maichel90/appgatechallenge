package com.larodriguezm.appgate.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.larodriguezm.appgate.dto.IPAddressDTO;
import com.larodriguezm.appgate.dto.ResponseSearchDTO;
import com.larodriguezm.appgate.service.IPAddressService;

@RestController
@RequestMapping("/ipaddress")
public class IPAddressController {
	
	@Autowired
	private IPAddressService ipAddressController;
	
	@PostMapping
    public ResponseEntity<Object> searchIpAddresLocation(@RequestBody IPAddressDTO ipAddressDTO) {
		List<ResponseSearchDTO> result = ipAddressController.searchIpAddresLocation(ipAddressDTO);
    	return new ResponseEntity<>(result,HttpStatus.OK);
    }

}
