package org.gzw.backend.entity;

import java.util.Date;

/**
 * 意见图片实体类
 * 对应数据库表：zw_advice_image
 */
public class ZwAdviceImage {
    /** 图片 ID（主键） */
    private Long imageId;
    /** 关联意见 ID */
    private Long adviceId;
    /** 图片链接 */
    private String imageUrl;
    /** 上传时间 */
    private Date createTime;

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public Long getAdviceId() {
        return adviceId;
    }

    public void setAdviceId(Long adviceId) {
        this.adviceId = adviceId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
