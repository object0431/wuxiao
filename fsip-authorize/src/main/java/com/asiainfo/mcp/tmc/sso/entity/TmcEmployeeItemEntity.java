package com.asiainfo.mcp.tmc.sso.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author wzl
 * @Description:
 * @date 2022/9/14 14:26
 */
@Data
@TableName("tmc_employee_item")
public class TmcEmployeeItemEntity {

   /**
    * 员工编码
    */
   @TableId
   private String userId;
   /**
    * 属性编码
    */
   private String attrCode;
   /**
    * 属性值
    */
   private String attrValue;
   /**
    * 更新时间
    */
   private String updateTime;
   /**
    * 更新人
    */
   private String operateId;

}
