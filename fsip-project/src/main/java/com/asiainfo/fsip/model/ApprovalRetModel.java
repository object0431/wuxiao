package com.asiainfo.fsip.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "审批结果信息Model")
public class ApprovalRetModel {

    @ApiModelProperty(value = "审批单类型：LXSQ=立项申请、CGSQ=成果申请、SJZSJCG=市级转升级成果申请")
    private String targetType;

    @ApiModelProperty(value = "审批单编码")
    private String targetId;

    @ApiModelProperty(value = "审批意见")
    private String remark;

    @ApiModelProperty(value = "审批结果：TG=通过、BH=驳回")
    private String retType;

    @ApiModelProperty(value = "是否短信提醒：1=是、0=否")
    private String notifyBySms;

    @ApiModelProperty(value = "扩展信息")
    private Map<String, String> ext;
}
