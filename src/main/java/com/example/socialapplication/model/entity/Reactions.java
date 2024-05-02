package com.example.socialapplication.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
@Table(name = "reactions")
public class Reactions {
    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)")
    private String reactionsId;

    @Column(name = "object_type")
    private String objectType;

    @Column(name = "object_id", columnDefinition = "CHAR(36)")
    private String objectId;

    private String type;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private Users createdBy;

    // Phương thức setter cho createdBy
    public void setCreatedBy(Users createdBy) {
        this.createdBy = createdBy;
    }
}
