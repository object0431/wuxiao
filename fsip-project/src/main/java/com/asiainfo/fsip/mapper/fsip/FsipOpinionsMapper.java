package com.asiainfo.fsip.mapper.fsip;

import com.asiainfo.fsip.entity.FsipOpinionsEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FsipOpinionsMapper extends BaseMapper<FsipOpinionsEntity> {

    List<FsipOpinionsEntity> selectByStaffId(String staffId);
}
