package com.asiainfo.fsip.mapper.fsip;

import com.asiainfo.fsip.entity.FsipProjectAchievementBaseEntity;
import com.asiainfo.fsip.model.NationalProjectAchievementSelReq;
import com.asiainfo.fsip.model.NationalProjectAchievementSelResp;
import com.asiainfo.fsip.model.ProjectAchievementPushSelReq;
import com.asiainfo.fsip.model.ProjectAchievementPushSelResp;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface FsipProjectAchievementBaseMapper extends BaseMapper<FsipProjectAchievementBaseEntity> {

    @Select("<script>" +
            "SELECT" +
            "  b.ACHIEVEMENT_ID AS achievementId," +
            "  b.AWARD_LEVEL AS awardLevel," +
            "  b.ACHIEVEMENT_TYPE AS achievementType," +
            "  b.APPLY_YEARMON AS applyYearmon," +
            "  b.APPLIER_ID AS applierId," +
            "  a.APPLIER_NAME AS applierName," +
            "  a.APPLIER_COMPANY_ID AS applierCompanyId," +
            "  a.APPLIER_DEPT_ID AS applierDeptId," +
            "  b.APPLY_DATE AS applyDate," +
            "  a.PROJECT_TYPE AS projectType," +
            "  a.PROJECT_NAME AS projectName," +
            "  a.START_DATE AS startDate," +
            "  a.END_DATE AS endDate," +
            "  a.BENEFIT AS benefit," +
            "  a.INNOVATION_TYPE AS innovationType," +
            "  FORMAT(AVG(r.SCORE), 1) AS avgScore" +
            " FROM" +
            "  FSIP_PROJECT_ACHIEVEMENT_BASE b" +
            "  LEFT JOIN FSIP_PROJECT_ACHIEVEMENT a " +
            "  ON (a.ACHIEVEMENT_ID = b.ACHIEVEMENT_ID)" +
            "<if test='req.innovationType != null and req.innovationType !=\"\"'>" +
            "  AND a.INNOVATION_TYPE =  #{req.innovationType}" +
            "</if>" +
            "<if test='req.projectType != null and req.projectType !=\"\"'>" +
            "  AND a.PROJECT_TYPE =  #{req.projectType}" +
            "</if>" +
            " LEFT JOIN FSIP_PROJECT_ACHIEVEMENT_REVIEW r" +
            " ON b.ACHIEVEMENT_ID = r.ACHIEVEMENT_ID " +
            " where 1=1 " +
            "<if test='req.achievementType != null and req.achievementType !=\"\"'>" +
            "  AND b.ACHIEVEMENT_TYPE =  #{req.achievementType}" +
            "</if>" +
            "<if test='req.projectName != null and req.projectName !=\"\"'>" +
            "  AND a.PROJECT_NAME like concat('%',#{req.projectName},'%') " +
            "</if>" +
            "<if test='req.awardLevel != null and req.awardLevel !=\"\"'>" +
            "  AND b.AWARD_LEVEL =  #{req.awardLevel}" +
            "</if>" +
            "<if test='applierCompanyId != null and applierCompanyId !=\"\"'>" +
            "  AND b.APPLIER_COMPANY_ID =  #{applierCompanyId}" +
            "</if>" +
            "<if test='req.achievementId != null and req.achievementId !=\"\"'>" +
            "  AND b.ACHIEVEMENT_ID =  #{req.achievementId}" +
            "</if>" +
            "<if test='req.cityToProvFlag != null and req.cityToProvFlag =\"0\"'>" +
            "  AND (b.city_to_prov_flag ='' OR  b.city_to_prov_flag IS NULL) " +
            "</if>" +
            "<if test='req.cityToProvApplyFlag != null and req.cityToProvApplyFlag =\"1\"'>" +
            " AND (b.appr_node_code is null or b.appr_node_code != 'GHZXSP') " +
            "</if>" +
            " GROUP BY a.ACHIEVEMENT_ID," +
            "  b.ACHIEVEMENT_ID," +
            "  b.AWARD_LEVEL," +
            "  b.ACHIEVEMENT_TYPE," +
            "  b.APPLY_YEARMON," +
            "  b.APPLIER_ID," +
            "  b.APPLIER_NAME," +
            "  b.APPLIER_COMPANY_ID," +
            "  b.APPLIER_DEPT_ID," +
            "  b.APPLY_DATE," +
            "  a.PROJECT_TYPE," +
            "  a.PROJECT_NAME," +
            "  a.START_DATE," +
            "  a.END_DATE," +
            "  a.BENEFIT," +
            "  a.INNOVATION_TYPE" +
            " ORDER BY b.APPLY_DATE DESC" +
            "</script>")
    List<ProjectAchievementPushSelResp> getRatingList(@Param("req") ProjectAchievementPushSelReq req,
                                                      @Param("applierCompanyId") String applierCompanyId);

    @Select("<script>" +
            "SELECT" +
            "  b.ACHIEVEMENT_ID AS achievementId," +
            "  b.AWARD_LEVEL AS awardLevel," +
            "  b.ACHIEVEMENT_TYPE AS achievementType," +
            "  b.APPLY_YEARMON AS applyYearmon," +
            "  b.APPLIER_ID AS applierId," +
            "  a.APPLIER_NAME AS applierName," +
            "  A.APPLIER_COMPANY_ID AS applierCompanyId," +
            "  A.APPLIER_DEPT_ID AS applierDeptId," +
            "  b.APPLY_DATE AS applyDate," +
            "  b.APPR_NODE_CODE AS apprNodeCode," +
            "  b.STATUS AS status," +
            "  b.CITY_TO_PROV_FLAG AS cityToProvFlag," +
            "  b.PENDING_CODE AS pendingCode," +
            "  b.DING_TASK_ID AS dingTaskId," +
            "  a.PROJECT_TYPE AS projectType," +
            "  a.PROJECT_NAME AS projectName," +
            "  a.START_DATE AS startDate," +
            "  a.END_DATE AS endDate," +
            "  a.BENEFIT AS benefit," +
            "  a.INNOVATION_TYPE AS innovationType," +
            "  FORMAT(AVG(r.SCORE), 1) AS avgScore" +
            " FROM" +
            "  FSIP_PROJECT_ACHIEVEMENT_BASE b" +
            "  LEFT JOIN FSIP_PROJECT_ACHIEVEMENT a " +
            "  ON a.ACHIEVEMENT_ID = b.ACHIEVEMENT_ID" +
            // "  AND a.STATUS &lt;&gt; '00'" +
            " LEFT JOIN FSIP_PROJECT_ACHIEVEMENT_REVIEW r" +
            " ON b.ACHIEVEMENT_ID = r.ACHIEVEMENT_ID " +
            " where (b.STATUS='00' and b.CITY_TO_PROV_FLAG is null) " +
            "<if test='pendingCode != null and pendingCode !=\"\"'>" +
            "  and b.PENDING_CODE =  #{pendingCode}" +
            "</if>" +
            " GROUP BY " +
            "  b.ACHIEVEMENT_ID," +
            "  b.AWARD_LEVEL," +
            "  b.ACHIEVEMENT_TYPE," +
            "  b.APPLY_YEARMON," +
            "  b.APPR_NODE_CODE," +
            "  b.STATUS," +
            "  b.APPLIER_ID," +
            "  b.APPLIER_NAME," +
            "  b.APPLIER_COMPANY_ID," +
            "  b.APPLIER_DEPT_ID," +
            "  b.APPLY_DATE," +
            "  b.CITY_TO_PROV_FLAG," +
            "  b.PENDING_CODE," +
            "  b.DING_TASK_ID," +
            "  a.PROJECT_TYPE," +
            "  a.PROJECT_NAME," +
            "  a.START_DATE," +
            "  a.END_DATE," +
            "  a.BENEFIT," +
            "  a.INNOVATION_TYPE" +
            " ORDER BY b.APPLY_DATE DESC" +
            "</script>")
    List<ProjectAchievementPushSelResp> getCity2ProvAuditAchievement(@Param("pendingCode") String pendingCode);


    @Select("<script>" +
            "SELECT" +
            "  b.ACHIEVEMENT_ID AS achievementId," +
            "  b.ACHIEVEMENT_TYPE AS achievementType," +
            "  b.APPLIER_COMPANY_ID AS applierCompanyId," +
            "  b.APPLIER_DEPT_ID AS applierDeptId," +
            "  b.APPLY_YEARMON AS applyYearmon," +
            "  b.APPLIER_ID AS applierId," +
            "  b.APPLIER_NAME AS applierName," +
            "  b.MAIN_CREATE_NAME AS mainCreateName," +
            "  b.OTHER_CREATE_NAME AS otherCreateName," +
            "  b.AWARDS_PROJECT_NAME_LEVEL AS awardsProjectNameLevel," +
            "  b.APPLY_DATE AS applyDate," +
            "  a.APPLIER_NAME AS applierName," +
            "  a.BACK_IMAGE AS backImage," +
            "  a.PROJECT_NAME AS projectName," +
            "  (SELECT" +
            "    ITEM_VALUE" +
            "  FROM" +
            "    fsip_project_achievement_item t" +
            "  WHERE t.ACHIEVEMENT_ID = a.ACHIEVEMENT_ID" +
            "    AND t.ITEM_TYPE = 'ACHIEVEMENT'" +
            "    AND t.ITEM_CODE = 'CGJJ'" +
            "  LIMIT 0, 1) AS projectIntroduce" +
            "  FROM FSIP_PROJECT_ACHIEVEMENT_BASE b,FSIP_PROJECT_ACHIEVEMENT a " +
            "    WHERE a.ACHIEVEMENT_ID = b.ACHIEVEMENT_ID AND ACHIEVEMENT_TYPE ='NATIONAL' " +
            "<if test='req.projectName != null and req.projectName !=\"\"'>" +
            "  AND a.PROJECT_NAME like concat('%', #{req.projectName},'%')" +
            "</if>" +
            "<if test='req.mainCreateName != null and req.mainCreateName !=\"\"'>" +
            "  AND b.MAIN_CREATE_NAME = like concat('%', #{req.mainCreateName},'%')" +
            "</if>" +
            "<if test='req.awardsProjectNameLevel != null and req.awardsProjectNameLevel !=\"\"'>" +
            "  AND b.AWARDS_PROJECT_NAME_LEVEL like concat('%', #{req.awardsProjectNameLevel},'%')" +
            "</if>" +
            "<if test='req.achievementId != null and req.achievementId !=\"\"'>" +
            "  AND b.ACHIEVEMENT_ID = #{req.achievementId}" +
            "</if>" +
            "<if test='req.cycle != null and req.cycle !=\"\"'>" +
            "  AND b.APPLY_YEARMON =  #{req.cycle}" +
            "</if>" +
            " ORDER BY b.APPLY_DATE DESC" +
            "</script>")
    List<NationalProjectAchievementSelResp> getNationalProjectAchievementList(@Param("req") NationalProjectAchievementSelReq req);

    @Select("<script>" +
            "SELECT" +
            "  B.ACHIEVEMENT_ID AS achievementId," +
            "  B.ACHIEVEMENT_TYPE AS achievementType," +
            "  A.APPLIER_COMPANY_ID AS applierCompanyId," +
            "  A.APPLIER_DEPT_ID AS applierDeptId," +
            "  B.APPLY_YEARMON AS applyYearmon," +
            "  B.MAIN_CREATE_NAME AS mainCreateName," +
            "  B.OTHER_CREATE_NAME AS otherCreateName," +
            "  B.AWARDS_PROJECT_NAME_LEVEL AS awardsProjectNameLevel," +
            "  B.AWARD_LEVEL AS awardLevel," +
            "  B.APPLY_DATE AS applyDate," +
            "  A.APPLIER_ID AS applierId," +
            "  A.APPLIER_NAME AS applierName," +
            "  A.BACK_IMAGE AS backImage," +
            "  A.PROJECT_NAME AS projectName" +
            "  FROM FSIP_PROJECT_ACHIEVEMENT_BASE b,FSIP_PROJECT_ACHIEVEMENT a " +
            "    WHERE B.ACHIEVEMENT_ID = A.ACHIEVEMENT_ID " +
            "  AND (B.ACHIEVEMENT_TYPE = 'CITY' AND B.APPLIER_COMPANY_ID = #{companyId} OR B.ACHIEVEMENT_TYPE IN ('PROV','NATIONAL'))" +
            " ORDER BY B.APPLY_DATE DESC" +
            "</script>")
    List<FsipProjectAchievementBaseEntity> selectByProp(@Param("companyId") String companyId);

    @Select("<script>" +
            "SELECT COUNT(DISTINCT A.ACHIEVEMENT_ID) AS TOTAL" +
            " FROM FSIP_PROJECT_ACHIEVEMENT_BASE A, FSIP_PROJECT_ACHIEVEMENT B" +
            " WHERE A.ACHIEVEMENT_ID = B.ACHIEVEMENT_ID AND A.APPLY_YEARMON &gt;= #{startMonth}" +
            " AND A.APPLY_YEARMON &lt;= #{endMonth} AND B.APPLIER_COMPANY_ID = #{companyId} AND (A.ACHIEVEMENT_ID IN " +
            "<foreach collection=\"achievementIdList\" item=\"value\" separator=\",\" open=\"(\" close=\")\">" +
            "  #{value}" +
            "</foreach>" +
            " OR A.CITY_TO_PROV_FLAG = '1' OR A.APPR_NODE_CODE = 'GHZXSP')" +
            "</script>")
    int countCity2ProvTotal(List<String> achievementIdList, String startMonth, String endMonth, String companyId);

}
