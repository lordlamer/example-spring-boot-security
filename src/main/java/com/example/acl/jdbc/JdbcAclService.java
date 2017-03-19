package com.example.acl.jdbc;

import com.example.acl.AclException;
import com.example.acl.AclInterface;
import com.example.acl.resource.ResourceInterface;
import com.example.acl.role.BaseRole;
import com.example.acl.role.RoleInterface;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

/**
 * JDBC Implementation for AclService
 *
 * Created by fhabermann on 01.03.2017.
 */
public class JdbcAclService implements AclInterface {
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;
    private String quoteString;

    public JdbcAclService(DataSource dataSource) throws SQLException {
        // our datasource
        this.dataSource = dataSource;

        // create our jdbcTemplate
        jdbcTemplate = new JdbcTemplate(dataSource);

        // get quote string from db driver
        quoteString = dataSource.getConnection().getMetaData().getIdentifierQuoteString();
    }

    /**
     * quote a name
     *
     * @param name
     * @return name in quotes
     */
    private String quote(String name) {
        // TODO: check for sql server with []
        return quoteString + name + quoteString;
    }

    /**
     * check if role exists
     *
     * @param role
     * @return
     */
    @Override
    public boolean hasRole(RoleInterface role) {

        try {
            jdbcTemplate.queryForObject("SELECT id FROM acl_role WHERE " + quote("role") + "=?", Long.class, role.getRoleId());
        } catch(EmptyResultDataAccessException e) {
            return false;
        }

        return true;
    }

    /**
     * add role
     *
     * @param role
     */
    @Override
    public void addRole(RoleInterface role) throws AclException {
        addRole(role, null);
    }

    /**
     * add role with parent roles
     *
     * @param role
     * @param parents
     */
    @Override
    public void addRole(RoleInterface role, List<RoleInterface> parents) throws AclException {
        // check if role already exists
        if(hasRole(role))
            throw new AclException("Role already exists: " + role.getRoleId());

        // check if parent roles exists
        for(RoleInterface parentRole : parents) {
            // check if role already exists
            if(hasRole(parentRole))
                throw new AclException("Role already exists: " + parentRole.getRoleId());
        }

        // create role entry
        try {
            jdbcTemplate.update("INSERT INTO acl_role (" + quote("role") + ") VALUES (?)", new Object[]{role.getRoleId()});
        } catch (Exception e) {
            throw new AclException("Could not create role: " + role.getRoleId());
        }

        // create membership roles
        for(RoleInterface parentRole : parents) {
            try {
                jdbcTemplate.update("INSERT INTO acl_role_parent (" + quote("role") + ", role_parent) VALUES (?, ?)", new Object[]{role.getRoleId(), parentRole.getRoleId()});
            } catch (Exception e) {
                throw new AclException("Could not create role parent: " + role.getRoleId());
            }
        }
    }

    /**
     * remove role
     *
     * @param role
     * @return
     */
    @Override
    public void removeRole(RoleInterface role) throws AclException {
        removeRole(role, false);
    }

    /**
     * remove role and delete all acl entries
     *
     * @param role
     * @param removeAcl
     * @return
     */
    @Override
    public void removeRole(RoleInterface role, boolean removeAcl) throws AclException {
        // check if role exists
        if(!hasRole(role))
            throw new AclException("Role already exists: " + role.getRoleId());

        //remove existing acl entries
        if(removeAcl)
            clearByRole(role);

        // remove parent role
        try {
            jdbcTemplate.update("DELETE FROM acl_role_parent WHERE role_parent=? OR " + quote("role") + "=?", new Object[]{role.getRoleId(), role.getRoleId()});
        } catch (Exception e) {
            throw new AclException("Could not delete role parent: " + role.getRoleId());
        }

        // delete role
        try {
            jdbcTemplate.update("DELETE FROM acl_role WHERE " + quote("role") + "=?", new Object[]{role.getRoleId(), role.getRoleId()});
        } catch (Exception e) {
            throw new AclException("Could not delete role: " + role.getRoleId());
        }
    }

    /**
     * get all roles
     *
     * @return
     */
    @Override
    public List<RoleInterface> getRoles() throws AclException {
        try {
            List<RoleInterface> roles = jdbcTemplate.queryForList("SELECT * FROM acl_role ORDER BY id ASC", RoleInterface.class);
            return roles;
        } catch (Exception e) {
            throw new AclException("Could not get roles: " + e.getMessage());
        }
    }

    /**
     * check if resource exists
     *
     * @param resource
     * @return
     */
    @Override
    public boolean hasResource(ResourceInterface resource) {
        try {
            jdbcTemplate.queryForObject("SELECT id FROM acl_resource WHERE " + quote("resource") + "=?", Long.class, resource.getResourceId());
        } catch(EmptyResultDataAccessException e) {
            return false;
        }

        return true;
    }

    /**
     * add resource
     *
     * @param resource
     */
    @Override
    public void addResource(ResourceInterface resource) throws AclException {
        // check if resource already exists
        if(hasResource(resource))
            throw new AclException("Resource already exists");

        // create resource entry in table acl_resource
        try {
            jdbcTemplate.update("INSERT INTO acl_resource (" + quote("resource") + ") VALUES (?)", new Object[]{resource.getResourceId()});
        } catch (Exception e) {
            throw new AclException("Could not create resource: " + resource.getResourceId());
        }
    }

    /**
     * remove resource
     *
     * @param resource
     */
    @Override
    public void removeResource(ResourceInterface resource) throws AclException {
        removeResource(resource, false);
    }

    /**
     * remove resource and all acl entries
     *
     * @param resource
     * @param removeAcl
     */
    @Override
    public void removeResource(ResourceInterface resource, boolean removeAcl) throws AclException {
        if(!hasResource(resource))
            throw new AclException("Resource does not exist: " + resource.getResourceId());

        // remove existing acl entries
        if(removeAcl)
            clearByResource(resource);

        // delete role
        try {
            jdbcTemplate.update("DELETE FROM acl_resource WHERE " + quote("resource") + "=?", new Object[]{resource.getResourceId()});
        } catch (Exception e) {
            throw new AclException("Could not delete resource: " + resource.getResourceId());
        }
    }

    /**
     * get all resources
     *
     * @return
     */
    @Override
    public List<ResourceInterface> getResources() throws AclException {
        try {
            List<ResourceInterface> resources = jdbcTemplate.queryForList("SELECT * FROM acl_resource ORDER BY id ASC", ResourceInterface.class);
            return resources;
        } catch (Exception e) {
            throw new AclException("Could not get resources: " + e.getMessage());
        }
    }

    /**
     * clear acl entries by role
     *
     * @param role
     */
    @Override
    public void clearByRole(RoleInterface role) throws AclException {
        if(!hasRole(role))
            throw new AclException("Role does not exist: " + role.getRoleId());

        try {
            jdbcTemplate.update("DELETE FROM " + quote("acl") + " WHERE " + quote("role") + "=?", new Object[]{role.getRoleId()});
        } catch (Exception e) {
            throw new AclException("Could not delete acl entries for role: " + role.getRoleId());
        }
    }

    /**
     * clear acl entries by resource
     *
     * @param resource
     */
    @Override
    public void clearByResource(ResourceInterface resource) throws AclException {
        if(!hasResource(resource))
            throw new AclException("Resource does not exist: " + resource.getResourceId());

        try {
            jdbcTemplate.update("DELETE FROM " + quote("acl") + " WHERE " + quote("resource") + "=?", new Object[]{resource.getResourceId()});
        } catch (Exception e) {
            throw new AclException("Could not delete acl entries for resource: " + resource.getResourceId());
        }
    }

    /**
     * create allow acl entry
     *
     * @param role
     * @param resource
     * @param action
     */
    @Override
    public void allow(RoleInterface role, ResourceInterface resource, String action) throws AclException {
        if(!hasRole(role))
            throw new AclException("Role does not exist: " + role.getRoleId());

        if(!hasResource(resource))
            throw new AclException("Resource does not exist: " + resource.getResourceId());

        try {
            jdbcTemplate.update("INSERT IGNORE INTO " + quote("acl") + " ("+quote("role")+", "+quote("resource")+", "+quote("action")+", "+quote("right")+") VALUES (?, ?, ?, 'allow')", new Object[]{role.getRoleId(), role.getRoleId(), resource.getResourceId(), action});
        } catch (Exception e) {
            throw new AclException("Could not create allow entry for role: " + role.getRoleId() + " resource: " + resource.getResourceId() + " action: " + action);
        }
    }

    /**
     * create deny acl entry
     *
     * @param role
     * @param resource
     * @param action
     */
    @Override
    public void deny(RoleInterface role, ResourceInterface resource, String action) throws AclException {
        if(!hasRole(role))
            throw new AclException("Role does not exist: " + role.getRoleId());

        if(!hasResource(resource))
            throw new AclException("Resource does not exist: " + resource.getResourceId());

        try {
            jdbcTemplate.update("INSERT IGNORE INTO " + quote("acl") + " ("+quote("role")+", "+quote("resource")+", "+quote("action")+", "+quote("right")+") VALUES (?, ?, ?, 'deny')", new Object[]{role.getRoleId(), role.getRoleId(), resource.getResourceId(), action});
        } catch (Exception e) {
            throw new AclException("Could not create deny entry for role: " + role.getRoleId() + " resource: " + resource.getResourceId() + " action: " + action);
        }
    }

    /**
     * remove acl entry
     *
     * @param role
     * @param resource
     * @param action
     */
    @Override
    public void remove(RoleInterface role, ResourceInterface resource, String action) throws AclException {
        if(!hasRole(role))
            throw new AclException("Role does not exist: " + role.getRoleId());

        if(!hasResource(resource))
            throw new AclException("Resource does not exist: " + resource.getResourceId());

        try {
            jdbcTemplate.update("DELETE FROM " + quote("acl") + " WHERE "+quote("role")+"=? and "+quote("resource")+"=? and "+quote("action")+"=?", new Object[]{role.getRoleId(), role.getRoleId(), resource.getResourceId(), action});
        } catch (Exception e) {
            throw new AclException("Could not delete acl entry for role: " + role.getRoleId() + " resource: " + resource.getResourceId() + " action: " + action);
        }
    }

    /**
     * check if acl entry exists
     *
     * @param role
     * @param resource
     * @param action
     * @return
     */
    @Override
    public boolean hasRule(RoleInterface role, ResourceInterface resource, String action) {
        try {
            jdbcTemplate.queryForObject("SELECT id FROM " + quote("acl") + " WHERE " + quote("role") + "=? and "+quote("resource")+"=? and "+quote("action")+"=?", Long.class, new Object[]{role.getRoleId(), role.getRoleId(), resource.getResourceId(), action});
        } catch(EmptyResultDataAccessException e) {
            return false;
        }

        return true;
    }

    /**
     * check if role is allowed to access resource with given action
     *
     * @param role
     * @param resource
     * @param action
     * @return
     */
    @Override
    public boolean isAllowed(RoleInterface role, ResourceInterface resource, String action) throws AclException {
        return isAllowed(role, resource, action, false);
    }

    /**
     * check if role is allowed to access resource with given action
     * if strict is true parents will not be checked
     *
     * @param role
     * @param resource
     * @param action
     * @param strict
     * @return
     */
    @Override
    public boolean isAllowed(RoleInterface role, ResourceInterface resource, String action, boolean strict) throws AclException {
        boolean allowed = false;

        if(!hasRole(role))
            throw new AclException("Role does not exist: " + role.getRoleId());

        if(!hasResource(resource))
            throw new AclException("Resource does not exist: " + resource.getResourceId());

        try {
            // check if we found acl for role directly
            // ein eintrag direkt an einer rolle hat die hoechste prioritaet
            String accessRight = (String) jdbcTemplate.queryForObject(
                    "SELECT "+quote("right")+" FROM " + quote("acl") + " WHERE " + quote("role") + "=? and "+quote("resource")+"=? and "+quote("action")+"=?", new Object[]{role.getRoleId(), resource.getResourceId(), action}, String.class);

            // check if we got allow
            if (accessRight.equals("allow"))
                return true;
            else
                return false;
        } catch (EmptyResultDataAccessException e) {
            //throw new AclException(e.getMessage());
            System.out.println("EX: " + e.getMessage());
            e.printStackTrace();
        }

        // check for strict
        if(strict)
            return allowed;

        // we did not found an direct acl entry for role - so lets check parents
        // ORDER BY id DESC - prio fuer den letzten eintrag ist damit am hoechsten
        List<String> roles =jdbcTemplate.queryForList("SELECT role_parent FROM acl_role_parent WHERE " + quote("role") + "=? ORDER BY id DESC", new Object[]{role.getRoleId()}, String.class);

        for(String parentRole : roles) {
            // check acl for parent role
            if(isAllowed(new BaseRole(parentRole), resource, action))
                return true;
        }

        return allowed;
    }

    /**
     * clear complete acl store with all roles, resources and entries
     */
    @Override
    public void truncateAll() throws AclException {
        // delete role
        try {
            jdbcTemplate.update("TRUNCATE TABLE " + quote("acl"));
            jdbcTemplate.update("TRUNCATE TABLE " + quote("acl_resource"));
            jdbcTemplate.update("TRUNCATE TABLE " + quote("acl_role"));
            jdbcTemplate.update("TRUNCATE TABLE " + quote("acl_role_parent"));
        } catch (Exception e) {
            throw new AclException("Could not truncate acl database");
        }
    }
}
