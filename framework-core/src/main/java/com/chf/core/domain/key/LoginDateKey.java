package com.chf.core.domain.key;

import java.io.Serializable;
import java.time.Instant;

import javax.persistence.Column;

public class LoginDateKey implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(length = 63, name = "login", nullable = false)
    private String login;

    @Column(name = "date", nullable = false)
    private Instant date;

    public LoginDateKey() {
    }

    public LoginDateKey(String login) {
        this(login, Instant.now());
    }

    public LoginDateKey(String login, Instant date) {
        this.login = login;
        this.date = date;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((date == null) ? 0 : date.hashCode());
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
        LoginDateKey other = (LoginDateKey) obj;
        if (date == null) {
            if (other.date != null)
                return false;
        } else if (!date.equals(other.date))
            return false;
        if (login == null) {
            if (other.login != null)
                return false;
        } else if (!login.equals(other.login))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "LoginDateKey [login=" + login + ", date=" + date + "]";
    }

}
