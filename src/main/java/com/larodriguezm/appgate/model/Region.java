package com.larodriguezm.appgate.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Entity
public class Region {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer regionId;
	
	private String regionName;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "countryId", nullable = false)
	private Country country;

}
