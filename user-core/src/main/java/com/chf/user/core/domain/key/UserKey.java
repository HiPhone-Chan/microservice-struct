package com.chf.user.core.domain.key;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.OneToOne;

import com.chf.user.core.domain.User;

@Embeddable
public class UserKey implements Serializable {

    private static final long serialVersionUID = 1L;

    @OneToOne
    private User user;

    public UserKey() {
    }

    public UserKey(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((user == null) ? 0 : user.hashCode());
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
        UserKey other = (UserKey) obj;
        if (user == null) {
            if (other.user != null)
                return false;
        } else if (!user.equals(other.user))
            return false;
        return true;
    }
}
