package com.example.demo.entity;

import com.example.demo.util.BaseService;
import com.example.demo.util.DataUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Date;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
//@JsonIgnoreProperties(value = { "createdBy", "createdAt", "updateBy", "updateAt" }, allowGetters = true)
public abstract class AbstractAuditingEntity<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @CreatedBy
    @Column(name = "create_by", nullable = false, length = 50, updatable = false)
    private String createdBy;

    @Column(name = "create_at", updatable = false)
    private Date createdAt;

    @LastModifiedBy
    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @Column(name = "update_at")
    private Date updateAt;

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updateBy) {
        this.updatedBy = updateBy;
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }

    @PreUpdate
    public void updateLastModifiedDatePreUpdate() {
        this.setUpdateAt(new Date());
//        if (this.updatedBy == null) this.setUpdatedBy(BaseService.getCurrentUser().getFullname());
    }

    @PrePersist
    public void updateLastModifiedDatePrePersist() {
        if (this.createdAt == null) this.setCreatedAt(new Date());
        this.setUpdateAt(new Date());
        if (this.createdBy == null) this.setCreatedBy(BaseService.getCurrentUser().getFullname());
        if (this.updatedBy == null) this.setUpdatedBy(BaseService.getCurrentUser().getFullname());
    }


}
