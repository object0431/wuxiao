package com.asiainfo.fsip.model;


import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InnovationIssuesListReq {

    @ApiModelProperty(value = "查询类型 01：我的创新议题 02：创新议题查询")
    public String selType;

    @ApiModelProperty(value = "查询类型 01：我发布的 02：我参与的 03：我关注的")
    public String selfSelType;

    @ApiModelProperty(value = "议题列表")
    public String issuesId;

    @ApiModelProperty(value = "议题名称")
    public String issuesTitle;

    @ApiModelProperty(value = "工号")
    public String applierId;

    @ApiModelProperty(value = "部门")
    public String applierDeptId;

}
