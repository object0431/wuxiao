package com.asiainfo.mcp.tmc.sso.mapper;

import com.asiainfo.mcp.tmc.sso.entity.TmcPermissionInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SsoTmcPermissionMapper extends BaseMapper<TmcPermissionInfoEntity> {

    @Results(id = "tableMap", value = {@Result(column = "STAFF_ID",property = "staffId")
            , @Result(column = "STAFF_NAME",property = "staffName")
            , @Result(column = "DEPT_NO",property = "deptNo")
            , @Result(column = "COMPANY_NO",property = "companyNo")
            , @Result(column = "PERM_TYPE",property = "permType")
    })
    @Select("SELECT STAFF_ID, STAFF_NAME, DEPT_NO, COMPANY_NO, PERM_TYPE FROM TMC_PERMISSION_INFO WHERE STAFF_ID = #{staffId} ORDER BY PERM_TYPE")
    List<TmcPermissionInfoEntity> selectByStaffId(String staffId);
}
