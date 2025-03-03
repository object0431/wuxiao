package com.asiainfo.fsip.mapper.fsip;

import com.asiainfo.fsip.entity.FsipFlowLogEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FsipFlowLogMapper extends BaseMapper<FsipFlowLogEntity> {

    List<FsipFlowLogEntity> selectByTypeAndId(String flowType, String extId);

    List<FsipFlowLogEntity> selectByFlowTypeListAndExtId(List flowTypeList, String extId);
}
