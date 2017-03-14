package com.example.acl.role;

/**
 * Created by fhabermann on 01.03.2017.
 */
public class BaseRole implements RoleInterface {
    private String roleId;

    @Override
    public String getRoleId() {
        return null;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }
}
