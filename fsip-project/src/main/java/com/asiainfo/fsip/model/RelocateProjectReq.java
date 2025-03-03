package com.asiainfo.fsip.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RelocateProjectReq {
    private String projectId;
    private String projectName;
    private String remark;

}
