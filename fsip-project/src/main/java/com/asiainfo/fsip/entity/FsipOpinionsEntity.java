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
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("FSIP_COMMON_OPINIONS")
public class FsipOpinionsEntity {

    @TableId("ID")
    private String id;

    @TableField("STAFF_ID")
    private String staffId;

    @TableField("REMARK")
    private String remark;

    @TableField("UPDATE_TIME")
    private Date updateTime;


}
