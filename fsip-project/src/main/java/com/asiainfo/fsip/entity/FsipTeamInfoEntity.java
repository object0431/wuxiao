package com.asiainfo.fsip.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 战队信息表
 * </p>
 *
 * @author author
 * @since 2023-09-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("fsip_team_info")
@ApiModel(value="FsipTeamInfo对象", description="战队信息表")
public class FsipTeamInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "公司编码")
    @TableId(value = "COMPANY_ID", type = IdType.INPUT)
    private String companyId;

    @ApiModelProperty(value = "战队名称")
    @TableField(value = "TEAM_NAME")
    private String teamName;

    @ApiModelProperty(value = "初始闪耀值")
    @TableField(value = "SHINE_VALUE")
    private BigDecimal shineValue;

    @ApiModelProperty(value = "操作员编码")
    @TableField(value = "STAFF_ID")
    private String staffId;

    @ApiModelProperty(value = "操作员名称")
    @TableField(value = "STAFF_NAME")
    private String staffName;

    @ApiModelProperty(value = "操作时间")
    @TableField(value = "UPDATE_TIME")
    private Date updateTime;

    @ApiModelProperty(value = "登录闪耀值")
    @TableField("LOGIN_SHINE_VALUE")
    private int loginShineValue;

    @ApiModelProperty(value = "修改登录闪耀值操作时间")
    @TableField("UPDATE_SHINE_TIME")
    private Date updateShineTime;
}
