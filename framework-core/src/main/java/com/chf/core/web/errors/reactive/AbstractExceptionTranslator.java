package com.chf.core.web.errors.reactive;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import org.zalando.problem.DefaultProblem;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;
import org.zalando.problem.Status;
import org.zalando.problem.spring.webflux.advice.ProblemHandling;
import org.zalando.problem.spring.webflux.advice.security.SecurityAdviceTrait;
import org.zalando.problem.violations.ConstraintViolationProblem;

import com.chf.core.constants.ErrorCodeContants;
import com.chf.core.exception.ServiceException;
import com.chf.core.web.errors.ErrorConstants;
import com.chf.core.web.errors.ErrorVM;
import com.chf.core.web.errors.FieldErrorVM;

import reactor.core.publisher.Mono;

/**
 * Controller advice to translate the server side exceptions to client-friendly
 * json structures. The error response follows RFC7807 - Problem Details for
 * HTTP APIs (https://tools.ietf.org/html/rfc7807)
 */

public abstract class AbstractExceptionTranslator implements ProblemHandling, SecurityAdviceTrait {

    private static final Logger log = LoggerFactory.getLogger(AbstractExceptionTranslator.class);

    private static final String FIELD_ERRORS_KEY = "fieldErrors";
    private static final String MESSAGE_KEY = "message";
    private static final String PATH_KEY = "path";
    private static final String VIOLATIONS_KEY = "violations";

    @Override
    public Mono<ResponseEntity<Problem>> process(@Nullable ResponseEntity<Problem> entity, ServerWebExchange request) {
        if (entity == null) {
            return Mono.empty();
        }
        Problem problem = entity.getBody();
        if (!(problem instanceof ConstraintViolationProblem || problem instanceof DefaultProblem)) {
            return Mono.just(entity);
        }

        ProblemBuilder builder = Problem.builder()
                .withType(Problem.DEFAULT_TYPE.equals(problem.getType()) ? ErrorConstants.DEFAULT_TYPE
                        : problem.getType())
                .withStatus(problem.getStatus()).withTitle(problem.getTitle())
                .with(PATH_KEY, request.getRequest().getPath().value());

        if (problem instanceof ConstraintViolationProblem) {
            builder.with(VIOLATIONS_KEY, ((ConstraintViolationProblem) problem).getViolations()).with(MESSAGE_KEY,
                    ErrorConstants.ERR_VALIDATION);
        } else {
            builder.withCause(((DefaultProblem) problem).getCause()).withDetail(problem.getDetail())
                    .withInstance(problem.getInstance());
            problem.getParameters().forEach(builder::with);
            if (!problem.getParameters().containsKey(MESSAGE_KEY) && problem.getStatus() != null) {
                builder.with(MESSAGE_KEY, "error.http." + problem.getStatus().getStatusCode());
            }
        }
        return Mono.just(new ResponseEntity<>(builder.build(), entity.getHeaders(), entity.getStatusCode()));
    }

    @Override
    public Mono<ResponseEntity<Problem>> handleBindingResult(WebExchangeBindException ex,
            @Nonnull ServerWebExchange request) {
        BindingResult result = ex.getBindingResult();
        List<FieldErrorVM> fieldErrors = result.getFieldErrors().stream()
                .map(f -> new FieldErrorVM(f.getObjectName().replaceFirst("DTO$", ""), f.getField(),
                        StringUtils.isNotBlank(f.getDefaultMessage()) ? f.getDefaultMessage() : f.getCode()))
                .collect(Collectors.toList());

        Problem problem = Problem.builder().withType(ErrorConstants.CONSTRAINT_VIOLATION_TYPE)
                .withTitle("Data binding and validation failure").withStatus(Status.BAD_REQUEST)
                .with(MESSAGE_KEY, ErrorConstants.ERR_VALIDATION).with(FIELD_ERRORS_KEY, fieldErrors).build();
        return create(ex, problem, request);
    }

    @ExceptionHandler(ServiceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorVM processServiceError(ServiceException ex) {
        ErrorVM error = new ErrorVM(ex.getCode(), ex.getMessage());
        log.warn("Get ServiceException {}", error);
        return error;
    }

    @ExceptionHandler({ BadCredentialsException.class })
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ErrorVM processLoginException(Exception ex) {
        return new ErrorVM(ErrorCodeContants.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler
    public Mono<ResponseEntity<Problem>> handleNoSuchElementException(NoSuchElementException ex,
            ServerWebExchange request) {
        Problem problem = Problem.builder().withStatus(Status.NOT_FOUND)
                .with(MESSAGE_KEY, ErrorConstants.ENTITY_NOT_FOUND_TYPE).build();
        return create(ex, problem, request);
    }

    @ExceptionHandler
    public Mono<ResponseEntity<Problem>> handleConcurrencyFailure(ConcurrencyFailureException ex,
            ServerWebExchange request) {
        Problem problem = Problem.builder().withStatus(Status.CONFLICT)
                .with(MESSAGE_KEY, ErrorConstants.ERR_CONCURRENCY_FAILURE).build();
        return create(ex, problem, request);
    }
}
