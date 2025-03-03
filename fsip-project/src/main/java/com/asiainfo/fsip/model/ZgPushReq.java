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
@ApiModel(description = "专干推送信息")
public class ZgPushReq {

    @ApiModelProperty(value = "奖项级别")
    private String achievementType;

    @ApiModelProperty(value = "需推送的成果编码列表")
    private List<String> achievementIdList;

    @ApiModelProperty(value = "评审委员会列表")
    private List<PSWYHBean> list;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class PSWYHBean {

        @ApiModelProperty(value = "成员编码")
        private String staffId;

        @ApiModelProperty(value = "成员名称")
        private String staffName;
    }
}
