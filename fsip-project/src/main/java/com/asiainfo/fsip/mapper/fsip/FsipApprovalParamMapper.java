package com.asiainfo.fsip.mapper.fsip;

import com.asiainfo.fsip.entity.FsipApprovalParamEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FsipApprovalParamMapper extends BaseMapper<FsipApprovalParamEntity> {

    List<FsipApprovalParamEntity> selectApprovalByType(String apprType);

    List<FsipApprovalParamEntity> selectByType(String apprType);

    FsipApprovalParamEntity selectByTypeNode(String apprType, String nodeCode);
}
