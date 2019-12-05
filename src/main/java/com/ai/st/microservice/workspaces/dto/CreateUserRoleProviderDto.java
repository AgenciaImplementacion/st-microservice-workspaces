package com.ai.st.microservice.workspaces.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "CreateUserRoleProviderDto", description = "Create User Role Provider Dto")
public class CreateUserRoleProviderDto implements Serializable {

	private static final long serialVersionUID = -6386496439475467133L;

	@ApiModelProperty(required = true, notes = "Role ID")
	private Long roleId;

	@ApiModelProperty(required = true, notes = "Provider ID")
	private Long providerId;

	@ApiModelProperty(required = true, notes = "Profiles ID")
	private List<Long> profiles;

	public CreateUserRoleProviderDto() {
		this.profiles = new ArrayList<Long>();
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public Long getProviderId() {
		return providerId;
	}

	public void setProviderId(Long providerId) {
		this.providerId = providerId;
	}

	public List<Long> getProfiles() {
		return profiles;
	}

	public void setProfiles(List<Long> profiles) {
		this.profiles = profiles;
	}

}
