package com.example.socialapplication.repositories;

import com.example.socialapplication.model.entity.SharesPosts;
import com.example.socialapplication.model.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SharesPostsRepository extends JpaRepository<SharesPosts, String> {

    List<SharesPosts> findByCreateBy(Users createdBy);

}
