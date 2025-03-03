package com.asiainfo.fsip.mapper.fsip;

import com.asiainfo.fsip.entity.FsipInnovationIssuesEntity;
import com.asiainfo.fsip.model.InnovationIssuesListReq;
import com.asiainfo.fsip.model.InnovationIssuesListResp;
import com.asiainfo.fsip.model.ProjectAchievementPushSelReq;
import com.asiainfo.fsip.model.ProjectAchievementPushSelResp;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FsipInnovationIssuesMapper extends BaseMapper<FsipInnovationIssuesEntity> {
    @Select("<script>" +
            "SELECT" +
            "  t.ISSUES_ID as issuesId," +
            "  t.ISSUES_TITLE as issuesTitle," +
            "  t.CONTENT as content," +
            "  t.APPLIER_ID as applierId," +
            "  t.APPLIER_NAME as applierName," +
            "  t.APPLIER_COMPANY_id as applierCompanyId," +
            "  t.APPLIER_DEPT_ID as applierDeptId," +
            "  t.APPLY_DATE as applyDate," +
            "  t.CAN_JOIN as canJoin," +
            "  t.PARTNER_NUM as partnerNum," +
            "  (SELECT COUNT(f.staff_id) FROM fsip_innovation_issues_follow f WHERE t.ISSUES_ID= f.ISSUES_ID) AS followCount," +
            "  (SELECT COUNT(e.staff_id) FROM fsip_innovation_issues_evaluate e WHERE t.ISSUES_ID= e.ISSUES_ID AND e.evaluate_type='LIKE') AS likeCount," +
            "  (SELECT COUNT(e1.staff_id) FROM fsip_innovation_issues_evaluate e1 WHERE t.ISSUES_ID= e1.ISSUES_ID AND e1.evaluate_type='DISLIKE') AS disLikeCount," +
            "  (SELECT COUNT(c.staff_id) FROM fsip_innovation_issues_comment c WHERE t.ISSUES_ID= c.ISSUES_ID) AS commentCount" +
            " FROM" +
            "  fsip_innovation_issues t" +
            "<if test='req.selType ==\"01\"'>" +
            "<if test='req.selfSelType ==\"01\"'>" +
            " WHERE t.APPLIER_ID = #{req.applierId} " +
            "</if>" +
            "<if test='req.selfSelType ==\"02\"'>" +
            " WHERE t.ISSUES_ID IN (SELECT DISTINCT p.ISSUES_ID FROM FSIP_INNOVATION_ISSUES_PARTNER p WHERE p.ISSUES_ID = t.ISSUES_ID AND t.APPLIER_ID = p.PARTNER_ID AND p.PARTNER_ID = #{req.applierId}) " +
            "</if>" +
            "<if test='req.selfSelType ==\"03\"'>" +
            " WHERE t.ISSUES_ID IN (SELECT DISTINCT f.ISSUES_ID FROM FSIP_INNOVATION_ISSUES_FOLLOW f WHERE f.ISSUES_ID = t.ISSUES_ID AND t.APPLIER_ID = f.STAFF_ID AND f.STAFF_ID = #{req.applierId}) " +
            "</if>" +
            "<if test='req.issuesTitle != null and req.issuesTitle !=\"\"'>" +
            " AND t.ISSUES_TITLE like concat('%', #{req.issuesTitle},'%')" +
            "</if>" +
            "</if>" +
            "<if test='req.selType ==\"02\"'>" +
            " where t.ISSUES_ID IN" +
            "  (SELECT" +
            "    s.ISSUES_ID" +
            "  FROM" +
            "    fsip_innovation_issues_scope s" +
            "  WHERE (" +
            "      s.SCOPE_TYPE = '01'" +
            "      AND s.SCOPE_ID = t.APPLIER_DEPT_ID" +
            "      AND s.ISSUES_ID = t.ISSUES_ID" +
            "      AND s.SCOPE_ID = #{req.applierDeptId}" +
            "    )" +
            "    OR (" +
            "      s.SCOPE_TYPE = '02'" +
            "      AND s.SCOPE_ID = t.APPLIER_ID" +
            "      AND s.ISSUES_ID = t.ISSUES_ID" +
            "      AND s.SCOPE_ID = #{req.applierId}" +
            "    )) "+
            "<if test='req.issuesTitle != null and req.issuesTitle !=\"\"'>" +
            " AND t.ISSUES_TITLE like concat('%', #{req.issuesTitle},'%')" +
            "</if>" +
            "</if>" +
            " order by t.APPLY_DATE desc "+
            "</script>")
    List<InnovationIssuesListResp> getIssuesList(@Param("req") InnovationIssuesListReq req);

}


