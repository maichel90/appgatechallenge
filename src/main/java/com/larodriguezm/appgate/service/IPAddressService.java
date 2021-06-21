package com.larodriguezm.appgate.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.larodriguezm.appgate.dto.IPAddressDTO;
import com.larodriguezm.appgate.dto.ResponseSearchDTO;
import com.larodriguezm.appgate.exception.IPAddressException;
import com.larodriguezm.appgate.mapper.IResponseSearchMapper;
import com.larodriguezm.appgate.model.RangeIp;
import com.larodriguezm.appgate.repository.RangeRepository;
import com.larodriguezm.appgate.util.UtilIPAddress;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class IPAddressService {

	@Autowired
	private RangeRepository rangeRepository;

	/**
	 * Metodo que realiza la validacion de la ip, realiza el calculo de la ip en
	 * decimal y la consulta en la base de datos para retornar datos de ubicacion de
	 * la ip
	 * 
	 * @param ipAddressDTO DTO de entrada el cual contine la ip
	 * @return Lista de ResponseSearchDTO el cual retorna las posibles ubicaciones de la ip
	 */
	public List<ResponseSearchDTO> searchIpAddresLocation(IPAddressDTO ipAddressDTO) {
		log.debug("Checking information to {}", ipAddressDTO.getIpaddress());
		if (UtilIPAddress.validate(ipAddressDTO.getIpaddress())) {
			Integer ipAddressDecimal = UtilIPAddress.ipAddressDecimal(ipAddressDTO.getIpaddress());
			Optional<List<RangeIp>> optional = rangeRepository.findByDecimal(ipAddressDecimal, ipAddressDecimal);
			if (optional.isPresent()) {
				return IResponseSearchMapper.INSTANCE.entityToDTOList(optional.get());
			}
			log.debug("There is no information on the requested segment {}", ipAddressDTO.getIpaddress());
			throw new IPAddressException("There is no information on the requested segment", HttpStatus.ACCEPTED, null);

		}
		log.debug("Incorrect format, please correct it and try again {}", ipAddressDTO.getIpaddress());
		throw new IPAddressException("Incorrect format, please correct it and try again", HttpStatus.ACCEPTED, null);
	}

}
