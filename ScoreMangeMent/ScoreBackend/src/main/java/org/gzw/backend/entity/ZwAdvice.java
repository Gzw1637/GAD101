package org.gzw.backend.entity;

import java.util.Date;

/**
 * 意见信息实体类
 * 对应数据库表：zw_advice
 */
public class ZwAdvice {
    /** 意见 ID（主键） */
    private Long adviceId;
    /** 接收者 ID */
    private Long recipientId;
    /** 发送者 ID */
    private Long senderId;
    /** 意见类型 */
    private Integer adviceType;
    /** 意见状态 */
    private Integer adviceStatus;
    /** 意见描述 */
    private String adviceDesc;
    /** 发送者类型 */
    private Integer senderType;
    /** 创建时间 */
    private Date createTime;

    public Long getAdviceId() {
        return adviceId;
    }

    public void setAdviceId(Long adviceId) {
        this.adviceId = adviceId;
    }

    public Long getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Long recipientId) {
        this.recipientId = recipientId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Integer getAdviceType() {
        return adviceType;
    }

    public void setAdviceType(Integer adviceType) {
        this.adviceType = adviceType;
    }

    public Integer getAdviceStatus() {
        return adviceStatus;
    }

    public void setAdviceStatus(Integer adviceStatus) {
        this.adviceStatus = adviceStatus;
    }

    public String getAdviceDesc() {
        return adviceDesc;
    }

    public void setAdviceDesc(String adviceDesc) {
        this.adviceDesc = adviceDesc;
    }

    public Integer getSenderType() {
        return senderType;
    }

    public void setSenderType(Integer senderType) {
        this.senderType = senderType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
