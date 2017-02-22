package com.example.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * Created by fhabermann on 22.02.2017.
 */
@Component("permissionEvaluator")
public class PermissionEvaluator implements org.springframework.security.access.PermissionEvaluator {
    private static final Logger logger = LoggerFactory.getLogger(PermissionEvaluator.class);

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        logger.info("Auth: " + authentication.getName());
        logger.info("Target: " + targetDomainObject.toString());
        logger.info("Permission: " + permission.toString());

        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }
}
