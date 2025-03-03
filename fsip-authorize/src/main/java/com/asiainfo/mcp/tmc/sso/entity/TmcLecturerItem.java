package com.asiainfo.mcp.tmc.sso.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author wzl
 * @Description:
 * @date 2022/9/14 14:26
 */
@Data
@TableName("tmc_lecturer_item")
public class TmcLecturerItem {

   private String lecturerId;
   private String attrType;
   private String attrCode;
   private String attrValue;
   private String attrDesc;

}
