package com.luka.userauth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UserRolesId implements Serializable {

    @Column(name = "user_id")
    private Long userId;
    @Column(name = "role_id")
    private Long roleId;

    public UserRolesId() {
    }

    public UserRolesId(Long userId, Long roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) return true;

        if(obj == null || getClass() != obj.getClass()) return false;

        UserRolesId castedObj = (UserRolesId) obj;

        return Objects.equals(userId, castedObj.getUserId()) && Objects.equals(roleId, castedObj.getRoleId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, roleId);
    }
}
