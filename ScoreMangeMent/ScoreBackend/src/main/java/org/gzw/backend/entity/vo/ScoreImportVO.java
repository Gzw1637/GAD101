package org.gzw.backend.entity.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 成绩导入 VO 类
 * 用于 Excel 导入时的数据映射
 */
@Data
public class ScoreImportVO {
    
    @ExcelProperty(value = "考试名称", index = 0)
    private String examName;
    
    @ExcelProperty(value = "科目", index = 1)
    private String subject;
    
    @ExcelProperty(value = "学号", index = 2)
    private Integer studentCode;
    
    @ExcelProperty(value = "姓名", index = 3)
    private String studentName;
    
    @ExcelProperty(value = "分数", index = 4)
    private Double score;
}
