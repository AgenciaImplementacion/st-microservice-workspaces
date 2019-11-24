package com.ai.st.microservice.workspaces.dto.providers;

import java.io.Serializable;
import java.util.Date;

public class MicroserviceTypeSupplyDto implements Serializable {

	private static final long serialVersionUID = 3977770440374512592L;

	private Long id;
	private Date createdAt;
	private String description;
	private Boolean metadataRequired;
	private String name;
	private MicroserviceProviderProfileDto providerProfile;

	public MicroserviceTypeSupplyDto() {

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getMetadataRequired() {
		return metadataRequired;
	}

	public void setMetadataRequired(Boolean metadataRequired) {
		this.metadataRequired = metadataRequired;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public MicroserviceProviderProfileDto getProviderProfile() {
		return providerProfile;
	}

	public void setProviderProfile(MicroserviceProviderProfileDto providerProfile) {
		this.providerProfile = providerProfile;
	}

}
