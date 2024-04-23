package com.example.socialapplication.repositories;


import com.example.socialapplication.model.entity.Medias;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaRepository extends JpaRepository<Medias, String> {
}
