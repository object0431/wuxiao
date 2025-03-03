package com.asiainfo.fsip.service;

import com.asiainfo.fsip.entity.FsipActivityInfoEntity;
import com.asiainfo.fsip.model.FsipActivityInfoReq;
import com.asiainfo.mcp.tmc.common.entity.base.PageReq;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;

public interface ActivityInfoService extends IService<FsipActivityInfoEntity> {
    void saveInfo(FsipActivityInfoEntity activity) throws Exception;

    PageInfo<FsipActivityInfoEntity> queryInfo(PageReq<FsipActivityInfoReq> pageReq);

    void deleteInfo(String id) throws Exception;

    FsipActivityInfoEntity queryDetail(String id) throws Exception;
}
