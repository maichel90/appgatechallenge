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

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
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

	/**
	 * Metodo asyncrono encargado de cargar la informacion de un archivo en la
	 * fuente de datos realizando validaciones si la informacion suministrada ya
	 * existe en la fuente de datos para optimizar la data almacenada
	 * 
	 * @param document Entidad del documento que se quiere validad
	 * @param splitValue numero del micro archivo a cargar
	 * @param last si es el ultimo archivo
	 */
	@Async
	public void launchProcessDocument(Document document, String splitValue, boolean last) {
		log.info("Launch microfile MICROFILEID:{}", splitValue);
		log.info("MICROFILE {} date Starting {} ", splitValue, Calendar.getInstance().getTime());
		try (LineIterator it = FileUtils.lineIterator(loadDocument(document.getDocumentFolderName(), splitValue),
				"UTF-8")) {
			int count = 1000;
			while (it.hasNext()) {
				// cada mil ciclos se deja notificacion en consola que el proceso esta trabanado correctamente
				if (count == 1000) {
					count = 0;
					log.info("MICROFILE {} WORKING.................", splitValue);
				}
				String[] splitLine = it.nextLine().replace("\"", "").split("\\,");
				
				// valida si existe la ciudad en fuente de datos
				City city = findCity(splitLine[5], splitLine[6], splitLine[7]);
				if (city.getCityId() == null) {
					// valida si existe la region en fuente de datos					
					Region region = findRegion(splitLine[4]);
					if (region.getRegionId() == null) {
						
						// valida si existe el pais en fuente de datos
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
				//Valida o crea el nuevo segmento de ip
				findOrCreateIpRange(splitLine[0], splitLine[1], city);
			}
			
			// si es el ultimo microarchivo finaliza el proceso del documento
			if (last) {
				document.setProcessStatus(ProcessStatus.FINALIZED);
				documentRepository.save(document);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		log.info("MICROFILE {} date Finishing {} ", splitValue, Calendar.getInstance().getTime());
	}

	/**
	 * Metodo encargado de realizar la consulta de la ciudad si no existe crea un builder para luego ser almacenado
	 * @param folderName
	 * @param splitValue
	 * @return
	 */
	private File loadDocument(String folderName, String splitValue) {
		Path fileStorageLocation = Paths.get(uploadDir + folderName + "/").toAbsolutePath().normalize();
		Path targetLocation = fileStorageLocation.resolve("segment" + splitValue);
		return targetLocation.toFile();
	}

	/**
	 * Metodo encargado de realizar la consulta de la ciudad si no existe crea un builder para luego ser almacenado
	 * @param cityName
	 * @param cityLatitud
	 * @param cityLongitud
	 * @return
	 */
	private City findCity(String cityName, String cityLatitud, String cityLongitud) {
		Optional<City> optional = cityRepository.findByCityName(cityName);
		if (optional.isPresent()) {
			return optional.get();
		}
		return City.builder().cityName(cityName).cityLatitud(cityLatitud).cityLongitud(cityLongitud).build();

	}

	/**
	 * Metodo encargado de realizar la consulta de la region si no existe crea un builder para luego ser almacenado
	 * @param regionName
	 * @return
	 */
	private Region findRegion(String regionName) {
		Optional<Region> optional = regionRepository.findByRegionName(regionName);
		if (optional.isPresent()) {
			return optional.get();
		}
		return Region.builder().regionName(regionName).build();

	}

	/**
	 * Metodo encargado de realizar la consulta del pais si no existe crea un builder para luego ser almacenado
	 * @param countryCode
	 * @param countryName
	 * @return
	 */
	private Country findCountry(String countryCode, String countryName) {
		Optional<Country> optional = countryRepository.findByCountryCodeAndCountryName(countryCode, countryName);
		if (optional.isPresent()) {
			return optional.get();
		}
		return Country.builder().countryName(countryName).countryCode(countryCode).build();
	}

	/**
	 * Metodo encargado de realizar la consulta en la tabla de rangos de ip 
	 * @param ipfrom
	 * @param ipto
	 * @param city
	 * @return
	 */
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
