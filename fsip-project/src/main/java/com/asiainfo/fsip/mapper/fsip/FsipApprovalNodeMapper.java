package com.asiainfo.fsip.mapper.fsip;

import com.asiainfo.fsip.entity.FsipApprovalNodeEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

public interface FsipApprovalNodeMapper extends BaseMapper<FsipApprovalNodeEntity> {

    int batchInsertNode(String apprType, String apprId, List<Map<String, String>> nodeList);

    String selectByNode(String apprType, String apprId, String nodeCode);

    int deleteByApprId(String apprType, String apprId);

    List<FsipApprovalNodeEntity> selectApprovalNode(String apprType, String apprId);

    List<FsipApprovalNodeEntity> selectApprovalNodeByNode(String apprType, String apprId, String nodeCode);

    List<FsipApprovalNodeEntity> selectApprovalNodeByType(String apprType, String apprId);

    List<FsipApprovalNodeEntity> selectApprovalNodeByPendingCode(String pendingCode);

    List<FsipApprovalNodeEntity> selectApprovalNodeByApprId(List<String> projectIds, String city2Prov);

    List<FsipApprovalNodeEntity> selectApprovalNodeByApprTypeList(List flowTypeList, String apprId);

}
