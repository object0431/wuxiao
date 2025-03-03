package com.asiainfo.mcp.tmc.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Description of this file
 *
 * @author yangkl [yangkl@asiainfo.com]
 * @version 1.0  2019/3/8 initial version.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ApiModel(value = "MessageSendResp", description = "消息发送服务出参")
public class MessageSendRsp {

    @ApiModelProperty(value = "结果", name = "rspCode", required = true)
    private String rspCode;

    @ApiModelProperty(value = "返回描述", name = "rspDesc")
    private String rspDesc;

    @ApiModelProperty(value = "服务扩展数据", name = "attach")
    private Map attach;

}
