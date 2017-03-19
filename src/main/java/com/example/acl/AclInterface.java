package com.example.acl;

import com.example.acl.resource.ResourceInterface;
import com.example.acl.role.RoleInterface;

import java.util.List;

/**
 * Created by fhabermann on 01.03.2017.
 */
public interface AclInterface {
    //
    //	ROLE HANDLING
    //

    /**
     * check if role exists
     *
     * @param role
     * @return
     */
    boolean hasRole(RoleInterface role);

    /**
     * add role
     *
     * @param role
     */
    void addRole(RoleInterface role) throws AclException;

    /**
     * add role with parent roles
     *
     * @param role
     * @param parents
     */
    void addRole(RoleInterface role, List<RoleInterface> parents) throws AclException;

    /**
     * remove role
     *
     * @param role
     * @return
     */
    void removeRole(RoleInterface role) throws AclException;

    /**
     * remove role and delete all acl entries
     *
     * @param role
     * @param removeAcl
     * @return
     */
    void removeRole(RoleInterface role, boolean removeAcl) throws AclException;

    /**
     * get all roles
     *
     * @return
     */
    List<RoleInterface> getRoles() throws AclException;

    //
    //	RESOURCE HANDLING
    //

    /**
     * check if resource exists
     *
     * @param resource
     * @return
     */
    boolean hasResource(ResourceInterface resource);

    /**
     * add resource
     *
     * @param resource
     */
    void addResource(ResourceInterface resource) throws AclException;

    /**
     * remove resource
     *
     * @param resource
     */
    void removeResource(ResourceInterface resource) throws AclException;

    /**
     * remove resource and all acl entries
     *
     * @param resource
     * @param removeAcl
     */
    void removeResource(ResourceInterface resource, boolean removeAcl) throws AclException;

    /**
     * get all resources
     *
     * @return
     */
    List<ResourceInterface> getResources() throws AclException;

    //
    //	PERMISSIONS
    //

    /**
     * clear acl entries by role
     *
     * @param role
     */
    void clearByRole(RoleInterface role) throws AclException;

    /**
     * clear acl entries by resource
     *
     * @param resource
     */
    void clearByResource(ResourceInterface resource) throws AclException;

    /**
     * create allow acl entry
     *
     * @param role
     * @param resource
     * @param action
     */
    void allow(RoleInterface role, ResourceInterface resource, String action) throws AclException;

    /**
     * create deny acl entry
     *
     * @param role
     * @param resource
     * @param action
     */
    void deny(RoleInterface role, ResourceInterface resource, String action) throws AclException;

    /**
     * remove acl entry
     *
     * @param role
     * @param resource
     * @param action
     */
    void remove(RoleInterface role, ResourceInterface resource, String action) throws AclException;

    /**
     * check if acl entry exists
     *
     * @param role
     * @param resource
     * @param action
     * @return
     */
    boolean hasRule(RoleInterface role, ResourceInterface resource, String action);

    /**
     * check if role is allowed to access resource with given action
     *
     * @param role
     * @param resource
     * @param action
     * @return
     */
    boolean isAllowed(RoleInterface role, ResourceInterface resource, String action) throws AclException;

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
    boolean isAllowed(RoleInterface role, ResourceInterface resource, String action, boolean strict) throws AclException;

    //
    //	HELPER
    //

    /**
     * clear complete acl store with all roles, resources and entries
     */
    void truncateAll() throws AclException;
}
