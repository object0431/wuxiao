package com.asiainfo.fsip.mapper.fsip;

import com.asiainfo.fsip.entity.FsipProjectAchievementEntity;
import com.asiainfo.fsip.model.*;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface FsipProjectAchievementMapper extends BaseMapper<FsipProjectAchievementEntity> {

    @Select("<script>" +
            "SELECT" +
            " a.ACHIEVEMENT_ID AS achievementId," +
            " a.PROJECT_NAME AS projectName," +
            " a.START_DATE AS startDate," +
            " a.END_DATE AS endDate," +
            " a.BENEFIT AS benefit," +
            " a.INNOVATION_TYPE AS innovationType," +
            " a.PROJECT_TYPE AS projectType," +
            " a.APPLIER_ID AS applierId," +
            " a.APPLIER_NAME AS applierName," +
            " a.APPLIER_COMPANY_ID AS applierCompanyId," +
            " a.APPLIER_DEPT_ID AS applierDeptId," +
            " a.APPLY_DATE AS applyDate," +
            " a.CITY_TO_PROV_FLAG AS cityToProvFlag," +
/*            " b.AWARD_LEVEL AS awardLevel," +
            " b.ACHIEVEMENT_TYPE AS achievementType," +*/
            " '' AS awardLevel," +
            " '' AS achievementType," +
            " ROUND(AVG(r.SCORE), 1) AS avgScore" +
            " FROM" +
            " FSIP_PROJECT_ACHIEVEMENT a " +
            " JOIN FSIP_PROJECT_ACHIEVEMENT_REVIEW r " +
            " ON (a.ACHIEVEMENT_ID = r.ACHIEVEMENT_ID)" +
            "<if test='isSelCityToProvFlag != null and isSelCityToProvFlag ==\"0\"'>" +
            " AND r.CITY_TO_PROV_FLAG IS NULL"+
            "</if>" +
            "<if test='isSelCityToProvFlag != null and isSelCityToProvFlag ==\"1\"'>" +
            " AND r.CITY_TO_PROV_FLAG = '1'" +
            "</if>" +
/*            " LEFT JOIN FSIP_PROJECT_ACHIEVEMENT_BASE B " +
            " ON (a.ACHIEVEMENT_ID = B.ACHIEVEMENT_ID)" +*/
            "  WHERE a.STATUS IN ('00')" +
            "<if test='startDate != null and startDate !=\"\"'>" +
            " AND a.APPLY_DATE &gt;= #{startDate}"+
            "</if>" +
            "<if test='endDate != null and endDate !=\"\"'>" +
            " AND a.APPLY_DATE &lt;= #{endDate}"+
            "</if>" +
            "<if test='isSelCityToProvFlag != null and isSelCityToProvFlag ==\"0\"'>" +
            " AND a.APPLIER_COMPANY_ID =  #{applierCompanyId} AND a.CITY_TO_PROV_FLAG IS NULL"+
            "</if>" +
            "<if test='isSelCityToProvFlag != null and isSelCityToProvFlag ==\"1\"'>" +
            " AND a.CITY_TO_PROV_FLAG = '1'" +
            "</if>" +
            "<if test='req.achievementId != null and req.achievementId !=\"\"'>" +
            "  AND a.ACHIEVEMENT_ID =  #{req.achievementId}" +
            "</if>" +
            "<if test='req.innovationType != null and req.innovationType !=\"\"'>" +
            "  AND a.INNOVATION_TYPE =  #{req.innovationType}" +
            "</if>" +
            "<if test='req.projectName != null and req.projectName !=\"\"'>" +
            "  AND a.PROJECT_NAME like concat('%',#{req.projectName},'%') " +
            "</if>" +
            "<if test='req.projectType != null and req.projectType !=\"\"'>" +
            "  AND a.PROJECT_TYPE =  #{req.projectType}" +
            "</if>" +
            " and not exists (select 1 from fsip_project_achievement_base where a.achievement_id= achievement_id and achievement_type= #{req.achievementType}) "+
            " GROUP BY a.ACHIEVEMENT_ID," +
            " a.PROJECT_NAME," +
            " a.START_DATE," +
            " a.END_DATE," +
            " a.BENEFIT," +
            " a.INNOVATION_TYPE," +
            " a.CITY_TO_PROV_FLAG," +
            " a.PROJECT_TYPE," +
            " a.APPLIER_ID," +
            " a.APPLIER_NAME," +
            " a.APPLIER_COMPANY_ID," +
            " a.APPLIER_DEPT_ID," +
            " a.APPLY_DATE" +
/*            " b.AWARD_LEVEL," +
            " b.ACHIEVEMENT_TYPE " +*/
            " order by a.APPLY_DATE desc " +
            "</script>")
    List<ProjectAchievementPushSelResp> getPendingRatingSelList(@Param("req") ProjectAchievementPushSelReq req,
                                                                @Param("applierCompanyId")  String applierCompanyId,
                                                                @Param("startDate") String startDate,
                                                                @Param("endDate") String endDate,
                                                                @Param("isSelCityToProvFlag") String isSelCityToProvFlag);

    @Select("<script>" +
            "SELECT" +
            "  T.APPLIER_COMPANY_ID AS applierCompanyId," +
            "  T.APPLIER_DEPT_ID AS applierDeptId," +
            "  T.APPLIER_ID AS applierId," +
            "  T.APPLIER_NAME AS applierName," +
            "  SUM(" +
            "    CASE" +
            "      WHEN T.TYPE = 'FPI'" +
            "      THEN T.TOTAL" +
            "      ELSE 0" +
            "    END" +
            "  ) applyProjectTotal " +
            " ,SUM(CASE WHEN T.TYPE = 'FPA' THEN T.TOTAL ELSE 0 END) auditProjectTotal, FORMAT(AVG(T.SCORE),1) AS avgScore" +
            " FROM" +
            "  (SELECT" +
            "    a.APPLIER_COMPANY_ID," +
            "    a.APPLIER_DEPT_ID," +
            "    a.APPLIER_ID," +
            "    a.APPLIER_NAME," +
            "    COUNT(*) total," +
            "    0 AS SCORE," +
            "    'FPI' AS TYPE" +
            "  FROM" +
            "    fsip_project_initiation a" +
            "  WHERE a.STATUS = '00'" +
            "<if test='req.applierCompanyId != null and req.applierCompanyId !=\"\"'>" +
            "  AND a.APPLIER_COMPANY_ID =  #{applierCompanyId}" +
            "</if>" +
            "<if test='req.applierDeptId != null and req.applierDeptId !=\"\"'>" +
            "  AND a.APPLIER_DEPT_ID =  #{req.applierDeptId}" +
            "</if>" +
            "<if test='req.applierId != null and req.applierId !=\"\"'>" +
            "  AND a.APPLIER_ID =  #{req.applierId}" +
            "</if>" +
            " GROUP BY a.APPLIER_COMPANY_ID,\n" +
            "      a.APPLIER_DEPT_ID,\n" +
            "      a.APPLIER_ID,\n" +
            "      a.APPLIER_NAME" +
            "  UNION" +
            "  ALL" +
            "  SELECT" +
            "    C.APPLIER_COMPANY_ID," +
            "    C.APPLIER_DEPT_ID," +
            "    C.APPLIER_ID," +
            "    C.APPLIER_NAME," +
            "    COUNT(distinct c.ACHIEVEMENT_ID) total," +
            "    AVG(SCORE) AS SCORE," +
            "    'FPA' AS TYPE" +
            "  FROM" +
            "    fsip_project_achievement c" +
            "    LEFT JOIN fsip_project_achievement_base b" +
            "      ON (" +
            "        c.ACHIEVEMENT_ID = b.ACHIEVEMENT_ID" +
            "      )" +
            "    LEFT JOIN fsip_project_achievement_review r" +
            "      ON (" +
            "        c.ACHIEVEMENT_ID = r.ACHIEVEMENT_ID" +
            "      )" +
            "  WHERE c.STATUS = '00'  " +
            "<if test='req.applierCompanyId != null and req.applierCompanyId !=\"\"'>" +
            "  AND c.APPLIER_COMPANY_ID =  #{applierCompanyId}" +
            "</if>" +
            "<if test='req.applierDeptId != null and req.applierDeptId !=\"\"'>" +
            "  AND c.APPLIER_DEPT_ID =  #{req.applierDeptId}" +
            "</if>" +
            "<if test='req.applierId != null and req.applierId !=\"\"'>" +
            "  AND c.APPLIER_ID =  #{req.applierId}" +
            "</if>" +
            "  GROUP BY C.APPLIER_COMPANY_ID," +
            "    C.APPLIER_DEPT_ID," +
            "    C.APPLIER_NAME," +
            "    C.APPLIER_ID) T" +
            "  WHERE t.APPLIER_COMPANY_ID IS NOT NULL AND t.APPLIER_COMPANY_ID &lt;&gt; ''" +
            " GROUP BY T.APPLIER_COMPANY_ID," +
            "  T.APPLIER_DEPT_ID," +
            "  T.APPLIER_NAME," +
            "  T.APPLIER_ID" +
            "</script>")
    List<ProjectAchievementStatisticsResp> getProjectAchievementStatistics(@Param("req") ProjectAchievementStatisticsReq req,
                                                                           @Param("applierCompanyId")  String applierCompanyId);

    @Results(id = "tableMap", value = {@Result(column = "APPLIER_DEPT_ID",property = "applierDeptId")
            , @Result(column = "TOTAL",property = "total")
    })
    @Select("SELECT APPLIER_DEPT_ID, COUNT(*) TOTAL\n" +
            "FROM FSIP_PROJECT_ACHIEVEMENT\n" +
            "WHERE STATUS IN ('04','03','00') AND APPLY_DATE >= STR_TO_DATE(#{startDate}, '%Y%m%d')\n" +
            "   AND APPLY_DATE < STR_TO_DATE(#{endDate}, '%Y%m%d')\n" +
            " GROUP BY APPLIER_DEPT_ID ORDER BY APPLIER_DEPT_ID")
    List<FsipProjectAchievementEntity> countByDeptId(@Param("startDate") String startDate, @Param("endDate") String endDate);

    List<ProjectStatisticsProTypeModel> findProjectStatisticsAcheTotal(@Param("startDate") String startDate, @Param("endDate") String endDate);

    List<FsipProjectAchievementEntity> selectPush2CommentById(List<String> projectIds, String city2Prov);

    List<FsipProjectAchievementEntity> selectPendingPushByProp(@Param("req") AchievementPushQryReq req);

    List<FsipProjectAchievementEntity> selectPushedReviewByProp(@Param("req") AchievementPushQryReq req);

    List<FsipProjectAchievementEntity> selectPendingCommitByProp(@Param("req") AchievementPushQryReq req);


}
