package com.asiainfo.fsip.model;

import com.asiainfo.fsip.entity.FsipConsultOrderEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 咨询工单
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FsipConsultOrderRsp {

    @ApiModelProperty("当前工单信息")
    private FsipConsultOrderEntity currOrder;

    @ApiModelProperty("历史工单信息")
    private List<FsipConsultOrderEntity> hisOrderList;
}
