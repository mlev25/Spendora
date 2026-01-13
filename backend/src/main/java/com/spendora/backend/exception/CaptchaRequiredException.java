package com.spendora.backend.exception;

import org.springframework.security.authentication.BadCredentialsException;

public class CaptchaRequiredException extends BadCredentialsException {
    public CaptchaRequiredException(String msg) {
        super(msg);
    }
}
