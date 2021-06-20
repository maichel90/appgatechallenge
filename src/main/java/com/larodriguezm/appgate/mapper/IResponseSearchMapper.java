package com.larodriguezm.appgate.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import com.larodriguezm.appgate.dto.ResponseSearchDTO;
import com.larodriguezm.appgate.model.RangeIp;

@Mapper
public interface IResponseSearchMapper {

	IResponseSearchMapper INSTANCE = Mappers.getMapper(IResponseSearchMapper.class);
	
	@Mappings({
		@Mapping(source = "city.cityName", target = "cityName"),
		@Mapping(source = "city.region.regionName", target = "regionName"),
		@Mapping(source = "city.region.country.countryCode", target = "countryCode"),
		@Mapping(source = "city.region.country.countryName", target = "countryName")
	})
	ResponseSearchDTO entityToDTO(RangeIp rangeip);

	
	List<ResponseSearchDTO> entityToDTOList(List<RangeIp> rangeip);
}
