package com.larodriguezm.appgate.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.larodriguezm.appgate.model.City;
import com.larodriguezm.appgate.model.RangeIp;

public interface RangeRepository extends JpaRepository<RangeIp, Integer>{

	Optional<RangeIp> findByIpfromAndIptoAndCity(String ipfrom, String ipto, City city);
	
	@Query(nativeQuery = true, value = "select * from range_ip r where r.ipfrom <= ?1 AND r.ipto >= ?2")
	Optional<List<RangeIp>> findByDecimal(Integer ipAddressDecimal,Integer ipAddressDecimal2);
	
}
