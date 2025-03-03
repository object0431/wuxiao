package com.asiainfo.fsip.service.impl;

import com.asiainfo.fsip.entity.FsipOpinionsEntity;
import com.asiainfo.fsip.mapper.fsip.FsipOpinionsMapper;
import com.asiainfo.fsip.service.OpinionsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class OpinionsServiceImpl implements OpinionsService {

    @Resource
    private FsipOpinionsMapper fsipOpinionsMapper;

    @Override
    public List<FsipOpinionsEntity> getOpinionsByStaffId(String staffId) {
        return fsipOpinionsMapper.selectByStaffId(staffId);
    }

    @Override
    public void addOpinions(String staffId, String opinions) {
        FsipOpinionsEntity fsipOpinionsEntity = FsipOpinionsEntity.builder().staffId(staffId)
                .remark(opinions).updateTime(new Date()).build();

        fsipOpinionsMapper.insert(fsipOpinionsEntity);
    }

    @Override
    public void deleteOpinions(String id) {
        fsipOpinionsMapper.deleteById(id);
    }
}
