package com.example.security;

import com.example.acl.AclException;
import com.example.acl.jdbc.JdbcAclService;
import com.example.acl.resource.BaseResource;
import com.example.acl.role.BaseRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.io.Serializable;
import java.sql.SQLException;

/**
 * Created by fhabermann on 22.02.2017.
 */
@Component("permissionEvaluator")
public class PermissionEvaluator implements org.springframework.security.access.PermissionEvaluator {
    private static final Logger logger = LoggerFactory.getLogger(PermissionEvaluator.class);

    @Autowired
    private DataSource dataSource;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        logger.info("Auth: " + authentication.getName());
        logger.info("Target: " + targetDomainObject.toString());
        logger.info("Permission: " + permission.toString());

        try {
            JdbcAclService acl = new JdbcAclService(dataSource);

            System.out.println("CHECK: " + acl.isAllowed(new BaseRole("test"), new BaseResource("resource"), "foo", false));
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (AclException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }
}
