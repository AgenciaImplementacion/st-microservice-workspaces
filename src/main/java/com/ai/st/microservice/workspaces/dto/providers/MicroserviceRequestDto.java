package com.ai.st.microservice.workspaces.dto.providers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ai.st.microservice.workspaces.dto.MunicipalityDto;
import com.ai.st.microservice.workspaces.dto.administration.MicroserviceUserDto;

public class MicroserviceRequestDto implements Serializable {

	private static final long serialVersionUID = 18861947628131335L;

	private Long id;
	private Date createdAt;
	private Date deadline;
	private String observations;
	private MicroserviceRequestStateDto requestState;
	private List<MicroserviceSupplyRequestedDto> suppliesRequested;
	private List<MicroserviceEmitterDto> emitters;
	private MicroserviceProviderDto provider;
	private String municipalityCode;
	private MunicipalityDto municipality;
	private String packageLabel;
	private Date closedAt;
	private Long closedBy;
	private MicroserviceUserDto userClosedBy;

	public MicroserviceRequestDto() {
		suppliesRequested = new ArrayList<MicroserviceSupplyRequestedDto>();
		emitters = new ArrayList<MicroserviceEmitterDto>();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getDeadline() {
		return deadline;
	}

	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}

	public String getObservations() {
		return observations;
	}

	public void setObservations(String observations) {
		this.observations = observations;
	}

	public MicroserviceRequestStateDto getRequestState() {
		return requestState;
	}

	public void setRequestState(MicroserviceRequestStateDto requestState) {
		this.requestState = requestState;
	}

	public List<MicroserviceSupplyRequestedDto> getSuppliesRequested() {
		return suppliesRequested;
	}

	public void setSuppliesRequested(List<MicroserviceSupplyRequestedDto> suppliesRequested) {
		this.suppliesRequested = suppliesRequested;
	}

	public List<MicroserviceEmitterDto> getEmitters() {
		return emitters;
	}

	public void setEmitters(List<MicroserviceEmitterDto> emitters) {
		this.emitters = emitters;
	}

	public MicroserviceProviderDto getProvider() {
		return provider;
	}

	public void setProvider(MicroserviceProviderDto provider) {
		this.provider = provider;
	}

	public String getMunicipalityCode() {
		return municipalityCode;
	}

	public void setMunicipalityCode(String municipalityCode) {
		this.municipalityCode = municipalityCode;
	}

	public MunicipalityDto getMunicipality() {
		return municipality;
	}

	public void setMunicipality(MunicipalityDto municipality) {
		this.municipality = municipality;
	}

	public String getPackageLabel() {
		return packageLabel;
	}

	public void setPackageLabel(String packageLabel) {
		this.packageLabel = packageLabel;
	}

	public Date getClosedAt() {
		return closedAt;
	}

	public void setClosedAt(Date closedAt) {
		this.closedAt = closedAt;
	}

	public Long getClosedBy() {
		return closedBy;
	}

	public void setClosedBy(Long closedBy) {
		this.closedBy = closedBy;
	}

	public MicroserviceUserDto getUserClosedBy() {
		return userClosedBy;
	}

	public void setUserClosedBy(MicroserviceUserDto userClosedBy) {
		this.userClosedBy = userClosedBy;
	}

}
