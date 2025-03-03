package com.asiainfo.fsip.mapper.fsip;


import com.asiainfo.fsip.entity.FsipProjectInitiationEntity;
import com.asiainfo.fsip.model.ProjectStatisticsProTypeModel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FsipProjectInitiationMapper extends BaseMapper<FsipProjectInitiationEntity> {

    @Results(id = "tableMap", value = {@Result(column = "APPLIER_DEPT_ID",property = "applierDeptId")
            , @Result(column = "TOTAL",property = "total")
    })
    @Select("SELECT APPLIER_DEPT_ID, COUNT(*) TOTAL\n" +
            "FROM FSIP_PROJECT_INITIATION\n" +
            "WHERE STATUS = '00' AND APPLY_DATE >= STR_TO_DATE(#{startDate}, '%Y%m%d')\n" +
            "   AND APPLY_DATE < STR_TO_DATE(#{endDate}, '%Y%m%d')\n" +
            " GROUP BY APPLIER_DEPT_ID ORDER BY APPLIER_DEPT_ID")
    List<FsipProjectInitiationEntity> countByDeptIdAndTime(String startDate, String endDate);

    List<ProjectStatisticsProTypeModel> findProjectStatisticsInitiation(@Param("startDate") String startDate, @Param("endDate") String endDate);
}
