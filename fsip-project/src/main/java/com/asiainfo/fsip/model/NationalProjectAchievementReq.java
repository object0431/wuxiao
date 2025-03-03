package com.asiainfo.fsip.model;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "国家级项目成果库推送请求")
public class NationalProjectAchievementReq {

    @ApiModelProperty(value = "成果名称",name = "projectName")
    public String projectName;

    @ApiModelProperty(value = "获奖名称及等级",name = "awardsProjectNameLevel")
    public String awardsProjectNameLevel;

    @ApiModelProperty(value = "成果简介",name = "projectIntroduce")
    public String projectIntroduce;

    @ApiModelProperty(value = "介绍图片",name = "imageTag")
    public String imageTag;

    @ApiModelProperty(value = "创造人名称",name = "otherCreateName")
    public String mainCreateName;

    @ApiModelProperty(value = "参与创造人",name = "otherCreateName")
    public String otherCreateName;

    @ApiModelProperty(value = "附件列表",name = "attachmentList")
    private List<AttachmentBean> attachmentList;

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @ApiModel(value = "附件上传实体Bean")
    public static class AttachmentBean{
        @ApiModelProperty(value = "附件名称",name = "name")
        private String name;
    }

}
