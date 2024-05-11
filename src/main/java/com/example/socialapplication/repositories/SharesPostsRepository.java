package com.example.socialapplication.repositories;

import com.example.socialapplication.model.entity.Posts;
import com.example.socialapplication.model.entity.SharesPosts;
import com.example.socialapplication.model.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SharesPostsRepository extends JpaRepository<SharesPosts, String> {

    List<SharesPosts> findByCreateBy(Users createdBy);

    List<SharesPosts> findByPostId(Posts post);

}
