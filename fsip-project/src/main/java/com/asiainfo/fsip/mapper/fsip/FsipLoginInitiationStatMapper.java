package com.asiainfo.fsip.mapper.fsip;

import com.asiainfo.fsip.entity.FsipLoginInitiationStatEntity;
import com.asiainfo.fsip.model.LoginStatisticsModel;
import com.asiainfo.fsip.model.TimeScopeReq;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FsipLoginInitiationStatMapper extends BaseMapper<FsipLoginInitiationStatEntity> {

    int batchInsert(List<FsipLoginInitiationStatEntity> dataList);

    int deleteByStatMonth(String statMonth);

    List<LoginStatisticsModel> findByCidDid(@Param("req") TimeScopeReq req);
}
