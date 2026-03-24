package org.gzw.backend.entity.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 成绩导出 VO 类
 * 用于 Excel 导出时的数据映射
 */
@Data
public class ScoreExportVO {
    
    @ExcelProperty(value = "序号", index = 0)
    private Integer index;
    
    @ExcelProperty(value = "考试名称", index = 1)
    private String examName;
    
    @ExcelProperty(value = "科目", index = 2)
    private String subject;
    
    @ExcelProperty(value = "学号", index = 3)
    private Integer studentCode;
    
    @ExcelProperty(value = "姓名", index = 4)
    private String studentName;
    
    @ExcelProperty(value = "年级", index = 5)
    private String grade;
    
    @ExcelProperty(value = "班级", index = 6)
    private String className;
    
    @ExcelProperty(value = "分数", index = 7)
    private Double score;
    
    @ExcelProperty(value = "班级排名", index = 8)
    private Integer classRank;
    
    @ExcelProperty(value = "年级排名", index = 9)
    private Integer gradeRank;
}
