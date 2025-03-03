package com.asiainfo.fsip.entity;

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
@TableName("FSIP_STAFF_2_ROLE")
public class FispStaff2RoleEntity {
    @TableId("STAFF_ID")
    private String staffId;

    @TableField("STAFF_NAME")
    private String staffName;

    @TableField("DEPT_ID")
    private String deptId;

    @TableField("DEPT_NAME")
    private String deptName;

    @TableField("COMPANY_ID")
    private String companyId;

    @TableField("ROLE_ID")
    private String roleId;

    @TableField("UPDATE_TIME")
    private String updateTime;

    @TableField("OPERATOR_ID")
    private String operatorId;

    @TableField(exist = false)
    private String attrCode;

    @TableField(exist = false)
    private String attrValue;

    @TableField(exist = false)
    private String zyxCode;//专业线查询条件

}
