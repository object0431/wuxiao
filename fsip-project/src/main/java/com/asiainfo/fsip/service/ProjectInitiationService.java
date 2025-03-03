package com.asiainfo.fsip.service;

import com.asiainfo.fsip.model.*;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;

import java.util.List;
import java.util.Map;

public interface ProjectInitiationService {

    SaveProjectRsp saveProject(SaveProjectReq saveProjectReq);

    ProjectQueryRsp queryProject(ProjectQueryReq queryReq);

    ProjectDetailRsp detailProject(ProjectDetailReq detailReq);

    void transferProject(ProjectTransferReq transferReq, StaffInfo staffInfo );

    ModifyProjectRsp modifyProject(ModifyProjectReq modifyProjectReq);

    RelocateProjectRsp relocateProject(RelocateProjectReq relocateProjectReq);

    void expertAdviceScore(String expertAdviceId, float parseFloat, StaffInfo staffInfo);

    Map<String, List<String>> delProjects(String[] split);
}
