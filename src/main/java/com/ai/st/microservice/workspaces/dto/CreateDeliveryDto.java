package com.ai.st.microservice.workspaces.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "CreateDeliveryDto", description = "Create Delivery Dto")
public class CreateDeliveryDto implements Serializable {

	private static final long serialVersionUID = -2960319633239145257L;

	@ApiModelProperty(required = true, notes = "Observations")
	private String observations;

	@ApiModelProperty(required = true, notes = "Supplies")
	private List<CreateSupplyDeliveryDto> supplies;

	public CreateDeliveryDto() {
		this.supplies = new ArrayList<CreateSupplyDeliveryDto>();
	}

	public String getObservations() {
		return observations;
	}

	public void setObservations(String observations) {
		this.observations = observations;
	}

	public List<CreateSupplyDeliveryDto> getSupplies() {
		return supplies;
	}

	public void setSupplies(List<CreateSupplyDeliveryDto> supplies) {
		this.supplies = supplies;
	}

}
