package com.asiainfo.fsip.mapper.fsip;

import com.asiainfo.fsip.entity.FsipTeamInfoEntity;
import com.asiainfo.fsip.model.TeamInfoReportInfoResp;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface FsipTeamInfoMapper extends BaseMapper<FsipTeamInfoEntity> {

    List<TeamInfoReportInfoResp> selectTeamStatisticData();

    List<FsipTeamInfoEntity> selectPendingData();

}
