package com.example.security;

import com.example.acl.AclException;
import com.example.acl.jdbc.JdbcAclService;
import com.example.acl.resource.BaseResource;
import com.example.acl.resource.ResourceInterface;
import com.example.acl.role.BaseRole;
import com.example.acl.role.RoleInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

            for(ResourceInterface resource : acl.getResources()) {
                System.out.println("RESOURCE: " + resource.getResourceId());
            }

            for(RoleInterface role : acl.getRoles()) {
                System.out.println("ROLE: " + role.getRoleId());
            }

            System.out.println("CHECK RES1: " + acl.hasResource(new BaseResource("resource")));
            System.out.println("CHECK RES2: " + acl.hasResource(new BaseResource("resource_does_not_exist")));

            System.out.println("CHECK ROL1: " + acl.hasRole(new BaseRole("test")));
            System.out.println("CHECK ROL2: " + acl.hasRole(new BaseRole("test_does_not_exist")));

            if(acl.hasRole(new BaseRole("new_role")))
                acl.removeRole(new BaseRole("new_role"));

            acl.addRole(new BaseRole("new_role"));
            acl.removeRole(new BaseRole("new_role"));


            if(acl.hasResource(new BaseResource("new_resource")))
                acl.removeResource(new BaseResource("new_resource"));

            acl.addResource(new BaseResource("new_resource"));
            acl.removeResource(new BaseResource("new_resource"));

            BaseRole a = new BaseRole("ROLE_A");
            BaseRole b = new BaseRole("ROLE_B");
            BaseRole c = new BaseRole("ROLE_C");

            acl.addRole(a);
            acl.addRole(b);

            List<BaseRole> parents = new ArrayList<BaseRole>();
            parents.add(a);
            parents.add(b);

            acl.addRole(c, parents);

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
