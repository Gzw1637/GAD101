package org.gzw.backend.entity.vo;

public class TeacherVO {
    private Long teacherId;
    private Long userId;
    private String userName;
    private String name;
    private Integer sex;
    private String sexDesc;
    private Integer teacherCode;
    private Integer teachSubject;
    private String teachSubjectDesc;
    private Integer teacherStatus;
    private String teacherStatusDesc;
    private Integer teacherType;
    private String teacherTypeDesc;
    private Long phone;
    private Integer gra;
    private Integer cla;

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getSexDesc() {
        return sexDesc;
    }

    public void setSexDesc(String sexDesc) {
        this.sexDesc = sexDesc;
    }

    public Integer getTeacherCode() {
        return teacherCode;
    }

    public void setTeacherCode(Integer teacherCode) {
        this.teacherCode = teacherCode;
    }

    public Integer getTeachSubject() {
        return teachSubject;
    }

    public void setTeachSubject(Integer teachSubject) {
        this.teachSubject = teachSubject;
    }

    public String getTeachSubjectDesc() {
        return teachSubjectDesc;
    }

    public void setTeachSubjectDesc(String teachSubjectDesc) {
        this.teachSubjectDesc = teachSubjectDesc;
    }

    public Integer getTeacherStatus() {
        return teacherStatus;
    }

    public void setTeacherStatus(Integer teacherStatus) {
        this.teacherStatus = teacherStatus;
    }

    public String getTeacherStatusDesc() {
        return teacherStatusDesc;
    }

    public void setTeacherStatusDesc(String teacherStatusDesc) {
        this.teacherStatusDesc = teacherStatusDesc;
    }

    public Integer getTeacherType() {
        return teacherType;
    }

    public void setTeacherType(Integer teacherType) {
        this.teacherType = teacherType;
    }

    public String getTeacherTypeDesc() {
        return teacherTypeDesc;
    }

    public void setTeacherTypeDesc(String teacherTypeDesc) {
        this.teacherTypeDesc = teacherTypeDesc;
    }

    public Long getPhone() {
        return phone;
    }

    public void setPhone(Long phone) {
        this.phone = phone;
    }

    public Integer getGra() {
        return gra;
    }

    public void setGra(Integer gra) {
        this.gra = gra;
    }

    public Integer getCla() {
        return cla;
    }

    public void setCla(Integer cla) {
        this.cla = cla;
    }
}
