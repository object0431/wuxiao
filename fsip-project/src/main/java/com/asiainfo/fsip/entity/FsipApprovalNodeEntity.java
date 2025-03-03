package com.asiainfo.fsip.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("FSIP_APPROVAL_NODE")
public class FsipApprovalNodeEntity {
    @TableId("ID")
    private Long id;

    @TableField("APPR_TYPE")
    private String apprType;

    @TableField("APPR_ID")
    private String apprId;

    @TableField("NODE_CODE")
    private String nodeCode;

    @TableField("DEAL_STAFF_ID")
    private String dealStaffId;

    @TableField("DEAL_STAFF_NAME")
    private String dealStaffName;

    @TableField("UPDATE_TIME")
    private Date updateTime;

    @TableField("TRANSFER_DEAL_STAFF_ID")
    private String transferDealStaffId;

    @TableField("TRANSFER_DEAL_STAFF_NAME")
    private String transferDealStaffName;

    @TableField("PENDING_CODE")
    private String pendingCode;

    @TableField("CITY_2_PROV")
    private String city2Prov;

    @TableField(exist = false)
    private String nodeState;

    @TableField(exist = false)
    private String nodeName;

    @TableField(exist = false)
    private int sort;

    @TableField(exist = false)
    private String position;

    @TableField(exist = false)
    private String extValue;

    @TableField(exist = false)
    private String apprNumber;
}
