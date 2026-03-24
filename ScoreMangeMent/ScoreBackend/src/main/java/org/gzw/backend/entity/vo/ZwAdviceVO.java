package org.gzw.backend.entity.vo;

import java.util.Date;
import java.util.List;

/**
 * 意见视图对象
 * 包含关联的用户姓名和字典描述信息
 */
public class ZwAdviceVO {
    /** 意见 ID（主键） */
    private Long adviceId;
    /** 接收者 ID */
    private Long recipientId;
    /** 接收者姓名 */
    private String recipientName;
    /** 发送者 ID */
    private Long senderId;
    /** 发送者姓名 */
    private String senderName;
    /** 意见类型 */
    private Integer adviceType;
    /** 意见类型名称 */
    private String adviceTypeName;
    /** 意见状态 */
    private Integer adviceStatus;
    /** 意见状态名称 */
    private String adviceStatusName;
    /** 意见描述 */
    private String adviceDesc;
    /** 发送者类型 */
    private Integer senderType;
    /** 发送者类型名称（实名/匿名） */
    private String senderTypeName;
    /** 图片 URL 列表 */
    private List<String> imageUrls;
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

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public Integer getAdviceType() {
        return adviceType;
    }

    public void setAdviceType(Integer adviceType) {
        this.adviceType = adviceType;
    }

    public String getAdviceTypeName() {
        return adviceTypeName;
    }

    public void setAdviceTypeName(String adviceTypeName) {
        this.adviceTypeName = adviceTypeName;
    }

    public Integer getAdviceStatus() {
        return adviceStatus;
    }

    public void setAdviceStatus(Integer adviceStatus) {
        this.adviceStatus = adviceStatus;
    }

    public String getAdviceStatusName() {
        return adviceStatusName;
    }

    public void setAdviceStatusName(String adviceStatusName) {
        this.adviceStatusName = adviceStatusName;
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

    public String getSenderTypeName() {
        return senderTypeName;
    }

    public void setSenderTypeName(String senderTypeName) {
        this.senderTypeName = senderTypeName;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
