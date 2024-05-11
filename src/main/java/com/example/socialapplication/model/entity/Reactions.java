package com.example.socialapplication.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

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

    @Column(name = "create_at")
    private Timestamp createAt;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private Users createBy;

    // Phương thức setter cho createdBy
    public void setCreateBy(Users createBy) {
        this.createBy = createBy;
    }

    @PrePersist
    protected void onCreate() {
        createAt = new Timestamp(new Date().getTime());
    }
}
