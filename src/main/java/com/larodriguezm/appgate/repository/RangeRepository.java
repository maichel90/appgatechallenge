package com.larodriguezm.appgate.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.larodriguezm.appgate.model.City;
import com.larodriguezm.appgate.model.RangeIp;

public interface RangeRepository extends JpaRepository<RangeIp, Integer>{

	Optional<RangeIp> findByIpfromAndIptoAndCity(String ipfrom, String ipto, City city);
	
}
