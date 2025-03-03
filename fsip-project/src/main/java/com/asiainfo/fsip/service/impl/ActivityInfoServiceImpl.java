package com.asiainfo.fsip.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.asiainfo.fsip.entity.FsipActivityInfoEntity;
import com.asiainfo.fsip.mapper.fsip.ActivityInfoMapper;
import com.asiainfo.fsip.model.FsipActivityInfoReq;
import com.asiainfo.fsip.model.ProjectAchievementSearchModel;
import com.asiainfo.fsip.service.ActivityInfoService;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;
import com.asiainfo.mcp.tmc.common.entity.base.PageReq;
import com.asiainfo.mcp.tmc.common.exception.BusinessException;
import com.asiainfo.mcp.tmc.common.util.TranceNoTool;
import com.asiainfo.mcp.tmc.sso.util.StaffInfoUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class ActivityInfoServiceImpl extends ServiceImpl<ActivityInfoMapper, FsipActivityInfoEntity> implements ActivityInfoService {

    @Resource
    private ActivityInfoMapper activityInfoMapper;

    @Resource
    private TranceNoTool tranceNoTool;

    @Value("${newsLength:100}")
    private int newsLength;

    //添加新闻
    @Override
    public void saveInfo(FsipActivityInfoEntity activity) throws Exception {
        if (activity.getType().isEmpty()) {
            throw new Exception("请选择类型");
        } else if (activity.getTitle().isEmpty()) {
            throw new Exception("标题不为空");
        }
        String newsId = tranceNoTool.getCommonId("Active_ID");
        activity.setId(newsId);

        StaffInfo staffInfo = StaffInfoUtil.getStaff();
        activity.setStaffId(String.valueOf(staffInfo.getMainUserId()));
        activity.setStaffName(staffInfo.getEmpName());

        activity.setUpdateTime(new Date());

        activityInfoMapper.insert(activity);
    }


    //按条件查询新闻
    @Override
    public PageInfo<FsipActivityInfoEntity> queryInfo(PageReq<FsipActivityInfoReq> pageReq) {
        FsipActivityInfoReq reqParam = pageReq.getReqParam();

        PageHelper.startPage(pageReq.getPageNum(), pageReq.getPageSize());
        LambdaQueryWrapper<FsipActivityInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(reqParam.getType() != null, FsipActivityInfoEntity::getType, reqParam.getType());
        queryWrapper.like(reqParam.getTitle() != null, FsipActivityInfoEntity::getTitle, reqParam.getTitle());
        queryWrapper.orderByDesc(FsipActivityInfoEntity::getUpdateTime);

        List<FsipActivityInfoEntity> entityList = activityInfoMapper.selectList(queryWrapper);
        if(CollUtil.isEmpty(entityList)){
            return new PageInfo(entityList);
        }

        entityList.parallelStream().forEach(item -> {
            String content = item.getContent();
            if(content.length() > newsLength){
                item.setIntroduction(content.substring(0, newsLength).concat("..."));
            }else{
                item.setIntroduction(content);
            }
        });

        return new PageInfo(entityList);
    }

    //删除
    @Override
    public void deleteInfo(String id) throws Exception {
        int i = activityInfoMapper.deleteById(id);
        if (i < 1) {
            throw new Exception("删除失败");
        }
    }

    //根据id查询详情
    @Override
    public FsipActivityInfoEntity queryDetail(String id) throws Exception {
        if (id.isEmpty()) {
            throw new Exception("查看详情失败");
        }

        return activityInfoMapper.selectById(id);
    }


}
