package com.bj.wms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户实体类
 * 
 * 仓库管理系统的用户信息
 */
@Data
@Entity
@Table(name = "users")
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码长度至少6个字符")
    @Column(name = "password", nullable = false)
    private String password;

    @NotBlank(message = "真实姓名不能为空")
    @Size(max = 100, message = "真实姓名长度不能超过100个字符")
    @Column(name = "real_name", nullable = false, length = 100)
    private String realName;

    @Email(message = "邮箱格式不正确")
    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    /**
     * 用户状态
     * 0: 禁用, 1: 启用
     */
    @Column(name = "status", nullable = false)
    private Integer status = 1;

    /**
     * 用户角色
     * ADMIN: 管理员
     * MANAGER: 仓库管理员
     * OPERATOR: 操作员
     * VIEWER: 查看者
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role = UserRole.OPERATOR;

    /**
     * 用户角色枚举
     */
    public enum UserRole {
        ADMIN("管理员"),
        MANAGER("仓库管理员"),
        OPERATOR("操作员"),
        VIEWER("查看者");

        private final String description;

        UserRole(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}


