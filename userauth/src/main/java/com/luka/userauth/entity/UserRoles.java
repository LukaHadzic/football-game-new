package com.luka.userauth.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user_roles")
public class UserRoles {

    @EmbeddedId
    private UserRolesId id = new UserRolesId();

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("roleId")
    @JoinColumn(name="role_id")
    private Role role;


    public UserRoles() {
    }

    public UserRoles(UserRolesId id, User user, Role role) {
        this.id = id;
        this.user = user;
        this.role = role;
    }

    public UserRoles(User user, Role role){
        this.id = new  UserRolesId();
        this.id.setUserId(user.getId());
        this.id.setRoleId(role.getId());
        this.user = user;
        this.role = role;
    }

    public UserRolesId getId() {
        return id;
    }

    public void setId(UserRolesId id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}

