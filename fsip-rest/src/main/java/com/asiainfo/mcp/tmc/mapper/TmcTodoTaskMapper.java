package com.asiainfo.mcp.tmc.mapper;

import com.asiainfo.mcp.tmc.entity.TmcTodoTaskEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TmcTodoTaskMapper extends BaseMapper<TmcTodoTaskEntity> {

    /**
     * 根据taskId更新状态
     */
    int updateByTaskId(@Param("req") TmcTodoTaskEntity tmcTodoTaskEntity);


    /**
     * 根据属性删除待办任务信息
     */
    int deleteByProp(@Param("req") TmcTodoTaskEntity tmcTodoTaskEntity);
}
