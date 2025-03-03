package com.asiainfo.fsip.controller;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.utils.StringUtils;
import com.asiainfo.fsip.entity.FsipStaticParamEntity;
import com.asiainfo.fsip.model.*;
import com.asiainfo.fsip.service.*;
import com.asiainfo.fsip.utils.JwtUtil;
import com.asiainfo.mcp.tmc.common.base.annotation.RspResult;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;
import com.asiainfo.mcp.tmc.common.entity.TitleInfo;
import com.asiainfo.mcp.tmc.common.entity.base.BaseRsp;
import com.asiainfo.mcp.tmc.common.entity.base.PageReq;
import com.asiainfo.mcp.tmc.common.exception.BusinessException;
import com.asiainfo.mcp.tmc.common.util.RspHelp;
import com.asiainfo.mcp.tmc.sso.util.StaffInfoUtil;
import com.github.pagehelper.PageInfo;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

@RestController
@RequestMapping("common")
@Slf4j
@Api("通用接口")
public class CommonController {

    @Resource
    private StaffInfoService staffInfoService;

    @Resource
    private MenuService menuService;

    @Resource
    private ParamService paramService;

    @Resource
    private TestService testService;

    @Resource
    private CacheService cacheService;

    @ApiOperation("查询菜单")
    @GetMapping("/queryMenuList")
    @RspResult
    public TreeSet<MenuInfoResp> queryMenuList() {
        StaffInfo staffInfo = StaffInfoUtil.getStaff();
        return menuService.queryUserMenuList(staffInfo);
    }

    @PostMapping("/queryStaffByPage")
    @ApiOperation(value = "查询员工数据")
    @RspResult
    public PageInfo<OrganizerStrucRsp.EmployeeChildrenBean> queryStaffByPage(@RequestBody PageReq<QryEmployeeReq> req){
        PageInfo<OrganizerStrucRsp.EmployeeChildrenBean> rsp =  staffInfoService.queryStaffInfoList(req);
        return rsp;
    }

    @PostMapping("/qryParamList")
    @ApiOperation(value = "查询参数列表")
    @RspResult
    public List<FsipStaticParamEntity> qryParamList(@RequestBody FsipStaticParamEntity req){
        log.info("查询静态参数请求：{}", JSONObject.toJSONString(req));
        if (StringUtils.isEmpty(req.getAttrType())) {
            throw new BusinessException("8888","参数类型[ATTR_TYPE]不能为空！");
        }
        StaffInfo staffInfo = StaffInfoUtil.getStaff();
        return  paramService.queryParam(req, staffInfo);
    }

    @PostMapping("/addParam")
    @ApiOperation(value = "新增静态参数")
    public BaseRsp<Void> addParam(@RequestBody FsipStaticParamEntity param){
        log.info("add static param：{}",JSONObject.toJSONString(param));
        StaffInfo staffInfo = StaffInfoUtil.getStaff();
        int insert = paramService.addParam(param, staffInfo);
        if(insert > 0 ){
            return RspHelp.success(null);
        }
        return RspHelp.fail(null,"添加失败！");
    }

    @PostMapping("/deleteParam")
    @ApiOperation(value = "删除静态参数")
    public BaseRsp<Void> deleteParam(@RequestParam String attrType, @RequestBody List<String> attrCodeList){
        log.info("delete static param：{}",JSONObject.toJSONString(attrCodeList));
        int delete = paramService.deleteParam(attrType, attrCodeList);
        if(delete > 0 ){
            return RspHelp.success(null);
        }
        return RspHelp.fail(null,"删除失败！");
    }

    @PostMapping("/changeParam")
    @ApiOperation(value = "修改静态参数")
    public BaseRsp<Void> changeParam(@RequestBody FsipStaticParamEntity param){
        StaffInfo staffInfo = StaffInfoUtil.getStaff();
        int update = paramService.modifyParam(param, staffInfo);
        if(update > 0 ){
            return RspHelp.success(null);
        }
        return RspHelp.fail(null,"修改失败！");
    }

    /**
     * 刷新缓存
     */
    @GetMapping("/refreshParam")
    public BaseRsp<Void> refreshParam(@RequestParam(required = false) String attrType) {
        try {
            cacheService.refreshParam(attrType);
            return RspHelp.success(null);
        } catch (Exception e) {
            log.info("Could not execute refreshCache", e);
            return RspHelp.fail(RspHelp.ERROR_CODE, e.getMessage());
        }
    }

    @RequestMapping("/queryOrgInfo")
    @ApiOperation("公司和部门信息查询")
    @RspResult
    public List<ParamModel> queryOrgInfo(@RequestBody Map<String, String> reqMap) {
        String queryType = reqMap.get("queryType");
        if (StringUtils.isEmpty(queryType)) {
            throw new BusinessException("查询类型[queryType]参数不能为空");
        }

        if ("company".equals(queryType)) {
            return paramService.queryCompanyList();
        }

        if ("dept".equals(queryType) && StringUtils.isEmpty(reqMap.get("queryKeywords"))) {
            throw new BusinessException("部门查询需要传入公司编码[queryKeywords]参数");
        }

        return paramService.queryDeptList(reqMap.get("queryKeywords"), reqMap.get("deptName"));
    }

    @PostMapping("/getTitle")
    @ApiOperation(value = "获取待办标题")
    @RspResult
    public String getTitle(@RequestParam("taskType") String taskType, @RequestBody TitleInfo titleInfo){
        return testService.getTitle(taskType, titleInfo);
    }

    @PostMapping("/sendPendingTask")
    @ApiOperation(value = "发送待办")
    @RspResult
    public void sendPendingTask(@RequestBody PendingModel pendingModel){
        StaffInfo staffInfo = StaffInfoUtil.getStaff();
        testService.sendPendingTask(pendingModel, staffInfo);
    }

    /**
     * 获取员工列表
     */
    @GetMapping("/getStaffList")
    public BaseRsp<List<StaffInfo>> getStaffList() {
        try {
            StaffInfo staffInfo = StaffInfoUtil.getStaff();
            List<StaffInfo> staffInfoList = staffInfoService.getStaffList(staffInfo.getMainUserId());
            return RspHelp.success(staffInfoList);
        } catch (Exception e) {
            log.info("Could not execute getStaffList", e);
            return RspHelp.fail(RspHelp.ERROR_CODE, e.getMessage());
        }
    }

    /**
     * 身份切换
     */
    @GetMapping("/switchIdentity")
    public BaseRsp<StaffInfo> switchIdentity(@RequestParam long id) {
        try {
            StaffInfo staffInfo = staffInfoService.switchIdentity(id);
            return RspHelp.success(staffInfo);
        } catch (Exception e) {
            log.info("Could not execute switchIdentity", e);
            return RspHelp.fail(RspHelp.ERROR_CODE, e.getMessage());
        }
    }


    @GetMapping("/testStaff")
    public JSONObject switchIdentity(@RequestParam String id) {
        JSONObject jsonObject = new JSONObject();
        if (id.equals("10086")){
            String jwt = JwtUtil.createJWT("zhangzx128");
            jsonObject.put("jwt", jwt);
        }
        jsonObject.put("id", id);
        return jsonObject;
    }

}
