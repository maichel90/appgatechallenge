package com.larodriguezm.appgate.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.larodriguezm.appgate.model.Region;

public interface RegionRepository extends JpaRepository<Region, Integer>{
	
	Optional<Region> findByRegionName(String regionName);

}
