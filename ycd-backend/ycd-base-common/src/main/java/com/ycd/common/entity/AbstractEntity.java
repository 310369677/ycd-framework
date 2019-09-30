package com.ycd.common.entity;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Id;

@javax.persistence.Entity
public abstract class AbstractEntity implements Entity<Long> {

    /**
     * 唯一标识
     */
    @Id
    @ApiModelProperty(name = "id", value = "主键id")
    protected Long id;

    /**
     * 版本，乐观锁
     */
    @ApiModelProperty(hidden = true)
    protected Long version;

    /**
     * 创建时间
     */
    @ApiModelProperty(hidden = true)
    protected String createTime;

    /**
     * 修改时间
     */
    @ApiModelProperty(hidden = true)
    protected String updateTime;

    /**
     * 创建者id
     */
    @ApiModelProperty(hidden = true)
    protected String createUserId;

    /**
     * 修改者id
     */
    @ApiModelProperty(hidden = true)
    protected String updateUserId;

    @Override
    public Long id() {
        return getId();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public String getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(String updateUserId) {
        this.updateUserId = updateUserId;
    }
}
