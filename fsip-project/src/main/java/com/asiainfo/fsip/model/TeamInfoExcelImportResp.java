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
public class TeamInfoExcelImportResp {

    @ApiModelProperty(value = "返回code",name = "respCode")
    public String respCode;

    @ApiModelProperty(value = "返回消息",name = "respMsg")
    public String respMsg;
}
