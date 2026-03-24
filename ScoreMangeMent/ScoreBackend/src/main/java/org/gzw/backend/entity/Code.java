package org.gzw.backend.entity;

/**
 * 系统编码字典实体类
 * 对应数据库表：g_code
 */
public class Code {
    /** 编码 ID */
    private Integer codeId;
    /** 编码类型 */
    private String type;
    /** 类型名称 */
    private String typeName;
    /** 编码描述 */
    private String codeDesc;
    /** 数值 */
    private Integer num;
    /** 状态 */
    private String status;

    public Integer getCodeId() {
        return codeId;
    }

    public void setCodeId(Integer codeId) {
        this.codeId = codeId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getCodeDesc() {
        return codeDesc;
    }

    public void setCodeDesc(String codeDesc) {
        this.codeDesc = codeDesc;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
