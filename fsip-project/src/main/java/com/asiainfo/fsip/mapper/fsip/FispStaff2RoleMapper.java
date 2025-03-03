package com.asiainfo.fsip.mapper.fsip;

import com.asiainfo.fsip.entity.FispStaff2RoleEntity;
import com.asiainfo.fsip.model.Staff2RoleModel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FispStaff2RoleMapper extends BaseMapper<FispStaff2RoleEntity> {

    List<Staff2RoleModel> selectByProp(FispStaff2RoleEntity req);

    List<Staff2RoleModel> selectByStaffId(String staffId);

    int batchInsert(@Param("staffRoleList") List<FispStaff2RoleEntity> staffRoleList);

    int deleteByRoleIdAndStaffIds(String roleId, List<String> staffIdList);


    @Select("<script>" +
            "SELECT a.role_id roleId,a.staff_id staffId,a.staff_name staffName,a.dept_id deptId,a.dept_name deptName,a.company_id companyId,a.operator_id operatorId,b.ATTR_CODE attrCode,b.ATTR_VALUE attrValue FROM fsip_staff_2_role a " +
            "LEFT JOIN fsip_staff_2_role_extend b " +
            "ON a.STAFF_ID=b.STAFF_ID AND a.ROLE_ID=b.ROLE_ID AND b.ATTR_TYPE='ZYX' " +
            "WHERE a.ROLE_ID = #{roleId} " +
            "<if test='companyId != null and companyId != \"\"'>" +
            "AND a.COMPANY_ID = #{companyId} " +
            "</if>" +
            "</script>")
    List<FispStaff2RoleEntity> select(FispStaff2RoleEntity req);
}
