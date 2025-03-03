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
@TableName("FSIP_FLOW_LOG")
public class FsipFlowLogEntity {

    @TableId("ID")
    private int id;

    @TableField("FLOW_TYPE")
    private String flowType;

    @TableField("EXT_ID")
    private String extId;

    @TableField("NODE_CODE")
    private String nodeCode;

    @TableField("NODE_NAME")
    private String nodeName;

    @TableField("NODE_STATE")
    private String nodeState;

    @TableField("START_TIME")
    private Date startTime;

    @TableField("END_TIME")
    private Date endTime;

    @TableField("DEAL_STAFF_ID")
    private String dealStaffId;

    @TableField("DEAL_STAFF_NAME")
    private String dealStaffName;

    @TableField("SORT")
    private int sort;

    @TableField("REMARK")
    private String remark;

    @TableField(exist = false)
    private String nodeStateName;

}
