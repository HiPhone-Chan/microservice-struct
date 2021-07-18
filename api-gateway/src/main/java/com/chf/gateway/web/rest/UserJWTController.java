package com.chf.gateway.web.rest;

import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chf.core.security.jwt.JWTToken;
import com.chf.core.security.jwt.TokenProvider;
import com.chf.core.security.jwt.reactive.JWTFilter;
import com.chf.user.core.web.vm.LoginVM;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class UserJWTController {

    public final static int EXPIRE_TIME = 2; // min

    private final TokenProvider tokenProvider;

    private final ReactiveAuthenticationManager authenticationManager;

    public UserJWTController(TokenProvider tokenProvider, ReactiveAuthenticationManager authenticationManager) {
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/authenticate")
    public Mono<ResponseEntity<JWTToken>> authorize(@Valid @RequestBody Mono<LoginVM> loginVM) {
        return loginVM
            .flatMap(
                login ->
                    authenticationManager
                        .authenticate(new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword()))
                        .flatMap(auth -> Mono.fromCallable(() -> tokenProvider.createToken(auth, login.isRememberMe())))
            )
            .map(
                jwt -> {
                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);
                    return new ResponseEntity<>(new JWTToken(jwt), httpHeaders, HttpStatus.OK);
                }
            );
    }

}
