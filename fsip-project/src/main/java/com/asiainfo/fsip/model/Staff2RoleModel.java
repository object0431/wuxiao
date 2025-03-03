package com.asiainfo.fsip.model;

import com.asiainfo.mcp.tmc.common.annotation.ExcelField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("员工角色信息")
public class Staff2RoleModel {

    @ApiModelProperty(value = "员工编码")
    private String staffId;

    @ApiModelProperty(value = "员工名称")
    @ExcelField(name = "成员名称",columnWidth = 3500)
    private String staffName;

    @ApiModelProperty(value = "公司编码")
    private String companyId;

    @ApiModelProperty(value = "公司名称")
    @ExcelField(name = "公司",columnWidth = 6000)
    private String companyName;

    @ApiModelProperty(value = "部门编码")
    private String deptId;

    @ApiModelProperty(value = "部门名称")
    @ExcelField(name = "所属部门",columnWidth = 7000)
    private String deptName;

    @ApiModelProperty(value = "角色編碼")
    private String roleId;

    @ApiModelProperty(value = "角色名稱")
    private String roleName;

    @ApiModelProperty(value = "专业线编码")
    private String zyxCode;

    @ApiModelProperty(value = "专业线名称")
    @ExcelField(name = "专业线",columnWidth = 3500)
    private String zyxName;

    @ApiModelProperty(value = "拓展属性")
    private List<ExtendInfo> extendInfos;

    @Data
    public static class ExtendInfo{
        @ApiModelProperty(value = "拓展属性类型")
        private String attrType;

        @ApiModelProperty(value = "拓展属性编码")
        private String attrCode;

        @ApiModelProperty(value = "拓展属性值")
        private String attrValue;

        @ApiModelProperty(value = "拓展属性描述")
        private String attrDesc;
    }

}
