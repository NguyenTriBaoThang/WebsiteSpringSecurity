package com.bookstore.repository;

import com.bookstore.entity.Role;
import com.bookstore.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IUserRepository extends JpaRepository<User, Integer> {
    @Query("SELECT u FROM User u WHERE u.username = ?1")
    User findByUsername(String username);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO user_role (user_id, role_id) VALUE (?1, ?2)", nativeQuery = true)
    void addRoleToUser(Long userId, Long roleId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE user_role SET role_id = ?2 WHERE user_id = ?1", nativeQuery = true)
    void updateUserRole(Long userId, Long roleId);

    @Query("SELECT u.id FROM User u WHERE u.username = ?1")
    Long getUserIdByUserName(String username);

    @Query(value = "SELECT r.name FROM role r INNER JOIN user_role ur ON r.id = ur.role_id WHERE ur.user_id = ?1", nativeQuery = true)
    String[] getRoleOfUser(Long userId);

    @Query("SELECT r FROM Role r JOIN r.users u WHERE u.username = :username")
    List<Role> findRolesByUsername(@Param("username") String username);

    User findByEmail(String email);

    User findByResetPasswordToken(String token);

    @Query("SELECT r.id FROM Role r WHERE r.name = :roleName")
    Long getRoleIdByName(@Param("roleName") String roleName);
}