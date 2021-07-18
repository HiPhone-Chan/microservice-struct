package com.chf.core.security.authentication;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;

public class DefaultPreAuthenticationChecks implements UserDetailsChecker {

    protected final Log logger = LogFactory.getLog(getClass());

    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    @Override
    public void check(UserDetails user) {
        if (!user.isAccountNonLocked()) {
            logger.debug("User account is locked");

            throw new LockedException(
                    messages.getMessage("AbstractUserDetailsAuthenticationProvider.locked", "User account is locked"));
        }

        if (!user.isEnabled()) {
            logger.debug("User account is disabled");

            throw new DisabledException(
                    messages.getMessage("AbstractUserDetailsAuthenticationProvider.disabled", "User is disabled"));
        }

        if (!user.isAccountNonExpired()) {
            logger.debug("User account is expired");

            throw new AccountExpiredException(messages.getMessage("AbstractUserDetailsAuthenticationProvider.expired",
                    "User account has expired"));
        }
    }

}
