package com.larodriguezm.appgate.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.larodriguezm.appgate.model.Country;

public interface CountryRepository extends JpaRepository<Country, Integer>{
	
	Optional<Country> findByCountryCodeAndCountryName(String countryCode,String countryName);

}
