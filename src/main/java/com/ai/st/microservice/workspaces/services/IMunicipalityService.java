package com.ai.st.microservice.workspaces.services;

import java.util.List;

import com.ai.st.microservice.workspaces.entities.MunicipalityEntity;

public interface IMunicipalityService {

	public Long getCount();

	public MunicipalityEntity createMunicipality(MunicipalityEntity municipalityEntity);

	public List<MunicipalityEntity> getMunicipalitiesByDepartmentId(Long departmentId);
	
	public MunicipalityEntity getMunicipalityById(Long id);

}
