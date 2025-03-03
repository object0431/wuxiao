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
@ApiModel(description = "发起咨询请求报文")
public class ConsultationInitiateReq {

    @ApiModelProperty(value = "咨询类型：LXSQ=立项申请、CGSQ=成果申请")
    private String targetType;

    @ApiModelProperty(value = "工单编码")
    private String targetId;

    @ApiModelProperty(value = "咨询问题")
    private String questions;

    @ApiModelProperty(value = "专家列表")
    private List<Expert> expertList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel(description = "庄专家信息")
    public static class Expert {
        @ApiModelProperty(value = "人员编码")
        private String staffId;

        @ApiModelProperty(value = "人员名称")
        private String staffName;

        @ApiModelProperty(value = "手机号码")
        private String mobilePhone;
    }

}
