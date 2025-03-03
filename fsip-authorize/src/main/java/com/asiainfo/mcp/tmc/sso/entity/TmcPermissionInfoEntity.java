package com.asiainfo.mcp.tmc.sso.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("tmc_permission_info")
public class TmcPermissionInfoEntity {
    @TableId("STAFF_ID")
    private String staffId;
    @TableField("STAFF_NAME")
    private String staffName;
    @TableField("DEPT_NO")
    private String deptNo;
    @TableField("DEPT_NAME")
    private String deptName;
    @TableField("COMPANY_NO")
    private String companyNo;
    @TableField("PERM_TYPE")
    private String permType;
    @TableField("UPDATE_TIME")
    private String updateTime;
    @TableField("OPERATE_ID")
    private String operatorId;
    @TableField(exist = false)
    private String permTypeName;

}
