package com.asiainfo.fsip.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RelocateProjectRsp {
    private String rspCode;
    private String rspMsg;
    private String projectId;
}