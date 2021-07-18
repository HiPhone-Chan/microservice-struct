package com.chf.core.domain;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class AbstractTokenInfo {
    // AppType
    @Id
    @Column(name = "type", length = 31)
    private String type;

    @Column(name = "access_token", length = 511)
    private String accessToken;

    @Column(name = "expire_time")
    private Instant expireTime;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Instant getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Instant expireTime) {
        this.expireTime = expireTime;
    }

}
