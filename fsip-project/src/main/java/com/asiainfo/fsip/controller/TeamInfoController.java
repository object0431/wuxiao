package com.asiainfo.fsip.controller;

import com.asiainfo.fsip.entity.FsipTeamInfoEntity;
import com.asiainfo.fsip.model.TeamInfoExcelImportResp;
import com.asiainfo.fsip.model.TeamInfoReportInfoResp;
import com.asiainfo.fsip.service.FsipTeamInfoService;
import com.asiainfo.mcp.tmc.common.base.annotation.RspResult;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;
import com.asiainfo.mcp.tmc.common.entity.base.PageReq;
import com.asiainfo.mcp.tmc.sso.util.StaffInfoUtil;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("team")
@Slf4j
@Api("战队信息获取服务")
public class TeamInfoController {

    @Resource
    FsipTeamInfoService fsipTeamInfoService;

    @PostMapping("/teamExcelImport")
    @ApiOperation("战队信息导入")
    @RspResult
    public TeamInfoExcelImportResp teamExcelImport(MultipartFile file){
        StaffInfo staffInfo = StaffInfoUtil.getStaff();
        try {
            return fsipTeamInfoService.saveFsipTeamInfo(staffInfo,file.getInputStream());
        }catch (Exception e){
            log.error("数据导入异常",e);
            return TeamInfoExcelImportResp.builder().respCode("8888").respMsg("导入数据异常，异常原因："+e.getMessage()).build();
        }
    }

    @PostMapping("/teamInfoSel")
    @ApiOperation("战队信息查询")
    @RspResult
    public PageInfo<FsipTeamInfoEntity> teamInfoSel(@RequestBody PageReq<FsipTeamInfoEntity> req){
        return fsipTeamInfoService.teamInfoSel(req);
    }


    @PostMapping("/teamInfoReportInfoSel")
    @ApiOperation("战队信息报表数据获取")
    @RspResult
    public List<TeamInfoReportInfoResp> teamInfoReportInfoSel(){
        return fsipTeamInfoService.teamInfoReportInfoSel();
    }

}
