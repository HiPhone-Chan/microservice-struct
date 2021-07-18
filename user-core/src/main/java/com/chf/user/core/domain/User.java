package com.chf.user.core.domain;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.chf.core.constants.SystemConstants;
import com.chf.core.domain.AbstractAuditingEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Table("user")
public class User extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Pattern(regexp = SystemConstants.LOGIN_REGEX)
    @Size(min = 1, max = 50)
    @Column("login")
    private String login;

    @JsonIgnore
    @NotNull
    @Size(min = 6, max = 60)
    @Column("password_hash")
    private String password;

    @Size(max = 50)
    @Column("nick_name")
    private String nickName;

    // 放全名
    @Size(max = 50)
    @Column("name")
    private String name;

    @Size(max = 50)
    @Column("first_name")
    private String firstName;

    @Size(max = 50)
    @Column("last_name")
    private String lastName;

    @Email
    @Size(min = 5, max = 100)
    @Column("email")
    private String email;

    @Column("mobile")
    private String mobile;

    @Column("id_card")
    private String idCard;

    @NotNull
    @Column("activated")
    private boolean activated = false;

    @Size(min = 2, max = 5)
    @Column("lang_key")
    private String langKey = SystemConstants.LANG;

    @Size(max = 256)
    @Column("image_url")
    private String imageUrl;

    @Size(max = 16)
    @Column("activation_key")
    @JsonIgnore
    private String activationKey;

    @Column("activation_date")
    private Instant activationDate;

    @Size(max = 20)
    @Column("reset_key")
    @JsonIgnore
    private String resetKey;

    @Column("reset_date")
    private Instant resetDate = null;

    @NotNull
    @Column("locked")
    private boolean locked = false;

    @Column("lock_date")
    private Instant lockDate;

    @Column("failed_times")
    private Integer failedTimes = 0;

    @Column("failed_date")
    private Instant failedDate;

    @JsonIgnore
    @Transient
    private Set<Authority> authorities = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    // Lowercase the login before saving it in database
    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public String getActivationKey() {
        return activationKey;
    }

    public void setActivationKey(String activationKey) {
        this.activationKey = activationKey;
    }

    public Instant getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(Instant activationDate) {
        this.activationDate = activationDate;
    }

    public String getLangKey() {
        return langKey;
    }

    public void setLangKey(String langKey) {
        this.langKey = langKey;
    }

    public Set<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public Instant getLockDate() {
        return lockDate;
    }

    public void setLockDate(Instant lockDate) {
        this.lockDate = lockDate;
    }

    public Integer getFailedTimes() {
        return failedTimes;
    }

    public void setFailedTimes(Integer failedTimes) {
        this.failedTimes = failedTimes;
    }

    public Instant getFailedDate() {
        return failedDate;
    }

    public void setFailedDate(Instant failedDate) {
        this.failedDate = failedDate;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getResetKey() {
        return resetKey;
    }

    public void setResetKey(String resetKey) {
        this.resetKey = resetKey;
    }

    public Instant getResetDate() {
        return resetDate;
    }

    public void setResetDate(Instant resetDate) {
        this.resetDate = resetDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }
        return id != null && id.equals(((User) o).id);
    }

    @Override
    public int hashCode() {
        // see
        // https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "User{" + "login='" + login + '\'' + ", firstName='" + firstName + '\'' + ", lastName='" + lastName
                + '\'' + ", email='" + email + '\'' + ", imageUrl='" + imageUrl + '\'' + ", activated='" + activated
                + '\'' + ", langKey='" + langKey + '\'' + "}";
    }
}