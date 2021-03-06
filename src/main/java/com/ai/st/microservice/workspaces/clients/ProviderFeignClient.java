package com.ai.st.microservice.workspaces.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.ai.st.microservice.workspaces.dto.providers.MicroserviceAddAdministratorToProviderDto;
import com.ai.st.microservice.workspaces.dto.providers.MicroserviceAddUserToProviderDto;
import com.ai.st.microservice.workspaces.dto.providers.MicroserviceCreatePetitionDto;
import com.ai.st.microservice.workspaces.dto.providers.MicroserviceCreateProviderProfileDto;
import com.ai.st.microservice.workspaces.dto.providers.MicroserviceCreateRequestDto;
import com.ai.st.microservice.workspaces.dto.providers.MicroserviceCreateSupplyRevisionDto;
import com.ai.st.microservice.workspaces.dto.providers.MicroserviceCreateTypeSupplyDto;
import com.ai.st.microservice.workspaces.dto.providers.MicroservicePetitionDto;
import com.ai.st.microservice.workspaces.dto.providers.MicroserviceProviderAdministratorDto;
import com.ai.st.microservice.workspaces.dto.providers.MicroserviceProviderDto;
import com.ai.st.microservice.workspaces.dto.providers.MicroserviceProviderProfileDto;
import com.ai.st.microservice.workspaces.dto.providers.MicroserviceProviderUserDto;
import com.ai.st.microservice.workspaces.dto.providers.MicroserviceRequestDto;
import com.ai.st.microservice.workspaces.dto.providers.MicroserviceRequestPaginatedDto;
import com.ai.st.microservice.workspaces.dto.providers.MicroserviceRoleDto;
import com.ai.st.microservice.workspaces.dto.providers.MicroserviceSupplyRequestedDto;
import com.ai.st.microservice.workspaces.dto.providers.MicroserviceSupplyRevisionDto;
import com.ai.st.microservice.workspaces.dto.providers.MicroserviceTypeSupplyDto;
import com.ai.st.microservice.workspaces.dto.providers.MicroserviceUpdatePetitionDto;
import com.ai.st.microservice.workspaces.dto.providers.MicroserviceUpdateSupplyRequestedDto;
import com.ai.st.microservice.workspaces.dto.providers.MicroserviceUpdateSupplyRevisionDto;
import com.ai.st.microservice.workspaces.exceptions.BusinessException;

import feign.Feign;
import feign.codec.Encoder;

import feign.form.spring.SpringFormEncoder;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;

@FeignClient(name = "st-microservice-providers", configuration = ProviderFeignClient.Configuration.class)
public interface ProviderFeignClient {

	@RequestMapping(method = RequestMethod.POST, value = "/api/providers-supplies/v1/requests", consumes = APPLICATION_JSON_VALUE)
	public MicroserviceRequestDto createRequest(@RequestBody MicroserviceCreateRequestDto request)
			throws BusinessException;

	@GetMapping("/api/providers-supplies/v1/users/{userCode}/providers")
	public MicroserviceProviderDto findByUserCode(@PathVariable Long userCode);

	@GetMapping("/api/providers-supplies/v1/providers/{providerId}")
	public MicroserviceProviderDto findById(@PathVariable(name = "providerId", required = true) Long providerId);

	@GetMapping("/api/providers-supplies/v1/providers/{providerId}/requests")
	public List<MicroserviceRequestDto> getRequestsByProvider(@PathVariable Long providerId,
			@RequestParam(required = false, name = "state") Long requestStateId);

	@GetMapping("/api/providers-supplies/v1/providers/{providerId}/requests/closed")
	public List<MicroserviceRequestDto> getRequestsByProviderClosed(@PathVariable Long providerId,
			@RequestParam(required = false, name = "user") Long userCode);

	@GetMapping("/api/providers-supplies/v1/requests/{requestId}")
	public MicroserviceRequestDto findRequestById(@PathVariable Long requestId);

	@GetMapping("/api/providers-supplies/v1/providers/{providerId}/users")
	public List<MicroserviceProviderUserDto> findUsersByProviderId(@PathVariable Long providerId)
			throws BusinessException;

	@RequestMapping(method = RequestMethod.PUT, value = "/api/providers-supplies/v1/requests/{requestId}/supplies/{supplyRequestedId}", consumes = APPLICATION_JSON_VALUE)
	public MicroserviceRequestDto updateSupplyRequested(@PathVariable Long requestId,
			@PathVariable Long supplyRequestedId, @RequestBody MicroserviceUpdateSupplyRequestedDto updateSupply);

	@PutMapping("/api/providers-supplies/v1/requests/{requestId}/delivered")
	public MicroserviceRequestDto closeRequest(@PathVariable Long requestId,
			@RequestParam(name = "closed_by") Long userCode);

	@RequestMapping(method = RequestMethod.POST, value = "/api/providers-supplies/v1/users", consumes = APPLICATION_JSON_VALUE)
	public List<MicroserviceProviderUserDto> addUserToProvide(@RequestBody MicroserviceAddUserToProviderDto data);

	@GetMapping("/api/providers-supplies/v1/types-supplies/{typeSupplyId}")
	public MicroserviceTypeSupplyDto findTypeSuppleById(@PathVariable Long typeSupplyId);

	@GetMapping("/api/providers-supplies/v1/providers/{providerId}/users")
	public List<MicroserviceProviderUserDto> findUsersByProviderIdAndProfiles(@PathVariable Long providerId,
			@RequestParam(name = "profiles", required = false) List<Long> profiles) throws BusinessException;

	@GetMapping("/api/providers-supplies/v1/requests/emmiters")
	public List<MicroserviceRequestDto> findRequestsByEmmiters(
			@RequestParam(name = "emmiter_code", required = true) Long emmiterCode,
			@RequestParam(name = "emmiter_type", required = true) String emmiterType);

	@RequestMapping(method = RequestMethod.POST, value = "/api/providers-supplies/v1/administrators", consumes = APPLICATION_JSON_VALUE)
	public List<MicroserviceProviderAdministratorDto> addAdministratorToProvide(
			@RequestBody MicroserviceAddAdministratorToProviderDto data);

	@GetMapping("/api/providers-supplies/v1/providers/{providerId}/administrators")
	public List<MicroserviceProviderAdministratorDto> findAdministratorsByProviderId(@PathVariable Long providerId)
			throws BusinessException;

	@GetMapping("/api/providers-supplies/v1/administrators/{userCode}/roles")
	public List<MicroserviceRoleDto> findRolesByUser(@PathVariable Long userCode);

	@GetMapping("/api/providers-supplies/v1/administrators/{userCode}/providers")
	public MicroserviceProviderDto findProviderByAdministrator(@PathVariable Long userCode);

	@RequestMapping(method = RequestMethod.DELETE, value = "/api/providers-supplies/v1/users", consumes = APPLICATION_JSON_VALUE)
	public List<MicroserviceProviderUserDto> removeUserToProvider(@RequestBody MicroserviceAddUserToProviderDto data)
			throws BusinessException;

	@RequestMapping(method = RequestMethod.POST, value = "/api/providers-supplies/v1/providers/{providerId}/profiles", consumes = APPLICATION_JSON_VALUE)
	public MicroserviceProviderProfileDto createProfile(@PathVariable Long providerId,
			@RequestBody MicroserviceCreateProviderProfileDto createProviderProfileDto) throws BusinessException;

	@GetMapping("/api/providers-supplies/v1/providers/{providerId}/profiles")
	public List<MicroserviceProviderProfileDto> getProfilesByProvider(@PathVariable Long providerId)
			throws BusinessException;

	@RequestMapping(method = RequestMethod.PUT, value = "/api/providers-supplies/v1/providers/{providerId}/profiles/{profileId}", consumes = APPLICATION_JSON_VALUE)
	public MicroserviceProviderProfileDto updateProfile(@PathVariable Long providerId, @PathVariable Long profileId,
			@RequestBody MicroserviceCreateProviderProfileDto createProviderProfileDto) throws BusinessException;

	@RequestMapping(method = RequestMethod.DELETE, value = "/api/providers-supplies/v1/providers/{providerId}/profiles/{profileId}", consumes = APPLICATION_JSON_VALUE)
	public void deleteProfile(@PathVariable Long providerId, @PathVariable Long profileId) throws BusinessException;

	@RequestMapping(method = RequestMethod.POST, value = "/api/providers-supplies/v1/providers/{providerId}/type-supplies", consumes = APPLICATION_JSON_VALUE)
	public MicroserviceTypeSupplyDto createTypeSupplies(@PathVariable Long providerId,
			@RequestBody MicroserviceCreateTypeSupplyDto createTypeSupplyDto) throws BusinessException;

	@GetMapping("/api/providers-supplies/v1/providers/{providerId}/types-supplies")
	public List<MicroserviceTypeSupplyDto> getTypesSuppliesByProvider(@PathVariable Long providerId)
			throws BusinessException;

	@RequestMapping(method = RequestMethod.PUT, value = "/api/providers-supplies/v1/providers/{providerId}/type-supplies/{typeSupplyId}", consumes = APPLICATION_JSON_VALUE)
	public MicroserviceTypeSupplyDto updateTypeSupplies(@PathVariable Long providerId, @PathVariable Long typeSupplyId,
			@RequestBody MicroserviceCreateTypeSupplyDto data) throws BusinessException;

	@RequestMapping(method = RequestMethod.DELETE, value = "/api/providers-supplies/v1/providers/{providerId}/type-supplies/{typeSupplyId}", consumes = APPLICATION_JSON_VALUE)
	public void deleteTypeSupply(@PathVariable Long providerId, @PathVariable Long typeSupplyId)
			throws BusinessException;

	@GetMapping("/api/providers-supplies/v1/users/{userCode}/profiles")
	public List<MicroserviceProviderProfileDto> findProfilesByUser(@PathVariable Long userCode);

	@GetMapping("/api/providers-supplies/v1/requests/search-manager-municipality")
	public MicroserviceRequestPaginatedDto getRequestsByManagerAndMunicipality(
			@RequestParam(name = "manager", required = true) Long managerCode,
			@RequestParam(name = "municipality", required = true) String municipalityCode,
			@RequestParam(name = "page", required = true) Integer page) throws BusinessException;

	@GetMapping("/api/providers-supplies/v1/requests/search-manager-provider")
	public MicroserviceRequestPaginatedDto getRequestsByManagerAndProvider(
			@RequestParam(name = "manager", required = true) Long managerCode,
			@RequestParam(name = "provider", required = true) Long providerId,
			@RequestParam(name = "page", required = true) Integer page) throws BusinessException;

	@GetMapping("/api/providers-supplies/v1/requests/search-manager-package")
	public List<MicroserviceRequestDto> getRequestsByManagerAndPackage(
			@RequestParam(name = "manager", required = true) Long managerCode,
			@RequestParam(name = "package_label", required = true) String packageLabel) throws BusinessException;

	@GetMapping("/api/providers-supplies/v1/requests/search-package")
	public List<MicroserviceRequestDto> getRequestsByPackage(
			@RequestParam(name = "package_label", required = true) String packageLabel) throws BusinessException;

	@GetMapping("/api/providers-supplies/v1/providers/{providerId}/supplies-requested")
	public List<MicroserviceSupplyRequestedDto> getSuppliesRequestedToReview(@PathVariable Long providerId,
			@RequestParam(name = "states", required = true) List<Long> states) throws BusinessException;

	@GetMapping("/api/providers-supplies/v1/supplies-requested/{supplyRequestedId}")
	public MicroserviceSupplyRequestedDto getSupplyRequested(@PathVariable Long supplyRequestedId);

	@RequestMapping(method = RequestMethod.POST, value = "/api/providers-supplies/v1/supplies-requested/{supplyRequestedId}/revision", consumes = APPLICATION_JSON_VALUE)
	public MicroserviceSupplyRevisionDto createSupplyRevision(@PathVariable Long supplyRequestedId,
			@RequestBody MicroserviceCreateSupplyRevisionDto createRevisionDto);

	@RequestMapping(method = RequestMethod.DELETE, value = "/api/providers-supplies/v1/supplies-requested/{supplyRequestedId}/revision/{supplyRevisionId}", consumes = APPLICATION_JSON_VALUE)
	public void deleteSupplyRevision(@PathVariable Long supplyRequestedId, @PathVariable Long supplyRevisionId);

	@GetMapping("/api/providers-supplies/v1/supplies-requested/{supplyRequestedId}/revision")
	public MicroserviceSupplyRevisionDto getSupplyRevisionFromSupplyRequested(@PathVariable Long supplyRequestedId);

	@RequestMapping(method = RequestMethod.PUT, value = "/api/providers-supplies/v1/supplies-requested/{supplyRequestedId}/revision/{revisionId}", consumes = APPLICATION_JSON_VALUE)
	public MicroserviceSupplyRevisionDto updateSupplyRevision(@PathVariable Long supplyRequestedId,
			@PathVariable Long revisionId, @RequestBody MicroserviceUpdateSupplyRevisionDto createRevisionDto);

	// Petitions Module
	@RequestMapping(method = RequestMethod.POST, value = "/api/providers-supplies/v1/providers/{providerId}/petitions", consumes = APPLICATION_JSON_VALUE)
	public MicroservicePetitionDto createPetition(@PathVariable Long providerId,
			@RequestBody MicroserviceCreatePetitionDto createPetitionDto);

	@GetMapping("/api/providers-supplies/v1/providers/{providerId}/petitions-manager/{managerId}")
	public List<MicroservicePetitionDto> getPetitionsForManager(@PathVariable Long providerId,
			@PathVariable Long managerId);

	@GetMapping("/api/providers-supplies/v1/providers/petitions-manager/{managerId}")
	public List<MicroservicePetitionDto> getPetitionsByManager(@PathVariable Long managerId);

	@GetMapping("/api/providers-supplies/v1/providers/{providerId}/petitions")
	public List<MicroservicePetitionDto> getPetitionsForProvider(@PathVariable Long providerId,
			@RequestParam(name = "states", required = true) List<Long> states);

	@RequestMapping(method = RequestMethod.PUT, value = "/api/providers-supplies/v1/providers/{providerId}/petitions/{petitionId}", consumes = APPLICATION_JSON_VALUE)
	public MicroservicePetitionDto updatePetition(@PathVariable Long providerId, @PathVariable Long petitionId,
			@RequestBody MicroserviceUpdatePetitionDto updatePetitionDto) throws BusinessException;

	// Supplies Module

	@RequestMapping(method = RequestMethod.PUT, value = "/api/providers-supplies/v1/types-supplies/{typeSupplyId}/enable", consumes = APPLICATION_JSON_VALUE)
	public MicroserviceTypeSupplyDto enableTypeSupply(@PathVariable Long typeSupplyId) throws BusinessException;

	@RequestMapping(method = RequestMethod.PUT, value = "/api/providers-supplies/v1/types-supplies/{typeSupplyId}/disable", consumes = APPLICATION_JSON_VALUE)
	public MicroserviceTypeSupplyDto disableTypeSupply(@PathVariable Long typeSupplyId) throws BusinessException;

	class Configuration {

		@Bean
		Encoder feignFormEncoder(ObjectFactory<HttpMessageConverters> converters) {
			return new SpringFormEncoder(new SpringEncoder(converters));
		}

		@Bean
		@Scope("prototype")
		public Feign.Builder feignBuilder() {
			return Feign.builder();
		}

	}

}
