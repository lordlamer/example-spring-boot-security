package com.example.acl.jdbc;

import com.example.acl.AclException;
import com.example.acl.AclInterface;
import com.example.acl.resource.ResourceInterface;
import com.example.acl.role.RoleInterface;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
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
        this.dataSource = dataSource;

        jdbcTemplate = new JdbcTemplate(dataSource);

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
            jdbcTemplate.queryForObject("SELECT id FROM acl_role WHERE `role`=?", Long.class, role.getRoleId());
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
            jdbcTemplate.update("INSERT INTO acl_role (`role`) VALUES (?)", new Object[]{role.getRoleId()});
        } catch (Exception e) {
            throw new AclException("Could not create role: " + role.getRoleId());
        }

        // create membership roles
        for(RoleInterface parentRole : parents) {
            try {
                jdbcTemplate.update("INSERT INTO acl_role_parent (`role`, `role_parent`) VALUES (?, ?)", new Object[]{role.getRoleId(), parentRole.getRoleId()});
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
            jdbcTemplate.update("DELETE FROM acl_role_parent WHERE `role_parent`=? OR `role`=?", new Object[]{role.getRoleId(), role.getRoleId()});
        } catch (Exception e) {
            throw new AclException("Could not delete role parent: " + role.getRoleId());
        }

        // delete role
        try {
            jdbcTemplate.update("DELETE FROM acl_role WHERE `role`=?", new Object[]{role.getRoleId(), role.getRoleId()});
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
    public List<RoleInterface> getRoles() {
        List<RoleInterface> roles = new ArrayList<RoleInterface>();

        dataSource.getConnection().getMetaData().getIdentifierQuoteString()

        //jdbcTemplate.queryForList("", )
        //TODO: mit rowmapper vielleicht implementieren

        return roles;
    }

    /**
     * remove all roles
     */
    @Override
    public void removeRoles() {
        removeRoles(false);
    }

    /**
     * remove all roles and all acl entries
     *
     * @param removeAcl
     */
    @Override
    public void removeRoles(boolean removeAcl) {
        //TODO: implementieren
    }

    /**
     * check if resource exists
     *
     * @param resource
     * @return
     */
    @Override
    public boolean hasResource(ResourceInterface resource) {
        return false;
    }

    /**
     * add resource
     *
     * @param resource
     */
    @Override
    public void addResource(RoleInterface resource) {

    }

    /**
     * remove resource
     *
     * @param resource
     */
    @Override
    public void removeResource(RoleInterface resource) {

    }

    /**
     * remove resource and all acl entries
     *
     * @param resource
     * @param removeAcl
     */
    @Override
    public void removeResource(RoleInterface resource, boolean removeAcl) {

    }

    /**
     * get all resources
     *
     * @return
     */
    @Override
    public List<RoleInterface> getResources() {
        return null;
    }

    /**
     * remove all resources
     */
    @Override
    public void removeResources() {

    }

    /**
     * remove all resources and all acl entries
     *
     * @param removeAcl
     */
    @Override
    public void removeResources(boolean removeAcl) {

    }

    /**
     * clear acl entries by role
     *
     * @param role
     */
    @Override
    public void clearByRole(RoleInterface role) {

    }

    /**
     * clear acl entries by resource
     *
     * @param resource
     */
    @Override
    public void clearByResource(RoleInterface resource) {

    }

    /**
     * create allow acl entry
     *
     * @param role
     * @param resource
     * @param action
     */
    @Override
    public void allow(RoleInterface role, RoleInterface resource, String action) {

    }

    /**
     * create deny acl entry
     *
     * @param role
     * @param resource
     * @param action
     */
    @Override
    public void deny(RoleInterface role, RoleInterface resource, String action) {

    }

    /**
     * remove acl entry
     *
     * @param role
     * @param resource
     * @param action
     */
    @Override
    public void remove(RoleInterface role, RoleInterface resource, String action) {

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
    public boolean hasRule(RoleInterface role, RoleInterface resource, String action) {
        return false;
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
    public boolean isAllowed(RoleInterface role, RoleInterface resource, String action) {
        return false;
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
    public boolean isAllowed(RoleInterface role, RoleInterface resource, String action, boolean strict) {
        return false;
    }

    /**
     * clear complete acl store with all roles, resources and entries
     */
    @Override
    public void truncateAll() {

    }
}
