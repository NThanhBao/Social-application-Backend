package com.example.socialapplication.repositories;

import com.example.socialapplication.model.entity.Users;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<Users, String> {
    Users findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByMail(String mail);
    boolean existsByPhoneNumber(String phoneNumber);
    @Query("SELECT u FROM Users u WHERE (u.firstName LIKE %?1% " +
            "OR u.lastName LIKE %?1% " +
            "OR CONCAT(u.firstName, ' ', u.lastName) LIKE %?1%" +
            "OR CONCAT(u.lastName, ' ', u.firstName) LIKE %?1%)" +
            "AND (u.roleType <> 'ADMIN' OR u.roleType IS NULL)")
    Page<Users> findByFullNameAndNotAdminRole(String fullName, Pageable pageable);

    @Query("select u from Users u where u.mail = :email")
    Users findByEmail(@Param("email") String email);


    @Query("SELECT COUNT(u) FROM Users u JOIN u.followingUser f WHERE f.username = :username")
    int countByFollowingUsersUsername(@Param("username") String username);


    @Query("SELECT f FROM Users u JOIN u.followingUser f WHERE u.id = :userId ORDER BY u.username")
    Page<Users> findFollowingUsersByUserId(@Param("userId") String userId, Pageable pageable);

    @Query("SELECT u FROM Users u JOIN u.followingUser f WHERE f.id = :userId ORDER BY u.username ASC")
    Page<Users> findFollowerUsersByUserId(@Param("userId") String userId, Pageable pageable);
}