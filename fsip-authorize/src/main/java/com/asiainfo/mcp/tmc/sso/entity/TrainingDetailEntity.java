package com.asiainfo.mcp.tmc.sso.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingDetailEntity {
    private String projectId;
    private String projectName;
    private TmcTraingCourse traingCourse;
}
