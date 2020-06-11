package com.ai.st.microservice.workspaces.business;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ai.st.microservice.workspaces.dto.DepartmentDto;
import com.ai.st.microservice.workspaces.dto.MunicipalityDto;
import com.ai.st.microservice.workspaces.entities.DepartmentEntity;
import com.ai.st.microservice.workspaces.entities.MunicipalityEntity;
import com.ai.st.microservice.workspaces.exceptions.BusinessException;
import com.ai.st.microservice.workspaces.services.IMunicipalityService;

@Component
public class MunicipalityBusiness {

	@Autowired
	private IMunicipalityService municipalityService;

	public List<MunicipalityDto> getMunicipalitiesByDepartmentId(Long departmentId) throws BusinessException {

		List<MunicipalityDto> listMunicipalitiesDto = new ArrayList<MunicipalityDto>();

		List<MunicipalityEntity> listMunicipalitiesEntity = municipalityService
				.getMunicipalitiesByDepartmentId(departmentId);

		for (MunicipalityEntity municipalityEntity : listMunicipalitiesEntity) {

			MunicipalityDto municipalityDto = new MunicipalityDto();
			municipalityDto.setId(municipalityEntity.getId());
			municipalityDto.setName(municipalityEntity.getName());
			municipalityDto.setCode(municipalityEntity.getCode());
			municipalityDto.setDepartment(new DepartmentDto(municipalityEntity.getDepartment().getId(),
					municipalityEntity.getDepartment().getName(), municipalityEntity.getDepartment().getCode()));

			listMunicipalitiesDto.add(municipalityDto);
		}

		return listMunicipalitiesDto;
	}

	public List<MunicipalityDto> getMunicipalitiesByDepartmentIdAndManager(Long departmentId, Long managerCode)
			throws BusinessException {

		List<MunicipalityDto> listMunicipalitiesDto = new ArrayList<MunicipalityDto>();

		List<MunicipalityEntity> listMunicipalitiesEntity = municipalityService
				.getMunicipalitiesByDepartmentIdAndManagerCode(departmentId, managerCode);

		for (MunicipalityEntity municipalityEntity : listMunicipalitiesEntity) {

			MunicipalityDto municipalityDto = new MunicipalityDto();
			municipalityDto.setId(municipalityEntity.getId());
			municipalityDto.setName(municipalityEntity.getName());
			municipalityDto.setCode(municipalityEntity.getCode());
			municipalityDto.setDepartment(new DepartmentDto(municipalityEntity.getDepartment().getId(),
					municipalityEntity.getDepartment().getName(), municipalityEntity.getDepartment().getCode()));

			listMunicipalitiesDto.add(municipalityDto);
		}

		return listMunicipalitiesDto;
	}

	public MunicipalityDto getMunicipalityByCode(String code) {

		MunicipalityDto municipalityDto = null;

		MunicipalityEntity municipalityEntity = municipalityService.getMunicipalityByCode(code);
		if (municipalityEntity instanceof MunicipalityEntity) {
			municipalityDto = new MunicipalityDto();
			municipalityDto.setCode(municipalityEntity.getCode());
			municipalityDto.setId(municipalityEntity.getId());
			municipalityDto.setName(municipalityEntity.getName());

			DepartmentEntity departmentEntity = municipalityEntity.getDepartment();
			municipalityDto.setDepartment(new DepartmentDto(departmentEntity.getId(), departmentEntity.getName(),
					departmentEntity.getCode()));

		}

		return municipalityDto;
	}

}
