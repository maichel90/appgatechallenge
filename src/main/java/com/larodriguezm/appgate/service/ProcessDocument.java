package com.larodriguezm.appgate.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.larodriguezm.appgate.model.City;
import com.larodriguezm.appgate.model.Country;
import com.larodriguezm.appgate.model.Document;
import com.larodriguezm.appgate.model.ProcessStatus;
import com.larodriguezm.appgate.model.RangeIp;
import com.larodriguezm.appgate.model.Region;
import com.larodriguezm.appgate.repository.CityRepository;
import com.larodriguezm.appgate.repository.CountryRepository;
import com.larodriguezm.appgate.repository.DocumentRepository;
import com.larodriguezm.appgate.repository.RangeRepository;
import com.larodriguezm.appgate.repository.RegionRepository;

@Service
public class ProcessDocument {

	@Value("${file.upload-dir}")
	private String uploadDir;
	
	@Autowired
	private DocumentRepository documentRepository;

	@Autowired
	private CityRepository cityRepository;

	@Autowired
	private RegionRepository regionRepository;

	@Autowired
	private CountryRepository countryRepository;

	@Autowired
	private RangeRepository rangeRepository;

	@Async
	public void launchProcessDocument(Document document, String splitValue,boolean last) {
		System.out.println(Calendar.getInstance().getTime());
		try (LineIterator it = FileUtils.lineIterator(loadDocument(document.getDocumentFolderName(), splitValue),
				"UTF-8")) {
			int count = 1000;
			while (it.hasNext()) {
				if (count == 1000) {
					count = 0;
					System.out.println("DOCUMENT " + splitValue + " WORKING.................");
				}
				String[] splitLine = it.nextLine().replace("\"", "").split("\\,");
				City city = findCity(splitLine[5], splitLine[6], splitLine[7]);
				if (city.getCityId() == null) {
					Region region = findRegion(splitLine[4]);
					if (region.getRegionId() == null) {
						Country country = findCountry(splitLine[2], splitLine[3]);
						if (country.getCountryId() == null) {
							country = countryRepository.save(country);
						}
						region.setCountry(country);
						region = regionRepository.save(region);
					}
					city.setRegion(region);
					city = cityRepository.save(city);
				}
				findOrCreateIpRange(splitLine[0], splitLine[1], city);
			}
			if(last) {
				document.setProcessStatus(ProcessStatus.FINALIZED);
				documentRepository.save(document);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(Calendar.getInstance().getTime());
	}

	private File loadDocument(String folderName, String splitValue) {
		Path fileStorageLocation = Paths.get(uploadDir + folderName + "/").toAbsolutePath().normalize();
		Path targetLocation = fileStorageLocation.resolve("segment" + splitValue);
		return targetLocation.toFile();
	}

	private City findCity(String cityName, String cityLatitud, String cityLongitud) {
		Optional<City> optional = cityRepository.findByCityName(cityName);
		if (optional.isPresent()) {
			return optional.get();
		}
		return City.builder().cityName(cityName).cityLatitud(cityLatitud).cityLongitud(cityLongitud).build();

	}

	private Region findRegion(String regionName) {
		Optional<Region> optional = regionRepository.findByRegionName(regionName);
		if (optional.isPresent()) {
			return optional.get();
		}
		return Region.builder().regionName(regionName).build();

	}

	private Country findCountry(String countryCode, String countryName) {
		Optional<Country> optional = countryRepository.findByCountryCodeAndCountryName(countryCode, countryName);
		if (optional.isPresent()) {
			return optional.get();
		}
		return Country.builder().countryName(countryName).countryCode(countryCode).build();
	}

	private RangeIp findOrCreateIpRange(String ipfrom, String ipto, City city) {
		Optional<RangeIp> optional = rangeRepository.findByIpfromAndIptoAndCity(ipfrom, ipto, city);
		if (optional.isPresent()) {
			return optional.get();
		} else {
			RangeIp range = RangeIp.builder().city(city).ipfrom(ipfrom).ipto(ipto).build();
			return rangeRepository.save(range);
		}
	}
}
