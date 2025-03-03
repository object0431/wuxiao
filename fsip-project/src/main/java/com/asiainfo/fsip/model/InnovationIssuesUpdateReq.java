package com.asiainfo.fsip.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "议题修改信息请求")
public class InnovationIssuesUpdateReq {

    @ApiModelProperty(value = "议题编码",name = "issuesId")
    private String issuesId;

    @ApiModelProperty(value = "议题标题",name = "title")
    private String title;

    @ApiModelProperty(value = "议题内容",name = "content")
    private String content;

    @ApiModelProperty(value = "是否要求合伙",name = "canJoin")
    private String canJoin;

    @ApiModelProperty(value = "合伙人人数",name = "partnerNum")
    private Integer partnerNum;

    @ApiModelProperty(value = "合伙人范围信息",name = "scope")
    private Scope scope;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel(value = "合伙人范围信息")
    public static class Scope{

        @ApiModelProperty(value = "类型",name = "type")
        private String type;

        @ApiModelProperty(value = "合伙人范围list",name = "values")
        private List<Value> values;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel(value = "合伙人范围信息")
    public static class Value{
        @ApiModelProperty(value = "编码",name = "code")
        private String code;

        @ApiModelProperty(value = "名称",name = "name")
        private String name;
    }

}
