package com.asiainfo.mcp.tmc.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 天梯日常版本检查
 * 1.10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageSendReq {

    @ApiModelProperty(value = "消息类型", name = "msgType", required = true)
    private String msgType;

    @ApiModelProperty(value = "发送类型", name = "sendType", required = true)
    private String sendType;

    @ApiModelProperty(value = "消息接收者", name = "recipient", required = true)
    private String recipient;

    @ApiModelProperty(value = "消息发送者(目前针对邮件)", name = "sender")
    private String sender;

    @ApiModelProperty(value = "消息标题(针对推送消息/邮件)", name = "title")
    private String title;

    @ApiModelProperty(value = "消息内容", name = "content")
    private String content;

    @ApiModelProperty(value = "消息有效时长(针对验证码短息)", name = "effectiveDuration")
    private String effectiveDuration;

    @ApiModelProperty(value = "消息模板ID", name = "templateId")
    private String templateId;

    @ApiModelProperty(value = "消息模板参数列表", name = "templatePara")
    private List<TemplatePara> templatePara;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ApiModel(value = "TemplatePara", description = "消息发送服务入参-消息模板参数列表")
    public static class TemplatePara{

        @ApiModelProperty(value = "模板参数Key", name = "key", required = true)
        private String key;

        @ApiModelProperty(value = "模板参数value", name = "recipient", required = true)
        private String value;
    }

}
