package com.example.acl.role;

/**
 * Created by fhabermann on 01.03.2017.
 */
public class BaseRole implements RoleInterface {
    private String roleId;

    public BaseRole() {

    }

    public BaseRole(String roleId) {
        this.roleId = roleId;
    }

    @Override
    public String getRoleId() {
        return this.roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }
}
