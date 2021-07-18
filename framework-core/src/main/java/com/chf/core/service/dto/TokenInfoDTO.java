package com.chf.core.service.dto;

public class TokenInfoDTO {

    private String accessToken;

    private long expireTime;

    public TokenInfoDTO() {
        super();
    }

    public TokenInfoDTO(String accessToken, long expireTime) {
        super();
        this.accessToken = accessToken;
        this.expireTime = expireTime;
    }

    public void set(TokenInfoDTO other) {
        this.accessToken = other.accessToken;
        this.expireTime = other.expireTime;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    @Override
    public String toString() {
        return "TokenInfoDTO [accessToken=" + accessToken + ", expireTime=" + expireTime + "]";
    }

}
