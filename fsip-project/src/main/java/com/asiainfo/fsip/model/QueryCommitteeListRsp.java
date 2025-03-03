package com.asiainfo.fsip.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class QueryCommitteeListRsp {

    @ApiModelProperty(value = "专业线列表")
    List<ZYX_BEAN> zyxList = new ArrayList<>();

    @Data
    public static class ZYX_BEAN{
        @ApiModelProperty(value = "专业线编码")
        private String zyxCode;

        @ApiModelProperty(value = "专业线名称")
        private String zyxName;

        @ApiModelProperty(value = "人员列表")
        private List<Staff2RoleModel> staffList = new ArrayList<>();
    }
}
