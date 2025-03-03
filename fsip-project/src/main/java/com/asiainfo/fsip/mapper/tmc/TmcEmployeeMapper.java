package com.asiainfo.fsip.mapper.tmc;

import com.asiainfo.mcp.tmc.entity.MiniUserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface TmcEmployeeMapper extends BaseMapper<MiniUserEntity> {

    List<MiniUserEntity> selectByProp(String staffName, String serialNumber, List<String> orgCodeList);

    List<MiniUserEntity> selectDeptLeader(String deptId);

    List<MiniUserEntity> selectByStaffId(List<String> staffIdList);

    int countByDeptId(List<String> orgCodeList);

    List<MiniUserEntity> selectAll();

    List<MiniUserEntity> selectUidEmpNameByStaffId(@Param("ids") List<String> staffIdList);
}
