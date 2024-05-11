package com.example.socialapplication.repositories;

import com.example.socialapplication.model.entity.Comments;
import com.example.socialapplication.model.entity.Reactions;
import com.example.socialapplication.model.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReactionsRepository extends JpaRepository<Reactions, String> {
    List<Reactions> findAllByObjectIdAndType(String objectId, String type);

    Optional<Reactions> findByCreateByAndObjectIdAndType(Users createBy, String objectId, String type);

    List<Reactions> findByObjectId(String object_id);

    List<Reactions> findByObjectIdAndType(String objectId, String type);

    @Query("SELECT r FROM Reactions r WHERE r.objectId IN :postIds")
    Page<Reactions> findByPostIdIn(@Param("postIds") List<String> postIds, Pageable pageable);

    @Query("SELECT r FROM Reactions r WHERE r.objectId IN :id")
    Page<Reactions> findByCommentIdIn(List<String> id, Pageable pageable);

    Page<Reactions> findAllByCreateBy(Users user, Pageable pageable);
}
