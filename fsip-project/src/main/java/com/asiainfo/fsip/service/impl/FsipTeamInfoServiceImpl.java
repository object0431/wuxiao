package com.asiainfo.fsip.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.asiainfo.fsip.entity.FsipTeamInfoEntity;
import com.asiainfo.fsip.mapper.fsip.FsipTeamInfoMapper;
import com.asiainfo.fsip.model.TeamInfoExcelImportResp;
import com.asiainfo.fsip.model.TeamInfoExcelModel;
import com.asiainfo.fsip.model.TeamInfoReportInfoResp;
import com.asiainfo.fsip.service.CacheService;
import com.asiainfo.fsip.service.FsipTeamInfoService;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;
import com.asiainfo.mcp.tmc.common.entity.base.PageReq;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;

@Service
@Slf4j
public class FsipTeamInfoServiceImpl implements FsipTeamInfoService {

    @Resource
    CacheService cacheService;

    @Resource
    FsipTeamInfoMapper fsipTeamInfoMapper;

    @Override
    public TeamInfoExcelImportResp saveFsipTeamInfo(StaffInfo staffInfo, InputStream inputStream) {
        try {
            EasyExcel.read(inputStream, TeamInfoExcelModel.class, new ReadListener<TeamInfoExcelModel>() {
                private static final int BATCH_COUNT = 200;
                List<TeamInfoExcelModel> list = new ArrayList<TeamInfoExcelModel>(BATCH_COUNT);

                @Override
                public void invoke(TeamInfoExcelModel teamInfoExcelModel, AnalysisContext analysisContext) {
                 //   log.info("解析到一条数据:{}", JSON.toJSONString(teamInfoExcelModel));
                    list.add(teamInfoExcelModel);
                    if (list.size() == BATCH_COUNT) {
                        String msg = fsipTeamInfoExcelVerify(list);
                        if(!StringUtils.isEmpty(msg)){
                            throw new RuntimeException(msg);
                        }
                        try {
                            fsipTeamInfoExcelSavaToDB(staffInfo,list);
                        } catch (Exception e) {
                            throw new RuntimeException(e.getMessage());
                        }
                        list.clear();
                    }
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                    try {
                        fsipTeamInfoExcelSavaToDB(staffInfo,list);
                    } catch (Exception e) {
                        throw new RuntimeException(e.getMessage());
                    }
                }
            }).sheet().doRead();
        }catch (Exception e){
            log.error("导入文件出错",e);
            return TeamInfoExcelImportResp.builder().respMsg(e.getMessage()).respCode("8888").build();
        }
        return TeamInfoExcelImportResp.builder().respMsg("导入成功").respCode("0000").build();
    }


    private Map<String, String> reverseCompanyMap(){
        Map<String, String> map =  new HashMap<String,String>();
        Map<String, String> companyMap =  cacheService.getCompanyMap();
        for (String key : companyMap.keySet()) {
            map.put(companyMap.get(key),key);
        }
        return map;
    }


    private String fsipTeamInfoExcelVerify(List<TeamInfoExcelModel> list){
        Map<String, String> map =  this.reverseCompanyMap();
        String msg = "";
        for (TeamInfoExcelModel teamInfoExcelModel: list){
            if (!map.containsKey(teamInfoExcelModel.getTeamName())){
                msg += teamInfoExcelModel.getTeamName()+",";
            }
        }
        return msg;
    }

    private int fsipTeamInfoExcelSavaToDB(StaffInfo staffInfo, List<TeamInfoExcelModel> list) throws Exception{
        int count = 0;
        Map<String, String> map =  this.reverseCompanyMap();

        Map<String,Integer> countMap = new HashMap<>();

        for (TeamInfoExcelModel teamInfoExcelModel : list) {
            if(countMap.containsKey(teamInfoExcelModel.getCompanyName())){
                countMap.put(teamInfoExcelModel.getCompanyName(),countMap.get(teamInfoExcelModel.getCompanyName())+1);
            }else{
                countMap.put(teamInfoExcelModel.getCompanyName(),1);
            }
            if(countMap.get(teamInfoExcelModel.getCompanyName())==1){
                FsipTeamInfoEntity entity = fsipTeamInfoMapper.selectById(map.get(teamInfoExcelModel.getCompanyName()));
                if(null != entity){
                    entity.setTeamName(teamInfoExcelModel.getCompanyName());
                    entity.setStaffId(staffInfo.getMainUserId());
                    entity.setUpdateTime(new Date());
                    entity.setStaffName(staffInfo.getEmpName());
                    entity.setShineValue(BigDecimal.valueOf(teamInfoExcelModel.getShineValue()));
                    int upCount = fsipTeamInfoMapper.updateById(entity);
                    count += upCount;
                } else {
                    FsipTeamInfoEntity entity1 = new FsipTeamInfoEntity();
                    entity1.setCompanyId(map.get(teamInfoExcelModel.getCompanyName()));
                    entity1.setTeamName(teamInfoExcelModel.getTeamName());
                    entity1.setStaffId(staffInfo.getMainUserId());
                    entity1.setUpdateTime(new Date());
                    entity1.setStaffName(staffInfo.getEmpName());
                    entity1.setShineValue(BigDecimal.valueOf(teamInfoExcelModel.getShineValue()));
                    int upCount = fsipTeamInfoMapper.insert(entity1);
                    count += upCount;
                }
            }
        }
        return count;
    }


    @Override
    public PageInfo<FsipTeamInfoEntity> teamInfoSel(PageReq<FsipTeamInfoEntity> req) {
        PageInfo<FsipTeamInfoEntity> pageInfo = new PageInfo<FsipTeamInfoEntity>();
        Map<String, Object> param = new HashMap<String, Object>();
        if (null != req && null != req.getReqParam()){
            if (!StringUtils.isEmpty(req.getReqParam().getCompanyId())) {
                param.put("COMPANY_ID", req.getReqParam().getCompanyId());
            }
            if (!StringUtils.isEmpty(req.getReqParam().getTeamName())) {
                param.put("TEAM_NAME", req.getReqParam().getTeamName());
            }
        }
        PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<FsipTeamInfoEntity> retList = fsipTeamInfoMapper.selectByMap(param);

        if (null != retList && !retList.isEmpty()) {
            retList.parallelStream().forEach(item -> {
                item.setCompanyId(cacheService.getCompanyMap().get(item.getCompanyId()));
            });
            pageInfo = PageInfo.of(retList);
        }

        return pageInfo;
    }

    @Override
    public List<TeamInfoReportInfoResp> teamInfoReportInfoSel() {
        return fsipTeamInfoMapper.selectTeamStatisticData();
    }

}
