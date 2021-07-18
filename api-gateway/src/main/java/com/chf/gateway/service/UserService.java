package com.chf.gateway.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chf.core.constants.SystemConstants;
import com.chf.core.security.reactive.SecurityUtils;
import com.chf.core.utils.RandomUtil;
import com.chf.gateway.repository.AuthorityRepository;
import com.chf.gateway.repository.UserRepository;
import com.chf.user.core.domain.Authority;
import com.chf.user.core.domain.User;
import com.chf.user.core.exception.InvalidPasswordException;
import com.chf.user.core.service.dto.AdminUserDTO;
import com.chf.user.core.service.dto.UserDTO;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthorityRepository authorityRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthorityRepository authorityRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorityRepository = authorityRepository;
    }

    @Transactional
    public Mono<User> createUser(AdminUserDTO userDTO) {
        User user = new User();
        user.setLogin(userDTO.getLogin().toLowerCase());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail().toLowerCase());
        }
        user.setImageUrl(userDTO.getImageUrl());
        if (userDTO.getLangKey() == null) {
            user.setLangKey(SystemConstants.LANG); // default language
        } else {
            user.setLangKey(userDTO.getLangKey());
        }
        return Flux
            .fromIterable(userDTO.getAuthorities() != null ? userDTO.getAuthorities() : new HashSet<>())
            .flatMap(authorityRepository::findById)
            .doOnNext(authority -> user.getAuthorities().add(authority))
            .then(Mono.just(user))
            .publishOn(Schedulers.boundedElastic())
            .map(
                newUser -> {
                    String encryptedPassword = passwordEncoder.encode(RandomUtil.generatePassword());
                    newUser.setPassword(encryptedPassword);
                    newUser.setResetKey(RandomUtil.generateResetKey());
                    newUser.setResetDate(Instant.now());
                    newUser.setActivated(true);
                    return newUser;
                }
            )
            .flatMap(this::saveUser)
            .doOnNext(user1 -> log.debug("Created Information for User: {}", user1));
    }

    /**
     * Update all information for a specific user, and return the modified user.
     *
     * @param userDTO user to update.
     * @return updated user.
     */
    @Transactional
    public Mono<AdminUserDTO> updateUser(AdminUserDTO userDTO) {
        return userRepository
            .findById(userDTO.getId())
            .flatMap(
                user -> {
                    user.setLogin(userDTO.getLogin().toLowerCase());
                    user.setFirstName(userDTO.getFirstName());
                    user.setLastName(userDTO.getLastName());
                    if (userDTO.getEmail() != null) {
                        user.setEmail(userDTO.getEmail().toLowerCase());
                    }
                    user.setImageUrl(userDTO.getImageUrl());
                    user.setActivated(userDTO.isActivated());
                    user.setLangKey(userDTO.getLangKey());
                    Set<Authority> managedAuthorities = user.getAuthorities();
                    managedAuthorities.clear();
                    return userRepository
                        .deleteUserAuthorities(user.getId())
                        .thenMany(Flux.fromIterable(userDTO.getAuthorities()))
                        .flatMap(authorityRepository::findById)
                        .map(managedAuthorities::add)
                        .then(Mono.just(user));
                }
            )
            .flatMap(this::saveUser)
            .doOnNext(user -> log.debug("Changed Information for User: {}", user))
            .map(AdminUserDTO::new);
    }

    @Transactional
    public Mono<Void> deleteUser(String login) {
        return userRepository
            .findOneByLogin(login)
            .flatMap(user -> userRepository.delete(user).thenReturn(user))
            .doOnNext(user -> log.debug("Deleted User: {}", user))
            .then();
    }

    /**
     * Update basic information (first name, last name, email, language) for the current user.
     *
     * @param firstName first name of user.
     * @param lastName  last name of user.
     * @param email     email id of user.
     * @param langKey   language key.
     * @param imageUrl  image URL of user.
     * @return a completed {@link Mono}.
     */
    @Transactional
    public Mono<Void> updateUser(String firstName, String lastName, String email, String langKey, String imageUrl) {
        return SecurityUtils
            .getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .flatMap(
                user -> {
                    user.setFirstName(firstName);
                    user.setLastName(lastName);
                    if (email != null) {
                        user.setEmail(email.toLowerCase());
                    }
                    user.setLangKey(langKey);
                    user.setImageUrl(imageUrl);
                    return saveUser(user);
                }
            )
            .doOnNext(user -> log.debug("Changed Information for User: {}", user))
            .then();
    }

    @Transactional
    public Mono<User> saveUser(User user) {
        return SecurityUtils
            .getCurrentUserLogin()
            .switchIfEmpty(Mono.just(SystemConstants.SYSTEM_ACCOUNT))
            .flatMap(
                login -> {
                    if (user.getCreatedBy() == null) {
                        user.setCreatedBy(login);
                    }
                    user.setLastModifiedBy(login);
                    // Saving the relationship can be done in an entity callback
                    // once https://github.com/spring-projects/spring-data-r2dbc/issues/215 is done
                    return userRepository
                        .save(user)
                        .flatMap(
                            savedUser ->
                                Flux
                                    .fromIterable(user.getAuthorities())
                                    .flatMap(authority -> userRepository.saveUserAuthority(savedUser.getId(), authority.getName()))
                                    .then(Mono.just(savedUser))
                        );
                }
            );
    }

    @Transactional
    public Mono<Void> changePassword(String login, String currentClearTextPassword, String newPassword) {
        return userRepository.findOneByLogin(login)
            .publishOn(Schedulers.boundedElastic())
            .map(
                user -> {
                    String currentEncryptedPassword = user.getPassword();
                    if (!passwordEncoder.matches(currentClearTextPassword, currentEncryptedPassword)) {
                        throw new InvalidPasswordException();
                    }
                    String encryptedPassword = passwordEncoder.encode(newPassword);
                    user.setPassword(encryptedPassword);
                    return user;
                }
            )
            .flatMap(this::saveUser)
            .doOnNext(user -> log.debug("Changed password for User: {}", user))
            .then();
    }

    @Transactional(readOnly = true)
    public Flux<AdminUserDTO> getAllManagedUsers(Pageable pageable) {
        return userRepository.findAllWithAuthorities(pageable).map(AdminUserDTO::new);
    }

    @Transactional(readOnly = true)
    public Flux<UserDTO> getAllPublicUsers(Pageable pageable) {
        return userRepository.findAllByIdNotNullAndActivatedIsTrue(pageable).map(UserDTO::new);
    }

    @Transactional(readOnly = true)
    public Mono<Long> countManagedUsers() {
        return userRepository.count();
    }

    @Transactional(readOnly = true)
    public Mono<User> getUserWithAuthoritiesByLogin(String login) {
        return userRepository.findOneWithAuthoritiesByLogin(login);
    }

    @Transactional(readOnly = true)
    public Mono<User> getUserWithAuthorities() {
        return SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneWithAuthoritiesByLogin);
    }

    /**
     * Not activated users should be automatically deleted after 3 days.
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {
        removeNotActivatedUsersReactively().blockLast();
    }

    @Transactional
    public Flux<User> removeNotActivatedUsersReactively() {
        return userRepository
            .findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(
                LocalDateTime.ofInstant(Instant.now().minus(3, ChronoUnit.DAYS), ZoneOffset.UTC)
            )
            .flatMap(user -> userRepository.delete(user).thenReturn(user))
            .doOnNext(user -> log.debug("Deleted User: {}", user));
    }

    /**
     * Gets a list of all the authorities.
     * @return a list of all the authorities.
     */
    @Transactional(readOnly = true)
    public Flux<String> getAuthorities() {
        return authorityRepository.findAll().map(Authority::getName);
    }
}

