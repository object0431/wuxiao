package com.asiainfo.mcp.tmc.controller;


import com.asiainfo.mcp.tmc.common.entity.base.BaseRsp;
import com.asiainfo.mcp.tmc.common.entity.pending.PendingEntity;
import com.asiainfo.mcp.tmc.common.entity.pending.PendingUpEntity;
import com.asiainfo.mcp.tmc.common.exception.RspResultException;
import com.asiainfo.mcp.tmc.service.TaskServive;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author pig
 * @since 2021-03-20
 */
@RestController
@RequestMapping("/task")
@Slf4j
public class TaskController {

    @Resource
    private TaskServive taskServive;

    @ApiOperation(value = "待办新增", notes = "待办新增", produces = "application/json")
    @PostMapping("/addPending")
    public BaseRsp<Void> addPending(@RequestBody PendingEntity[] data) {
        if(data== null || data.length == 0){
            throw new RspResultException("请求参数不能为空");
        }
        return taskServive.addPendingTask(data);
    }

    @ApiOperation(value = "待办更新", notes = "待办更新", produces = "application/json")
    @PostMapping("/updatePendingStatus")
    public BaseRsp<Void> updatePendingStatus(@RequestBody PendingUpEntity[] data) {
        if(data== null || data.length == 0){
            throw new RspResultException("请求参数不能为空");
        }
        return taskServive.updatePendingStatus(data);
    }

}
