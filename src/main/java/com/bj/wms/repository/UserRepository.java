package com.bj.wms.repository;

import com.bj.wms.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户数据访问层
 * 
 * 继承JpaRepository，自动提供基础的CRUD操作
 * 可以自定义查询方法
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 根据用户名查找用户
     * Spring Data JPA会自动根据方法名生成查询
     */
    Optional<User> findByUsername(String username);

    /**
     * 根据用户名查找用户（忽略大小写）
     */
    Optional<User> findByUsernameIgnoreCase(String username);

    /**
     * 根据邮箱查找用户
     */
    Optional<User> findByEmail(String email);

    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);

    /**
     * 根据角色查找用户
     */
    Page<User> findByRole(User.UserRole role, Pageable pageable);

    /**
     * 根据状态查找用户
     */
    Page<User> findByStatus(Integer status, Pageable pageable);

    /**
     * 根据用户名模糊查询
     */
    @Query("SELECT u FROM User u WHERE u.username LIKE %:keyword% OR u.realName LIKE %:keyword%")
    Page<User> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 查找未删除的用户
     */
    @Query("SELECT u FROM User u WHERE u.deleted = 0")
    Page<User> findAllActive(Pageable pageable);

    /**
     * 根据用户名查找未删除的用户
     */
    @Query("SELECT u FROM User u WHERE u.username = :username AND u.deleted = 0")
    Optional<User> findByUsernameAndNotDeleted(@Param("username") String username);
}


