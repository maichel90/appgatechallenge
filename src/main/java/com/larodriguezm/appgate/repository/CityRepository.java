package com.larodriguezm.appgate.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.larodriguezm.appgate.model.City;

public interface CityRepository extends JpaRepository<City, Integer>{

	Optional<City> findByCityName(String cityName);

	Optional<City> findByCityNameAndCityLatitudAndCityLongitud(String cityName,String cityLatitud,String cityLongitud);

}
