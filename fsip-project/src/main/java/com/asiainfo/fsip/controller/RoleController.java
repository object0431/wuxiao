package com.asiainfo.fsip.controller;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.asiainfo.fsip.entity.FispStaff2RoleEntity;
import com.asiainfo.fsip.model.*;
import com.asiainfo.fsip.service.RoleService;
import com.asiainfo.mcp.tmc.common.base.annotation.RspResult;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;
import com.asiainfo.mcp.tmc.common.entity.base.PageReq;
import com.asiainfo.mcp.tmc.common.util.ExcelUtil;
import com.asiainfo.mcp.tmc.sso.util.StaffInfoUtil;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/role")
@Api("人员角色管理")
public class RoleController {

    @Resource
    private RoleService roleService;

    @PostMapping("/queryStaffRoleList")
    @ApiOperation("查询角色信息")
    @RspResult
    public PageInfo queryStaffRoleList(@RequestBody PageReq<FispStaff2RoleEntity> req) {
        StaffInfo staffInfo = StaffInfoUtil.getStaff();
        return roleService.queryStaffRoleList(req, staffInfo);
    }

    @PostMapping("/queryStaffRoleListExport")
    @RspResult
    @ApiOperation(value = "角色查询列表导出")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "角色查询列表导出", value = "角色查询列表导出", paramType = "query", dataType = "req")
    })
    public void selPendingRatingListExport(@RequestBody PageReq<FispStaff2RoleEntity> req, HttpServletResponse response) throws Exception {
        StaffInfo staffInfo = StaffInfoUtil.getStaff();
        req.setPageSize(Integer.MAX_VALUE);
        PageInfo<Staff2RoleModel> p = roleService.queryStaffRoleList(req, staffInfo);

        String roleId = req.getReqParam().getRoleId();
        String fileName = "角色管理";
        if("DSGG".equals(roleId)) {
            fileName = "市级工会专干";
        }
        if("SJGG".equals(roleId)) {
            fileName = "省级工会专干";
        }
        if("ZJRC".equals(roleId)) {
            fileName = "专家人才";
        }
        if("SJPWH".equals(roleId)) {
            fileName = "省级评审委员会";
        }
        if("DSPWH".equals(roleId)) {
            fileName = "市级评审委员会";
        }
        if("GHZX".equals(roleId)) {
            fileName = "工会副主席-主席";
        }
        if("GJCGGLY".equals(roleId)) {
            fileName = "国家成果管理专员";
        }
        if("BMJL".equals(roleId)) {
            fileName = "部门领导";
        }
        if("FGLD".equals(roleId)) {
            fileName = "分管领导";
        }

        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName+"导出.xlsx", "UTF-8").replaceAll("\\+", "%20"));
        ExcelUtil.exportData(p.getList(), fileName, Staff2RoleModel.class,response.getOutputStream());
    }

    @PostMapping("/saveStaffRole")
    @ApiOperation("保存员工角色")
    @RspResult
    public void saveStaffRole(@RequestBody List<Staff2RoleModel> req) {
        if (!CollectionUtils.isEmpty(req)) {
            StaffInfo staffInfo = StaffInfoUtil.getStaff();
            roleService.saveStaffRole(req, staffInfo);
        }
    }

    @PostMapping("/deleteStaffRole")
    @ApiOperation("删除员工角色")
    @RspResult
    public void deleteStaffRole(@RequestParam String roleId, @RequestBody List<String> staffIdList) {
        roleService.deleteStaffRole(roleId, staffIdList);
    }

    @PostMapping("/queryCommitteeList")
    @ApiOperation("查询评委会信息")
    @RspResult
    public QueryCommitteeListRsp queryCommitteeList(@RequestBody PageReq<JSONObject> req) {
        StaffInfo staffInfo = StaffInfoUtil.getStaff();
        return roleService.queryCommitteeList(req, staffInfo);
    }
}
