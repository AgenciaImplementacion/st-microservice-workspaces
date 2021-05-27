package com.ai.st.microservice.workspaces.controllers.v1;

import com.ai.st.microservice.common.business.AdministrationBusiness;
import com.ai.st.microservice.common.dto.administration.MicroserviceUserDto;
import com.ai.st.microservice.common.dto.managers.MicroserviceManagerDto;
import com.ai.st.microservice.common.dto.providers.MicroserviceProviderDto;
import com.ai.st.microservice.common.exceptions.*;

import com.ai.st.microservice.workspaces.business.AdministratorMicroserviceBusiness;
import com.ai.st.microservice.workspaces.business.ManagerMicroserviceBusiness;
import com.ai.st.microservice.workspaces.business.ProviderBusiness;
import com.ai.st.microservice.workspaces.dto.AddProfileToUserDto;
import com.ai.st.microservice.workspaces.dto.BasicResponseDto;
import com.ai.st.microservice.workspaces.dto.ChangePasswordDto;
import com.ai.st.microservice.workspaces.dto.CreateUserDto;
import com.ai.st.microservice.workspaces.dto.UpdateUserDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(value = "Manage Users-Roles", tags = {"Administration"})
@RestController
@RequestMapping("api/workspaces/v1/administration")
public class AdministrationV1Controller {

    private final Logger log = LoggerFactory.getLogger(AdministrationV1Controller.class);

    private final AdministratorMicroserviceBusiness administrationMicroserviceBusiness;
    private final ManagerMicroserviceBusiness managerBusiness;
    private final ProviderBusiness providerBusiness;
    private final AdministrationBusiness administrationBusiness;

    public AdministrationV1Controller(AdministratorMicroserviceBusiness administrationBusiness,
                                      ManagerMicroserviceBusiness managerBusiness, ProviderBusiness providerBusiness,
                                      AdministrationBusiness administrationBusiness1) {
        this.administrationMicroserviceBusiness = administrationBusiness;
        this.managerBusiness = managerBusiness;
        this.providerBusiness = providerBusiness;
        this.administrationBusiness = administrationBusiness1;
    }

    @PostMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Create user")
    @ApiResponses(value = {@ApiResponse(code = 201, message = "Create user", response = MicroserviceUserDto.class),
            @ApiResponse(code = 500, message = "Error Server", response = String.class)})
    @ResponseBody
    public ResponseEntity<Object> createUser(@RequestBody CreateUserDto requestCreateUser,
                                             @RequestHeader("authorization") String headerAuthorization) {

        HttpStatus httpStatus;
        Object responseDto = null;

        try {

            // user session
            MicroserviceUserDto userDtoSession = administrationBusiness.getUserByToken(headerAuthorization);
            if (userDtoSession == null) {
                throw new DisconnectedMicroserviceException("Ha ocurrido un error consultando el usuario");
            }

            if (administrationBusiness.isSuperAdministrator(userDtoSession)) {
                responseDto = administrationMicroserviceBusiness.createUser(requestCreateUser.getFirstName(),
                        requestCreateUser.getLastName(), requestCreateUser.getEmail(), requestCreateUser.getUsername(),
                        requestCreateUser.getPassword(), null, requestCreateUser.getRoleAdministrator(), null, null);
            }

            if (administrationBusiness.isAdministrator(userDtoSession)) {

                if (requestCreateUser.getRoleProvider() != null) {
                    requestCreateUser.getRoleProvider().setFromAdministrator(true);
                }

                responseDto = administrationMicroserviceBusiness.createUserFromAdministrator(requestCreateUser.getFirstName(),
                        requestCreateUser.getLastName(), requestCreateUser.getEmail(), requestCreateUser.getUsername(),
                        requestCreateUser.getPassword(), requestCreateUser.getRoleProvider(),
                        requestCreateUser.getRoleAdministrator(), requestCreateUser.getRoleManager(),
                        requestCreateUser.getRoleOperator());
            }

            if (administrationBusiness.isManager(userDtoSession)) {

                // get manager
                MicroserviceManagerDto managerDto = managerBusiness.getManagerByUserCode(userDtoSession.getId());
                if (managerDto == null) {
                    throw new DisconnectedMicroserviceException("Ha ocurrido un error consultando el gestor.");
                }
                if (!managerBusiness.userManagerIsDirector(userDtoSession.getId())) {
                    throw new InputValidationException("El usuario no tiene permisos para crear usuarios.");
                }

                requestCreateUser.getRoleManager().setManagerId(managerDto.getId());

                responseDto = administrationMicroserviceBusiness.createUserFromManager(requestCreateUser.getFirstName(),
                        requestCreateUser.getLastName(), requestCreateUser.getEmail(), requestCreateUser.getUsername(),
                        requestCreateUser.getPassword(), requestCreateUser.getRoleManager());

            }

            if (administrationBusiness.isProvider(userDtoSession)) {

                requestCreateUser.getRoleProvider().setFromAdministrator(false);

                MicroserviceProviderDto providerDto = providerBusiness.getProviderByUserAdministrator(userDtoSession.getId());
                if (providerDto == null) {
                    throw new DisconnectedMicroserviceException("Ha ocurrido un error consultando el proveedor.");
                }
                if (!providerBusiness.userProviderIsDirector(userDtoSession.getId())) {
                    throw new InputValidationException("El usuario no tiene permisos para crear usuarios.");
                }

                requestCreateUser.getRoleProvider().setProviderId(providerDto.getId());

                responseDto = administrationMicroserviceBusiness.createUserFromProvider(requestCreateUser.getFirstName(),
                        requestCreateUser.getLastName(), requestCreateUser.getEmail(), requestCreateUser.getUsername(),
                        requestCreateUser.getPassword(), requestCreateUser.getRoleProvider());

            }

            httpStatus = HttpStatus.CREATED;

        } catch (BusinessException e) {
            log.error("Error AdministrationV1Controller@createUser#Business ---> " + e.getMessage());
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;
            responseDto = new BasicResponseDto(e.getMessage(), 2);
        } catch (Exception e) {
            log.error("Error AdministrationV1Controller@createUser#General ---> " + e.getMessage());
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            responseDto = new BasicResponseDto(e.getMessage(), 3);
        }

        return new ResponseEntity<>(responseDto, httpStatus);
    }

    @PostMapping(value = "/users/reset-password", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Change password user")
    @ApiResponses(value = {@ApiResponse(code = 201, message = "Create user", response = MicroserviceUserDto.class),
            @ApiResponse(code = 500, message = "Error Server", response = String.class)})
    @ResponseBody
    public ResponseEntity<Object> resetUserPassword(@RequestBody ChangePasswordDto requestChangePassword,
                                                    @RequestHeader("authorization") String headerAuthorization) {

        HttpStatus httpStatus;
        Object responseDto;

        try {

            // user session
            MicroserviceUserDto userDtoSession = administrationBusiness.getUserByToken(headerAuthorization);
            if (userDtoSession == null) {
                throw new DisconnectedMicroserviceException("Ha ocurrido un error consultando el usuario");
            }

            responseDto = administrationMicroserviceBusiness.changeUserPassword(userDtoSession.getId(),
                    requestChangePassword.getPassword());
            httpStatus = HttpStatus.OK;

        } catch (DisconnectedMicroserviceException e) {
            log.error("Error AdministrationV1Controller@resetUserPassword#Microservice ---> " + e.getMessage());
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            responseDto = new BasicResponseDto(e.getMessage(), 4);
        } catch (BusinessException e) {
            log.error("Error AdministrationV1Controller@resetUserPassword#Business ---> " + e.getMessage());
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;
            responseDto = new BasicResponseDto(e.getMessage(), 2);
        } catch (Exception e) {
            log.error("Error AdministrationV1Controller@resetUserPassword#General ---> " + e.getMessage());
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            responseDto = new BasicResponseDto(e.getMessage(), 3);
        }

        return new ResponseEntity<>(responseDto, httpStatus);
    }

    @PutMapping(value = "/users/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Update user")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Update user", response = MicroserviceUserDto.class),
            @ApiResponse(code = 500, message = "Error Server", response = String.class)})
    @ResponseBody
    public ResponseEntity<Object> updateUser(@PathVariable Long userId,
                                             @RequestBody UpdateUserDto requestUpdateUser,
                                             @RequestHeader("authorization") String headerAuthorization) {

        HttpStatus httpStatus;
        Object responseDto = null;

        try {

            // user session
            MicroserviceUserDto userDtoSession = administrationBusiness.getUserByToken(headerAuthorization);
            if (userDtoSession == null) {
                throw new DisconnectedMicroserviceException("Ha ocurrido un error consultando el usuario");
            }

            if (administrationBusiness.isSuperAdministrator(userDtoSession)) {
                responseDto = administrationMicroserviceBusiness.updateUserFromSuperAdmin(userId, requestUpdateUser.getFirstName(),
                        requestUpdateUser.getLastName(), requestUpdateUser.getEmail());
            }

            if (administrationBusiness.isAdministrator(userDtoSession)) {
                responseDto = administrationMicroserviceBusiness.updateUserFromAdministrator(userId,
                        requestUpdateUser.getFirstName(), requestUpdateUser.getLastName(), requestUpdateUser.getEmail());
            }

            if (administrationBusiness.isManager(userDtoSession)) {

                // get manager
                MicroserviceManagerDto managerDto = managerBusiness.getManagerByUserCode(userDtoSession.getId());
                if (managerDto == null) {
                    throw new DisconnectedMicroserviceException("Ha ocurrido un error consultando el gestor.");
                }
                if (!managerBusiness.userManagerIsDirector(userDtoSession.getId())) {
                    throw new InputValidationException("El usuario no tiene permisos para editar usuarios.");
                }

                responseDto = administrationMicroserviceBusiness.updateUserFromManager(userId, requestUpdateUser.getFirstName(),
                        requestUpdateUser.getLastName(), requestUpdateUser.getEmail(), managerDto.getId());

            }

            if (administrationBusiness.isProvider(userDtoSession)) {

                // get providers
                MicroserviceProviderDto providerDto = providerBusiness.getProviderByUserAdministrator(userDtoSession.getId());
                if (providerDto == null) {
                    throw new DisconnectedMicroserviceException("Ha ocurrido un error consultando el proveedor.");
                }
                if (!providerBusiness.userProviderIsDirector(userDtoSession.getId())) {
                    throw new InputValidationException("El usuario no tiene permisos para editar usuarios.");
                }

                responseDto = administrationMicroserviceBusiness.updateUserFromProvider(userId, requestUpdateUser.getFirstName(),
                        requestUpdateUser.getLastName(), requestUpdateUser.getEmail(), providerDto.getId());

            }

            httpStatus = HttpStatus.OK;

        } catch (DisconnectedMicroserviceException e) {
            log.error("Error AdministrationV1Controller@updateUser#Microservice ---> " + e.getMessage());
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            responseDto = new BasicResponseDto(e.getMessage(), 4);
        } catch (InputValidationException e) {
            log.error("Error AdministrationV1Controller@updateUser#Validation ---> " + e.getMessage());
            httpStatus = HttpStatus.UNAUTHORIZED;
            responseDto = new BasicResponseDto(e.getMessage(), 1);
        } catch (BusinessException e) {
            log.error("Error AdministrationV1Controller@updateUser#Business ---> " + e.getMessage());
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;
            responseDto = new BasicResponseDto(e.getMessage(), 2);
        } catch (Exception e) {
            log.error("Error AdministrationV1Controller@updateUser#General ---> " + e.getMessage());
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            responseDto = new BasicResponseDto(e.getMessage(), 3);
        }

        return new ResponseEntity<>(responseDto, httpStatus);
    }

    @PutMapping(value = "/users/{userId}/disable", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Disable user")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "User disabled", response = MicroserviceUserDto.class),
            @ApiResponse(code = 500, message = "Error Server", response = String.class)})
    @ResponseBody
    public ResponseEntity<Object> disableUser(@PathVariable Long userId,
                                              @RequestHeader("authorization") String headerAuthorization) {

        HttpStatus httpStatus;
        Object responseDto = null;

        try {

            // user session
            MicroserviceUserDto userDtoSession = administrationBusiness.getUserByToken(headerAuthorization);
            if (userDtoSession == null) {
                throw new DisconnectedMicroserviceException("Ha ocurrido un error consultando el usuario");
            }

            if (administrationBusiness.isSuperAdministrator(userDtoSession)) {
                responseDto = administrationMicroserviceBusiness.changeStatusUserFromSuperAdmin(userId, false);
            }

            if (administrationBusiness.isAdministrator(userDtoSession)) {
                responseDto = administrationMicroserviceBusiness.changeStatusUserFromAdministrator(userId, false);
            }

            if (administrationBusiness.isManager(userDtoSession)) {

                MicroserviceManagerDto managerDto = managerBusiness.getManagerByUserCode(userDtoSession.getId());
                if (managerDto == null) {
                    throw new DisconnectedMicroserviceException("Ha ocurrido un error consultando el gestor.");
                }
                if (!managerBusiness.userManagerIsDirector(userDtoSession.getId())) {
                    throw new InputValidationException("El usuario no tiene permisos para deshabilitar el soporte.");
                }

                responseDto = administrationMicroserviceBusiness.changeStatusUserFromManager(userId, false, managerDto.getId());
            }

            if (administrationBusiness.isProvider(userDtoSession)) {

                // get provider
                MicroserviceProviderDto providerDto = providerBusiness.getProviderByUserAdministrator(userDtoSession.getId());
                if (providerDto == null) {
                    throw new DisconnectedMicroserviceException("Ha ocurrido un error consultando el proveedor.");
                }
                if (!providerBusiness.userProviderIsDirector(userDtoSession.getId())) {
                    throw new InputValidationException("El usuario no tiene permisos para deshabilitar usuarios.");
                }

                responseDto = administrationMicroserviceBusiness.changeStatusUserFromProvider(userId, false, providerDto.getId());

            }

            httpStatus = HttpStatus.OK;

        } catch (DisconnectedMicroserviceException e) {
            log.error("Error AdministrationV1Controller@disableUser#Microservice ---> " + e.getMessage());
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            responseDto = new BasicResponseDto(e.getMessage(), 4);
        } catch (InputValidationException e) {
            log.error("Error AdministrationV1Controller@disableUser#Validation ---> " + e.getMessage());
            httpStatus = HttpStatus.UNAUTHORIZED;
            responseDto = new BasicResponseDto(e.getMessage(), 1);
        } catch (BusinessException e) {
            log.error("Error AdministrationV1Controller@disableUser#Business ---> " + e.getMessage());
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;
            responseDto = new BasicResponseDto(e.getMessage(), 2);
        } catch (Exception e) {
            log.error("Error AdministrationV1Controller@disableUser#General ---> " + e.getMessage());
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            responseDto = new BasicResponseDto(e.getMessage(), 3);
        }

        return new ResponseEntity<>(responseDto, httpStatus);
    }

    @PutMapping(value = "/users/{userId}/enable", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Enable user")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "User enabled", response = MicroserviceUserDto.class),
            @ApiResponse(code = 500, message = "Error Server", response = String.class)})
    @ResponseBody
    public ResponseEntity<Object> enableUser(@PathVariable Long userId,
                                             @RequestHeader("authorization") String headerAuthorization) {

        HttpStatus httpStatus;
        Object responseDto = null;

        try {

            // user session
            MicroserviceUserDto userDtoSession = administrationBusiness.getUserByToken(headerAuthorization);
            if (userDtoSession == null) {
                throw new DisconnectedMicroserviceException("Ha ocurrido un error consultando el usuario");
            }

            if (administrationBusiness.isSuperAdministrator(userDtoSession)) {
                responseDto = administrationMicroserviceBusiness.changeStatusUserFromSuperAdmin(userId, true);
            }

            if (administrationBusiness.isAdministrator(userDtoSession)) {
                responseDto = administrationMicroserviceBusiness.changeStatusUserFromAdministrator(userId, true);
            }

            if (administrationBusiness.isManager(userDtoSession)) {

                // get manager
                MicroserviceManagerDto managerDto = managerBusiness.getManagerByUserCode(userDtoSession.getId());
                if (managerDto == null) {
                    throw new DisconnectedMicroserviceException("Ha ocurrido un error consultando el gestor.");
                }
                if (!managerBusiness.userManagerIsDirector(userDtoSession.getId())) {
                    throw new InputValidationException("El usuario no tiene permisos para habilitar usuarios.");
                }

                responseDto = administrationMicroserviceBusiness.changeStatusUserFromManager(userId, true, managerDto.getId());
            }

            if (administrationBusiness.isProvider(userDtoSession)) {

                // get provider
                MicroserviceProviderDto providerDto = providerBusiness.getProviderByUserAdministrator(userDtoSession.getId());
                if (providerDto == null) {
                    throw new DisconnectedMicroserviceException("Ha ocurrido un error consultando el proveedor.");
                }
                if (!providerBusiness.userProviderIsDirector(userDtoSession.getId())) {
                    throw new InputValidationException("El usuario no tiene permisos para habilitar usuarios.");
                }

                responseDto = administrationMicroserviceBusiness.changeStatusUserFromProvider(userId, true, providerDto.getId());

            }

            httpStatus = HttpStatus.OK;

        } catch (DisconnectedMicroserviceException e) {
            log.error("Error AdministrationV1Controller@enableUser#Microservice ---> " + e.getMessage());
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            responseDto = new BasicResponseDto(e.getMessage(), 4);
        } catch (InputValidationException e) {
            log.error("Error AdministrationV1Controller@enableUser#Validation ---> " + e.getMessage());
            httpStatus = HttpStatus.UNAUTHORIZED;
            responseDto = new BasicResponseDto(e.getMessage(), 1);
        } catch (BusinessException e) {
            log.error("Error AdministrationV1Controller@enableUser#Business ---> " + e.getMessage());
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;
            responseDto = new BasicResponseDto(e.getMessage(), 2);
        } catch (Exception e) {
            log.error("Error AdministrationV1Controller@enableUser#General ---> " + e.getMessage());
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            responseDto = new BasicResponseDto(e.getMessage(), 3);
        }

        return new ResponseEntity<>(responseDto, httpStatus);
    }

    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get users")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Get users", response = MicroserviceUserDto.class),
            @ApiResponse(code = 500, message = "Error Server", response = String.class)})
    @ResponseBody
    public ResponseEntity<Object> getUsers(@RequestHeader("authorization") String headerAuthorization) {

        HttpStatus httpStatus;
        Object responseDto = null;

        try {

            // user session
            MicroserviceUserDto userDtoSession = administrationBusiness.getUserByToken(headerAuthorization);
            if (userDtoSession == null) {
                throw new DisconnectedMicroserviceException("Ha ocurrido un error consultando el usuario");
            }

            if (administrationBusiness.isSuperAdministrator(userDtoSession)) {
                responseDto = administrationMicroserviceBusiness.getUsersFromSuperAdmin();
            }

            if (administrationBusiness.isAdministrator(userDtoSession)) {
                responseDto = administrationMicroserviceBusiness.getUsersFromAdministrator();
            }

            if (administrationBusiness.isManager(userDtoSession)) {

                // get manager
                MicroserviceManagerDto managerDto = managerBusiness.getManagerByUserCode(userDtoSession.getId());
                if (managerDto == null) {
                    throw new DisconnectedMicroserviceException("Ha ocurrido un error consultando el gestor.");
                }
                if (!managerBusiness.userManagerIsDirector(userDtoSession.getId())) {
                    throw new InputValidationException("El usuario no tiene permisos para consultar usuarios.");
                }

                responseDto = administrationMicroserviceBusiness.getUsersFromManager(managerDto.getId());

            }

            if (administrationBusiness.isProvider(userDtoSession)) {

                // get provider
                MicroserviceProviderDto providerDto = providerBusiness.getProviderByUserAdministrator(userDtoSession.getId());
                if (providerDto == null) {
                    throw new DisconnectedMicroserviceException("Ha ocurrido un error consultando el proveedor.");
                }
                if (!providerBusiness.userProviderIsDirector(userDtoSession.getId())) {
                    throw new InputValidationException("El usuario no tiene permisos para consultar usuarios.");
                }

                responseDto = administrationMicroserviceBusiness.getUsersFromProvider(providerDto.getId());

            }

            httpStatus = HttpStatus.OK;

        } catch (DisconnectedMicroserviceException e) {
            log.error("Error AdministrationV1Controller@getUsers#Microservice ---> " + e.getMessage());
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            responseDto = new BasicResponseDto(e.getMessage(), 4);
        } catch (InputValidationException e) {
            log.error("Error AdministrationV1Controller@getUsers#Validation ---> " + e.getMessage());
            httpStatus = HttpStatus.UNAUTHORIZED;
            responseDto = new BasicResponseDto(e.getMessage(), 1);
        } catch (BusinessException e) {
            log.error("Error AdministrationV1Controller@getUsers#Business ---> " + e.getMessage());
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;
            responseDto = new BasicResponseDto(e.getMessage(), 2);
        } catch (Exception e) {
            log.error("Error AdministrationV1Controller@getUsers#General ---> " + e.getMessage());
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            responseDto = new BasicResponseDto(e.getMessage(), 3);
        }

        return new ResponseEntity<>(responseDto, httpStatus);
    }

    @PostMapping(value = "/users/{userId}/profiles", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Add profile to user")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Profile Added", response = MicroserviceUserDto.class),
            @ApiResponse(code = 500, message = "Error Server", response = String.class)})
    @ResponseBody
    public ResponseEntity<Object> addProfileToUser(@RequestHeader("authorization") String headerAuthorization,
                                                   @RequestBody AddProfileToUserDto addProfileUser, @PathVariable Long userId) {

        HttpStatus httpStatus;
        Object responseDto = null;

        try {

            // user session
            MicroserviceUserDto userDtoSession = administrationBusiness.getUserByToken(headerAuthorization);
            if (userDtoSession == null) {
                throw new DisconnectedMicroserviceException("Ha ocurrido un error consultando el usuario");
            }

            if (administrationBusiness.isManager(userDtoSession)) {

                // get manager
                MicroserviceManagerDto managerDto = managerBusiness.getManagerByUserCode(userDtoSession.getId());
                if (managerDto == null) {
                    throw new DisconnectedMicroserviceException("Ha ocurrido un error consultando el gestor.");
                }
                if (!managerBusiness.userManagerIsDirector(userDtoSession.getId())) {
                    throw new InputValidationException("El usuario no tiene permisos para agregar perfiles a los usuarios.");
                }

                responseDto = administrationMicroserviceBusiness.addProfileToUserFromManager(userId, addProfileUser.getProfileId(),
                        managerDto.getId());

            }

            if (administrationBusiness.isProvider(userDtoSession)) {

                // get provider
                MicroserviceProviderDto providerDto = providerBusiness.getProviderByUserAdministrator(userDtoSession.getId());
                if (providerDto == null) {
                    throw new DisconnectedMicroserviceException("Ha ocurrido un error consultando el proveedor.");
                }
                if (!providerBusiness.userProviderIsDirector(userDtoSession.getId())) {
                    throw new InputValidationException("El usuario no tiene permisos para agregar perfiles a los usuarios.");
                }

                responseDto = administrationMicroserviceBusiness.addProfileToUserFromProvider(userId, addProfileUser.getProfileId(),
                        providerDto.getId());

            }

            httpStatus = HttpStatus.OK;

        } catch (DisconnectedMicroserviceException e) {
            log.error("Error AdministrationV1Controller@addProfileToUser#Microservice ---> " + e.getMessage());
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            responseDto = new BasicResponseDto(e.getMessage(), 4);
        } catch (InputValidationException e) {
            log.error("Error AdministrationV1Controller@addProfileToUser#Validation ---> " + e.getMessage());
            httpStatus = HttpStatus.UNAUTHORIZED;
            responseDto = new BasicResponseDto(e.getMessage(), 1);
        } catch (BusinessException e) {
            log.error("Error AdministrationV1Controller@addProfileToUser#Business ---> " + e.getMessage());
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;
            responseDto = new BasicResponseDto(e.getMessage(), 2);
        } catch (Exception e) {
            log.error("Error AdministrationV1Controller@addProfileToUser#General ---> " + e.getMessage());
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            responseDto = new BasicResponseDto(e.getMessage(), 3);
        }

        return new ResponseEntity<>(responseDto, httpStatus);
    }

    @DeleteMapping(value = "/users/{userId}/profiles", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Remove profile to user")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Profile Added", response = MicroserviceUserDto.class),
            @ApiResponse(code = 500, message = "Error Server", response = String.class)})
    @ResponseBody
    public ResponseEntity<Object> removeProfileToUser(@RequestHeader("authorization") String headerAuthorization,
                                                      @RequestBody AddProfileToUserDto addProfileUser, @PathVariable Long userId) {

        HttpStatus httpStatus;
        Object responseDto = null;

        try {

            // user session
            MicroserviceUserDto userDtoSession = administrationBusiness.getUserByToken(headerAuthorization);
            if (userDtoSession == null) {
                throw new DisconnectedMicroserviceException("Ha ocurrido un error consultando el usuario");
            }

            if (administrationBusiness.isManager(userDtoSession)) {

                // get manager
                MicroserviceManagerDto managerDto = managerBusiness.getManagerByUserCode(userDtoSession.getId());
                if (managerDto == null) {
                    throw new DisconnectedMicroserviceException("Ha ocurrido un error consultando el gestor.");
                }
                if (!managerBusiness.userManagerIsDirector(userDtoSession.getId())) {
                    throw new InputValidationException("El usuario no tiene permisos para remover perfiles a los usuarios.");
                }

                responseDto = administrationMicroserviceBusiness.removeProfileToUserFromManager(userId,
                        addProfileUser.getProfileId(), managerDto.getId());

            }

            if (administrationBusiness.isProvider(userDtoSession)) {

                // get provider
                MicroserviceProviderDto providerDto = providerBusiness.getProviderByUserAdministrator(userDtoSession.getId());
                if (providerDto == null) {
                    throw new DisconnectedMicroserviceException("Ha ocurrido un error consultando el proveedor.");
                }
                if (!providerBusiness.userProviderIsDirector(userDtoSession.getId())) {
                    throw new InputValidationException("El usuario no tiene permisos para remover perfiles a los usuarios.");
                }

                responseDto = administrationMicroserviceBusiness.removeProfileToUserFromProvider(userId,
                        addProfileUser.getProfileId(), providerDto.getId());

            }

            httpStatus = HttpStatus.OK;

        } catch (DisconnectedMicroserviceException e) {
            log.error("Error AdministrationV1Controller@removeProfileToUser#Microservice ---> " + e.getMessage());
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            responseDto = new BasicResponseDto(e.getMessage(), 4);
        } catch (InputValidationException e) {
            log.error("Error AdministrationV1Controller@removeProfileToUser#Validation ---> " + e.getMessage());
            httpStatus = HttpStatus.UNAUTHORIZED;
            responseDto = new BasicResponseDto(e.getMessage(), 1);
        } catch (BusinessException e) {
            log.error("Error AdministrationV1Controller@removeProfileToUser#Business ---> " + e.getMessage());
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;
            responseDto = new BasicResponseDto(e.getMessage(), 2);
        } catch (Exception e) {
            log.error("Error AdministrationV1Controller@removeProfileToUser#General ---> " + e.getMessage());
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            responseDto = new BasicResponseDto(e.getMessage(), 3);
        }

        return new ResponseEntity<>(responseDto, httpStatus);
    }

}
