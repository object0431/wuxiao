package com.asiainfo.fsip.service;

import com.asiainfo.fsip.entity.FsipTeamInfoEntity;
import com.asiainfo.fsip.model.TeamInfoExcelImportResp;
import com.asiainfo.fsip.model.TeamInfoReportInfoResp;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;
import com.asiainfo.mcp.tmc.common.entity.base.PageReq;
import com.github.pagehelper.PageInfo;

import java.io.InputStream;
import java.util.List;

public interface FsipTeamInfoService {

    public TeamInfoExcelImportResp saveFsipTeamInfo(StaffInfo staffInfo, InputStream inputStream);

    public PageInfo<FsipTeamInfoEntity> teamInfoSel(PageReq<FsipTeamInfoEntity> req);

    public List<TeamInfoReportInfoResp> teamInfoReportInfoSel();

}
