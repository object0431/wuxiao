package com.asiainfo.mcp.tmc.sso.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@TableName("FSIP_LOGIN_LOG")
public class FsipLoginLogEntity {

   @TableId("ID")
   private Long id;

   @TableField("LOGIN_ACCOUNT")
   private String loginAccount;

   @TableField("STAFF_NAME")
   private String staffName;

   @TableField("DEPT_ID")
   private String deptId;

   @TableField("COMPANY_ID")
   private String companyId;

   @TableField("DEVICE_TYPE")
   private String deviceType;

   @TableField("LOGIN_TIME")
   private Date loginTime;

   @TableField(exist = false)
   private Integer total;

}
