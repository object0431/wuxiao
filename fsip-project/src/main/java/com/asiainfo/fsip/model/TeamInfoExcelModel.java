package com.asiainfo.fsip.model;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class TeamInfoExcelModel {

    @ExcelProperty(value = "所属公司", index = 0)
    String companyName;

    @ExcelProperty(value = "战队名称", index = 1)
    String teamName;

    @ExcelProperty(value = "初始闪耀值",index = 2)
    Integer shineValue;

}
