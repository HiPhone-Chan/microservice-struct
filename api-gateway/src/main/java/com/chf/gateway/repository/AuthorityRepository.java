package com.chf.gateway.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;

import com.chf.user.core.domain.Authority;

public interface AuthorityRepository extends R2dbcRepository<Authority, String> {
}
