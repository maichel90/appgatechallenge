package com.larodriguezm.appgate.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResponseSearchDTO {

	private String countryCode;
	
	private String countryName;
	
	private String regionName;

	private String cityName;

}
