package com.asiainfo.mcp.tmc.sso.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("FSIP_OPERATE_LOG")
public class FsipMenuLogEntity {

    @TableId("ID")
    private String id;

    @TableField("DEVICE_TYPE")
    private String deviceType;

    @TableField("MENU_ID")
    private String menuId;

    @TableField("OPERATOR_ID")
    private String operatorId;

    @TableField("OPERATOR_NAME")
    private String operatorName;

    @TableField("DEPT_ID")
    private String deptId;

    @TableField("COMPANY_ID")
    private String companyId;

    @TableField("OPERATE_TIME")
    private Date operateTime;

}
