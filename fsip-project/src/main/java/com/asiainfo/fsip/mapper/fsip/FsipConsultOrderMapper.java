package com.asiainfo.fsip.mapper.fsip;

import com.asiainfo.fsip.entity.FsipConsultOrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FsipConsultOrderMapper extends BaseMapper<FsipConsultOrderEntity> {

    int batchInsert(@Param("orderList") List<FsipConsultOrderEntity> orderList);

    List<FsipConsultOrderEntity> selectByTargetId(String targetId);

    List<FsipConsultOrderEntity> selectHisByExpertId(String targetId, String expertId);

}
