package com.cisdi.cpm.auth.module.auth.entity;

public class RoleEntity {
    private String id;
    private String rolenum;
    private String rolename;
    private String description;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getRolenum() {
        return rolenum;
    }
    public void setRolenum(String rolenum) {
        this.rolenum = rolenum;
    }
    public String getRolename() {
        return rolename;
    }
    public void setRolename(String rolename) {
        this.rolename = rolename;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
