package com.asiainfo.fsip.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectTransferReq {
    private String projectId;
    private String transferType;
    private String transferStaffId;
    private String transferStaffName;
    private String remark;
}
