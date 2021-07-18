package com.chf.core.domain.key;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class AppLogin implements Serializable {

    private static final long serialVersionUID = 4160532535963759329L;

    @Column(length = 63, name = "login", nullable = false)
    private String login;

    @Column(length = 15, name = "app_type", nullable = false)
    private String appType;

    public AppLogin() {
    }

    public AppLogin(String login, String appType) {
        this.login = login;
        this.appType = appType;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((appType == null) ? 0 : appType.hashCode());
        result = prime * result + ((login == null) ? 0 : login.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AppLogin other = (AppLogin) obj;
        if (appType == null) {
            if (other.appType != null)
                return false;
        } else if (!appType.equals(other.appType))
            return false;
        if (login == null) {
            if (other.login != null)
                return false;
        } else if (!login.equals(other.login))
            return false;
        return true;
    }

}
