package com.asiainfo.fsip.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "登录数据统计")
public class LoginStatModel {

    @ApiModelProperty("公司编码")
    private String companyId;

    @ApiModelProperty("公司名称")
    private String companyName;

    @ApiModelProperty("部门编码")
    private String deptId;

    @ApiModelProperty("部门名称")
    private String deptName;

    @ApiModelProperty("部门总人数")
    private int staffTotal;

    @ApiModelProperty("登录次数")
    private int loginNum;

    @ApiModelProperty("登录率")
    private String loginRate;

    @ApiModelProperty("立项数")
    private int initiationNum;

    @ApiModelProperty("立项率")
    private String initiationRate;

    @ApiModelProperty("成果数")
    private int achievementNum;

    @ApiModelProperty("成果申请率")
    private String achievementRate;
}
