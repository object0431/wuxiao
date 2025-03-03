package com.asiainfo.fsip.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 组织人员结构
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("组织人员结构")
public class OrganizerStrucRsp {

    @ApiModelProperty("组织编码")
    private String code;

    @ApiModelProperty("组织名称")
    private String displayName;

    @ApiModelProperty("下级部门")
    private List<DeptChildrenBean> children;
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DeptChildrenBean{

        @ApiModelProperty("部门编码")
        private String code;

        @ApiModelProperty("部门名称")
        private String name;

        @ApiModelProperty("展示名称")
        private String displayName;
        private List<DeptChildrenBean> children;
        private List<EmployeeChildrenBean> empChildren;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EmployeeChildrenBean{
        @ApiModelProperty("部门编码")
        private String deptId;

        @ApiModelProperty("部门名称")
        private String deptName;//部门

        @ApiModelProperty("职位")
        private String mainPosition;//职位

        @ApiModelProperty("员工编码")
        private String mainUserId;

        @ApiModelProperty("员工名称")
        private String staffName;

        @ApiModelProperty("员工HR编码")
        private String hrEmpCode;

        @ApiModelProperty("email")
        private String emailAddress;

        @ApiModelProperty("证件编码")
        private String identityNumber;

        @ApiModelProperty("性别")
        private String sex;

        @ApiModelProperty("手机号码")
        private String mobileTel;

        @ApiModelProperty("公司编码")
        private String companyId;

        @ApiModelProperty("公司名称")
        private String companyName;
    }


}
