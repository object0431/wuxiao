package com.asiainfo.fsip.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("FSIP_APPROVAL_PARAM")
public class FsipApprovalParamEntity {

    @MppMultiId
    @TableField("APPR_TYPE")
    private String apprType;

    @MppMultiId
    @TableField("NODE_CODE")
    private String nodeCode;

    @TableField("NODE_NAME")
    private String nodeName;

    @TableField("NODE_STATE")
    private String nodeState;

    @TableField("NODE_TYPE")
    private String nodeType;

    @TableField("APPR_OFFICER")
    private String apprOfficer;

    @TableField("EXT_VALUE")
    private String extValue;

    @TableField("SORT")
    private int sort;

    @TableField("APPR_NUMBER")
    private String apprNumber;

    @TableField(exist = false)
    private String dealStaffId;

    @TableField(exist = false)
    private String dealStaffName;
}
