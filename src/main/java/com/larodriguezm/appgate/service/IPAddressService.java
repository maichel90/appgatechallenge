package com.larodriguezm.appgate.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.larodriguezm.appgate.dto.IPAddressDTO;
import com.larodriguezm.appgate.dto.ResponseSearchDTO;
import com.larodriguezm.appgate.mapper.IResponseSearchMapper;
import com.larodriguezm.appgate.model.RangeIp;
import com.larodriguezm.appgate.repository.RangeRepository;
import com.larodriguezm.appgate.util.UtilIPAddress;

@Service
public class IPAddressService {
	
	@Autowired
	private RangeRepository rangeRepository;

	public List<ResponseSearchDTO> searchIpAddresLocation(IPAddressDTO ipAddressDTO) {
		if (UtilIPAddress.validate(ipAddressDTO.getIpaddress())) {
			Integer ipAddressDecimal = UtilIPAddress.ipAddressDecimal(ipAddressDTO.getIpaddress());
			Optional<List<RangeIp>> optional = rangeRepository.findByDecimal(ipAddressDecimal,ipAddressDecimal);
			if(optional.isPresent()) {
				return IResponseSearchMapper.INSTANCE.entityToDTOList(optional.get());
			}
		}
		return null;
	}
	
	

}
