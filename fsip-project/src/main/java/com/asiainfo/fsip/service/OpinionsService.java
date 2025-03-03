package com.asiainfo.fsip.service;


import com.asiainfo.fsip.entity.FsipOpinionsEntity;

import java.util.List;

public interface OpinionsService {

    List<FsipOpinionsEntity> getOpinionsByStaffId(String staffId);

    void addOpinions(String staffId, String opinions);

    void deleteOpinions(String id);
}
