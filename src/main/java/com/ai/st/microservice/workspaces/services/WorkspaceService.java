package com.ai.st.microservice.workspaces.services;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ai.st.microservice.workspaces.entities.MunicipalityEntity;
import com.ai.st.microservice.workspaces.entities.WorkspaceEntity;
import com.ai.st.microservice.workspaces.repositories.WorkspaceRepository;

@Service
public class WorkspaceService implements IWorkspaceService {

	@Autowired
	private WorkspaceRepository workspaceRepository;

	@Override
	@Transactional
	public WorkspaceEntity createWorkspace(WorkspaceEntity workspaceEntity) {
		return workspaceRepository.save(workspaceEntity);
	}

	@Override
	public Long getCountByMunicipality(MunicipalityEntity municipalityEntity) {
		return workspaceRepository.countByMunicipality(municipalityEntity);
	}

	@Override
	public List<WorkspaceEntity> getWorkspacesByMunicipality(MunicipalityEntity municipalityEntity) {
		return workspaceRepository.findByMunicipality(municipalityEntity);
	}

	@Override
	public WorkspaceEntity getWorkspaceActiveByMunicipality(MunicipalityEntity municipalityEntity) {
		return workspaceRepository.findByIsActiveAndMunicipality(true, municipalityEntity);
	}

}
