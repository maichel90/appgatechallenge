package com.larodriguezm.appgate.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.larodriguezm.appgate.model.City;
import com.larodriguezm.appgate.model.Country;
import com.larodriguezm.appgate.model.Document;
import com.larodriguezm.appgate.model.Region;
import com.larodriguezm.appgate.repository.CityRepository;
import com.larodriguezm.appgate.repository.CountryRepository;
import com.larodriguezm.appgate.repository.RegionRepository;

@Service
public class ProcessDocument {
	
	@Value("${file.upload-dir}")
	private String uploadDir;
	
	@Autowired
	private CityRepository cityRepository;
	
	@Autowired
	private RegionRepository regionRepository;
	
	@Autowired 
	private CountryRepository countryRepository;
	
	public void launchProcessDocument(Document document) {
		try(LineIterator it =FileUtils.lineIterator(loadDocument(document.getDocumentName()), "UTF-8")) {
			while (it.hasNext()) {
				
				String[] splitLine = it.nextLine().replace("\"", "").split("\\,");
				findCity(splitLine[5], splitLine[6], splitLine[7]);
				findRegion(splitLine[4]);
				findCountry(splitLine[2], splitLine[3]);
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private File loadDocument(String documentName) {
		Path fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
		Path targetLocation = fileStorageLocation.resolve(documentName);
		return targetLocation.toFile();
	}
	
	private City findCity(String cityName,String cityLatitud,String cityLongitud) {
		Optional<City> optional = cityRepository.findByCityNameAndCityLatitudAndCityLongitud(cityName, cityLatitud, cityLongitud);
		return optional.get();
	}
	
	private Region findRegion(String regionName) {
		Optional<Region> optional = regionRepository.findByRegionName(regionName);
		return optional.get();
	}
	private Country findCountry(String countryCode,String countryName) {
		Optional<Country> optional = countryRepository.findByCountryCodeAndCountryName(countryCode, countryName);
		return optional.get();
		
	}
}
