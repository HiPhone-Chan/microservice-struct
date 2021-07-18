package com.chf.gateway.web.rest;

import static com.chf.core.constants.AuthoritiesConstants.ADMIN;
import static com.chf.core.constants.AuthoritiesConstants.MANAGER;

import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import com.chf.core.constants.ErrorCodeContants;
import com.chf.core.constants.SystemConstants;
import com.chf.core.exception.ServiceException;
import com.chf.core.web.utils.PaginationUtil;
import com.chf.gateway.repository.UserRepository;
import com.chf.gateway.service.UserService;
import com.chf.user.core.domain.User;
import com.chf.user.core.service.dto.AdminUserDTO;
import com.chf.user.core.service.dto.PasswordChangeDTO;
import com.chf.user.core.web.vm.ManagedUserVM;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/admin")
public class UserResource {

    private final Logger log = LoggerFactory.getLogger(UserResource.class);

    private final UserRepository userRepository;

    private final UserService userService;

    public UserResource(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping("/user")
    @Secured(ADMIN)
    public Mono<User> createUser(@Valid @RequestBody AdminUserDTO userDTO) throws URISyntaxException {
        log.debug("REST request to save User : {}", userDTO);

        if (userDTO.getId() != null) {
            throw new ServiceException(ErrorCodeContants.BAD_PARAMETERS, "A new user cannot already have an ID");
        }

        return userRepository.findOneByLogin(userDTO.getLogin().toLowerCase()).hasElement().flatMap(loginExists -> {
            if (Boolean.TRUE.equals(loginExists)) {
                return Mono.error(new ServiceException(ErrorCodeContants.LOGIN_ALREADY_USED));
            }
            return userService.createUser(userDTO);
        });

    }

    @PutMapping("/user")
    @Secured(ADMIN)
    public Mono<ResponseEntity<AdminUserDTO>> updateUser(@Valid @RequestBody AdminUserDTO userDTO) {
        log.debug("REST request to update User : {}", userDTO);
        return userRepository.findOneByLogin(userDTO.getLogin().toLowerCase())
                .filter(user -> !user.getId().equals(userDTO.getId())).hasElement().flatMap(loginExists -> {
                    if (Boolean.TRUE.equals(loginExists)) {
                        return Mono.error(
                                new ServiceException(ErrorCodeContants.LOGIN_ALREADY_USED, "Login name already used!"));
                    }
                    return userService.updateUser(userDTO);
                }).switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(user -> ResponseEntity.ok().body(user));
    }

    @PostMapping("/user/change-password/{login:" + SystemConstants.LOGIN_REGEX + "}")
    @Secured(ADMIN)
    public void changePassword(@PathVariable String login, @RequestBody PasswordChangeDTO passwordChangeDTO) {
        if (!ManagedUserVM.checkPasswordLength(passwordChangeDTO.getNewPassword())) {
            throw new ServiceException(ErrorCodeContants.INVALID_PASSWORD, "Password is short.");
        }
        userService.changePassword(login, passwordChangeDTO.getCurrentPassword(), passwordChangeDTO.getNewPassword());
    }

    @GetMapping("/user/authorities")
    @Secured(ADMIN)
    public Flux<String> getAuthorities() {
        return userService.getAuthorities();
    }

    @GetMapping("/users")
    @Secured(ADMIN)
    public Mono<ResponseEntity<Flux<AdminUserDTO>>> getAllUsers(ServerHttpRequest request, Pageable pageable,
            @RequestParam(name = "authority", required = false) String authority) {
        return userService.countManagedUsers().map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
                .map(page -> PaginationUtil.generatePaginationHttpHeaders(UriComponentsBuilder.fromHttpRequest(request),
                        page))
                .map(headers -> ResponseEntity.ok().headers(headers).body(userService.getAllManagedUsers(pageable)));
    }

    @GetMapping("/users/{login:" + SystemConstants.LOGIN_REGEX + "}")
    @Secured(ADMIN)
    public Mono<AdminUserDTO> getUser(@PathVariable String login) {
        log.debug("REST request to get User : {}", login);
        return userService.getUserWithAuthoritiesByLogin(login).map(AdminUserDTO::new)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @DeleteMapping("/user/{login:" + SystemConstants.LOGIN_REGEX + "}")
    @Secured(ADMIN)
    public Mono<Void> deleteUser(@PathVariable String login) {
        log.debug("REST request to delete User: {}", login);
        return userService.deleteUser(login);
    }

//    @GetMapping("/openapi/user/check/{login:" + SystemConstants.LOGIN_REGEX + "}")
//    @Secured({ ADMIN, MANAGER })
//    public boolean checkLogin(@PathVariable String login) {
//        return userRepository.existsByLogin(login);
//    }
//
//    @GetMapping("/openapi/user/check-mobile/{mobile}")
//    @Secured({ ADMIN, MANAGER })
//    public boolean checkMobile(@PathVariable String mobile, @RequestParam(required = false) Long id) {
//        if (id == null) {
//            return userRepository.existsByMobile(mobile);
//        }
//        return userRepository.existsByMobileAndIdNot(mobile, id);
//    }
}
