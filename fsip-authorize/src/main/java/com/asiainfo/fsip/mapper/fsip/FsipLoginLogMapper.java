package com.asiainfo.fsip.mapper.fsip;

import com.asiainfo.mcp.tmc.sso.entity.FsipLoginLogEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface FsipLoginLogMapper extends BaseMapper<FsipLoginLogEntity> {

    @Select("SELECT COMPANY_ID, COUNT(DISTINCT LOGIN_ACCOUNT) TOTAL\n" +
            "        FROM FSIP_LOGIN_LOG\n" +
            "        WHERE LOGIN_TIME >=DATE_SUB(DATE_FORMAT(NOW(), '%Y-%m-01 00:00:00'), INTERVAL 1 MONTH)\n" +
            "          AND LOGIN_TIME < DATE_FORMAT(NOW(), '%Y-%m-01 00:00:00')")
    List<Map<String, Object>> countLastMonthLoginData();

    @Results(id = "tableMap", value = {@Result(column = "DEPT_ID",property = "deptId")
            , @Result(column = "TOTAL",property = "total")
    })
    @Select("SELECT DEPT_ID, COUNT(DISTINCT LOGIN_ACCOUNT) TOTAL\n" +
            "FROM FSIP_LOGIN_LOG\n" +
            "WHERE LOGIN_TIME >= STR_TO_DATE(#{startDate}, '%Y%m%d')\n" +
            "   AND LOGIN_TIME < STR_TO_DATE(#{endDate}, '%Y%m%d')\n" +
            " GROUP BY DEPT_ID ORDER BY DEPT_ID")
    List<FsipLoginLogEntity> countDeptLoginSData(String startDate, String endDate);

    @Select("SELECT DEPT_ID, COUNT(DISTINCT LOGIN_ACCOUNT) TOTAL\n" +
            "FROM FSIP_LOGIN_LOG\n" +
            "WHERE LOGIN_TIME >= STR_TO_DATE(#{startDate}, '%Y%m%d')\n" +
            "   AND LOGIN_TIME < STR_TO_DATE(#{endDate}, '%Y%m%d')\n" +
            " GROUP BY DEPT_ID ORDER BY DEPT_ID")
    List<FsipLoginLogEntity> selectByLoginAccount(String startDate, String endDate);

    @Select("SELECT * FROM FSIP_LOGIN_LOG A WHERE NOT EXISTS " +
            "(SELECT 1 FROM FSIP_LOGIN_INITIATION_STAT B WHERE A.DEPT_ID = B.DEPT_ID AND b.LOGIN_NUM > 0) \n" +
            "AND LOGIN_TIME >= STR_TO_DATE(#{startDate}, '%Y%m%d')")
    List<FsipLoginLogEntity> selectPendingDate(String startDate);

}
