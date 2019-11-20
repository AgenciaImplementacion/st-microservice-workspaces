package com.ai.st.microservice.workspaces.services;

import java.util.List;

import com.ai.st.microservice.workspaces.entities.MunicipalityEntity;
import com.ai.st.microservice.workspaces.entities.WorkspaceEntity;

public interface IWorkspaceService {

	public WorkspaceEntity createWorkspace(WorkspaceEntity workspaceEntity);

	public Long getCountByMunicipality(MunicipalityEntity municipalityEntity);

	public List<WorkspaceEntity> getWorkspacesByMunicipality(MunicipalityEntity municipalityEntity);

	public WorkspaceEntity getWorkspaceActiveByMunicipality(MunicipalityEntity municipalityEntity);

}
