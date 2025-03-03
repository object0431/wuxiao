package com.asiainfo.fsip.service.impl;

import com.asiainfo.fsip.entity.FsipProjectAchievementBaseEntity;
import com.asiainfo.fsip.entity.FsipProjectAchievementEntity;
import com.asiainfo.fsip.entity.FsipProjectAchievementItemEntity;
import com.asiainfo.fsip.mapper.fsip.FsipProjectAchievementBaseMapper;
import com.asiainfo.fsip.mapper.fsip.FsipProjectAchievementItemMapper;
import com.asiainfo.fsip.mapper.fsip.FsipProjectAchievementMapper;
import com.asiainfo.fsip.model.IndexAchievementQueryReq;
import com.asiainfo.fsip.model.IndexAchievementQueryRsp;
import com.asiainfo.fsip.service.CacheService;
import com.asiainfo.fsip.service.IndexService;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;
import com.asiainfo.mcp.tmc.sso.service.LoggerService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class IndexServiceImpl implements IndexService {

    enum AchievementType {
        NATIONAL, PROV, CITY
    }

    @Resource
    private FsipProjectAchievementBaseMapper achievementBaseMapper;

    @Resource
    private FsipProjectAchievementMapper achievementMapper;

    @Resource
    private FsipProjectAchievementItemMapper fsipProjectAchievementItemMapper;

    @Resource
    private CacheService cacheService;

    @Resource
    private LoggerService loggerService;

    @Override
    public IndexAchievementQueryRsp achievementQuery(IndexAchievementQueryReq req, StaffInfo staffInfo) {
        final List<IndexAchievementQueryRsp.RspData> nationalList = new ArrayList<>();
        final List<IndexAchievementQueryRsp.RspData> provList = new ArrayList<>();
        final List<IndexAchievementQueryRsp.RspData> cityList = new ArrayList<>();
        final List<FsipProjectAchievementBaseEntity> entityList = achievementBaseMapper.selectByProp(staffInfo.getCompanyId());
        if (!CollectionUtils.isEmpty(entityList)) {
            final Map<String, List<FsipProjectAchievementBaseEntity>> totalDataMap = new HashMap<>();
            entityList.forEach(v -> {
                if (totalDataMap.containsKey(v.getAchievementType())) {
                    totalDataMap.get(v.getAchievementType()).add(v);
                } else {
                    List<FsipProjectAchievementBaseEntity> dataList = new ArrayList<>();
                    dataList.add(v);
                    totalDataMap.put(v.getAchievementType(), dataList);
                }
            });
            totalDataMap.forEach((k, v) -> {
                Map<String, List<IndexAchievementQueryRsp.AchievementData>> dataMap = this.dataToMap(v);
                if (k.equalsIgnoreCase(AchievementType.NATIONAL.toString())) {
                    if (!CollectionUtils.isEmpty(dataMap)) {
                        for (Map.Entry<String, List<IndexAchievementQueryRsp.AchievementData>> entry : dataMap.entrySet()) {
                            nationalList.add(IndexAchievementQueryRsp.RspData.builder().achievementDate(entry.getKey()).achievementList(entry.getValue()).build());
                        }
                    }
                } else if (k.equalsIgnoreCase(AchievementType.PROV.toString())) {
                    if (!CollectionUtils.isEmpty(dataMap)) {
                        for (Map.Entry<String, List<IndexAchievementQueryRsp.AchievementData>> entry : dataMap.entrySet()) {
                            provList.add(IndexAchievementQueryRsp.RspData.builder().achievementDate(entry.getKey()).achievementList(entry.getValue()).build());
                        }
                    }
                } else if (k.equalsIgnoreCase(AchievementType.CITY.toString())) {
                    if (!CollectionUtils.isEmpty(dataMap)) {
                        for (Map.Entry<String, List<IndexAchievementQueryRsp.AchievementData>> entry : dataMap.entrySet()) {
                            cityList.add(IndexAchievementQueryRsp.RspData.builder().achievementDate(entry.getKey()).achievementList(entry.getValue()).build());
                        }
                    }
                }
            });
        }
        IndexAchievementQueryRsp build = IndexAchievementQueryRsp.builder().nationalLevel(nationalList).provincialLevel(provList).cityLevel(cityList).build();
        build.setNationalLevelCount((int) nationalList.stream().mapToLong(v -> v.getAchievementList().size()).sum());
        build.setProvincialLevelCount((int) provList.stream().mapToLong(v -> v.getAchievementList().size()).sum());
        build.setCityLevelCount((int) cityList.stream().mapToLong(v -> v.getAchievementList().size()).sum());

        loggerService.recordMenuLog("SY", req.getDeviceType(), staffInfo);

        return build;
    }

    private Map<String, List<IndexAchievementQueryRsp.AchievementData>> dataToMap(List<FsipProjectAchievementBaseEntity> dataList) {
        Map<String, String> companyMap = cacheService.getCompanyMap();
        Map<String, List<IndexAchievementQueryRsp.AchievementData>> dataMap = new HashMap<>();
        if (CollectionUtils.isEmpty(dataList)) return dataMap;
        for (FsipProjectAchievementBaseEntity entity : dataList) {
            FsipProjectAchievementEntity achievementEntity = achievementMapper.selectById(entity.getAchievementId());
            String achievementContent = null;
            if (AchievementType.NATIONAL.toString().equals(entity.getAchievementType())) {
                QueryWrapper<FsipProjectAchievementItemEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("ACHIEVEMENT_ID", entity.getAchievementId());
                queryWrapper.eq("ITEM_TYPE", "ACHIEVEMENT");
                queryWrapper.eq("ITEM_CODE", "CGJJ");
                List<FsipProjectAchievementItemEntity> itemEntityList = fsipProjectAchievementItemMapper.selectList(queryWrapper);
                if (!CollectionUtils.isEmpty(itemEntityList)) {
                    achievementContent = itemEntityList.get(0).getItemValue();
                }
            }
            IndexAchievementQueryRsp.AchievementData rspData = IndexAchievementQueryRsp.AchievementData.builder()
                    .achievementId(entity.getAchievementId())
                    .achievementName(AchievementType.NATIONAL.toString().equals(entity.getAchievementType()) ? entity.getAwardsProjectNameLevel() : getAchievementName(entity))
                    .projectName(achievementEntity == null ? "" : achievementEntity.getProjectName())
                    .backImage(achievementEntity == null ? "" : achievementEntity.getBackImage())
                    .applyName(AchievementType.NATIONAL.toString().equals(entity.getAchievementType()) ? entity.getMainCreateName() : entity.getApplierName())
                    .applyCompanyName(companyMap.get(entity.getApplierCompanyId()))
                    .achievementContent(achievementContent)
                    .build();
            if (dataMap.containsKey(entity.getApplyYearmon())) {
                dataMap.get(entity.getApplyYearmon()).add(rspData);
            } else {
                List<IndexAchievementQueryRsp.AchievementData> achievementDataList = new ArrayList<>();
                achievementDataList.add(rspData);
                dataMap.put(entity.getApplyYearmon(), achievementDataList);
            }
        }
        return dataMap;
    }

    private String getAchievementName(FsipProjectAchievementBaseEntity entity) {
        String achievementName = cacheService.getParamValue("AWARD_LEVEL", entity.getAwardLevel());
        if (StringUtils.isEmpty(achievementName)) {
            StringBuilder name = new StringBuilder("");
            if (AchievementType.NATIONAL.toString().equalsIgnoreCase(entity.getAchievementType())) {
                name.append("国家级");
            } else if (AchievementType.PROV.toString().equalsIgnoreCase(entity.getAchievementType())) {
                name.append("省级");
            } else {
                name.append("地市级");
            }
            if ("1".equals(entity.getAwardLevel())) {
                name.append("一等奖");
            } else if ("2".equals(entity.getAwardLevel())) {
                name.append("二等奖");
            } else if ("3".equals(entity.getAwardLevel())) {
                name.append("三等奖");
            }
            achievementName = name.toString();
        }
        return achievementName;
    }

    private boolean isProvence(StaffInfo staffInfo) {
        return "004300".equals(staffInfo.getCompanyId());
    }
}
