package com.bj.wms.service;

import com.bj.wms.entity.User;
import com.bj.wms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 用户服务类
 * 
 * 处理用户相关的业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 创建用户
     */
    @Transactional
    public User createUser(User user) {
        log.info("创建用户: {}", user.getUsername());
        
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("用户名已存在: " + user.getUsername());
        }
        
        // 检查邮箱是否已存在
        if (user.getEmail() != null && userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("邮箱已存在: " + user.getEmail());
        }
        
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        return userRepository.save(user);
    }

    /**
     * 更新用户信息
     */
    @Transactional
    public User updateUser(Long id, User userDetails) {
        log.info("更新用户: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在: " + id));
        
        // 检查用户名是否被其他用户使用
        if (!user.getUsername().equals(userDetails.getUsername()) && 
            userRepository.existsByUsername(userDetails.getUsername())) {
            throw new RuntimeException("用户名已存在: " + userDetails.getUsername());
        }
        
        // 检查邮箱是否被其他用户使用
        if (userDetails.getEmail() != null && 
            !userDetails.getEmail().equals(user.getEmail()) && 
            userRepository.existsByEmail(userDetails.getEmail())) {
            throw new RuntimeException("邮箱已存在: " + userDetails.getEmail());
        }
        
        // 更新用户信息
        user.setUsername(userDetails.getUsername());
        user.setRealName(userDetails.getRealName());
        user.setEmail(userDetails.getEmail());
        user.setPhone(userDetails.getPhone());
        user.setStatus(userDetails.getStatus());
        user.setRole(userDetails.getRole());
        
        // 如果提供了新密码，则加密后更新
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }
        
        return userRepository.save(user);
    }

    /**
     * 根据ID获取用户
     */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * 根据用户名获取用户
     */
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsernameAndNotDeleted(username);
    }

    /**
     * 获取所有用户（分页）
     */
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAllActive(pageable);
    }

    /**
     * 根据关键词搜索用户
     */
    public Page<User> searchUsers(String keyword, Pageable pageable) {
        return userRepository.findByKeyword(keyword, pageable);
    }

    /**
     * 根据角色获取用户
     */
    public Page<User> getUsersByRole(User.UserRole role, Pageable pageable) {
        return userRepository.findByRole(role, pageable);
    }

    /**
     * 根据状态获取用户
     */
    public Page<User> getUsersByStatus(Integer status, Pageable pageable) {
        return userRepository.findByStatus(status, pageable);
    }

    /**
     * 删除用户（逻辑删除）
     */
    @Transactional
    public void deleteUser(Long id) {
        log.info("删除用户: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在: " + id));
        
        user.setDeleted(1);
        userRepository.save(user);
    }

    /**
     * 验证用户密码
     */
    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 修改用户密码
     */
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        log.info("修改用户密码: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在: " + userId));
        
        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("旧密码不正确");
        }
        
        // 设置新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}


