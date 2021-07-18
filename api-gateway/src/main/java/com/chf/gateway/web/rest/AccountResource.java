package com.chf.gateway.web.rest;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.chf.core.constants.ErrorCodeContants;
import com.chf.core.exception.ServiceException;
import com.chf.core.security.reactive.SecurityUtils;
import com.chf.gateway.service.UserService;
import com.chf.user.core.service.dto.AdminUserDTO;
import com.chf.user.core.service.dto.PasswordChangeDTO;
import com.chf.user.core.web.vm.ManagedUserVM;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class AccountResource {

    private final Logger log = LoggerFactory.getLogger(AccountResource.class);

    private final UserService userService;

    public AccountResource(UserService userService) {
        super();
        this.userService = userService;
    }

    @GetMapping("/authenticate")
    public Mono<String> isAuthenticated(ServerWebExchange request) {
        log.debug("REST request to check if the current user is authenticated");
        return request.getPrincipal().map(Principal::getName);
    }

    @GetMapping("/account")
    public Mono<AdminUserDTO> getAccount() {
        return userService.getUserWithAuthorities().map(AdminUserDTO::new).switchIfEmpty(
                Mono.error(new ServiceException(ErrorCodeContants.LACK_OF_DATA, "User could not be found")));
    }

    @PostMapping("/account/change-password")
    public Mono<Void> changePassword(@RequestBody PasswordChangeDTO passwordChangeDTO) {
        if (!ManagedUserVM.checkPasswordLength(passwordChangeDTO.getNewPassword())) {
            throw new ServiceException(ErrorCodeContants.INVALID_PASSWORD, "Password is short.");
        }
        return userService.changePassword(SecurityUtils.getCurrentUserLogin().block(),
                passwordChangeDTO.getCurrentPassword(), passwordChangeDTO.getNewPassword());
    }

}
