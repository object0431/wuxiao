package com.asiainfo.fsip.entity;

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
@TableName("FSIP_LOGIN_INITIATION_STAT")
public class FsipLoginInitiationStatEntity {

    @TableField("STAT_MONTH")
    private String statMonth;

    @TableField("COMPANY_ID")
    private String companyId;

    @TableField("COMPANY_NAME")
    private String companyName;

    @TableField("DEPT_ID")
    private String deptId;

    @TableField("DEPT_NAME")
    private String deptName;

    @TableField("STAFF_TOTAL")
    private int staffTotal;

    @TableField("LOGIN_NUM")
    private int loginNum;

    @TableField("INITIATE_NUM")
    private int initiateNum;

    @TableField("ACHIEVEMENT_NUM")
    private int achievementNum;

}
