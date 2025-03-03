package com.asiainfo.fsip.model;

import com.asiainfo.mcp.tmc.common.annotation.ExcelField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectQueryRsp {
    private long pageNum;
    private long pageSize;
    private long total;
    private List<RspData> dataList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class RspData{
        private String projectId;
        @ExcelField(name = "项目名称",columnWidth = 8000,sort = 1)
        private String projectName;
        @ExcelField(name = "所属类别",columnWidth = 8000,sort = 2)
        private String typeAttrName;
        private String projectAttrCode;
        @ExcelField(name = "所属项目",columnWidth = 8000,sort = 3)
        private String projectAttrName;
        private String applier;
        @ExcelField(name = "申请人",columnWidth = 8000,sort = 4)
        private String applierName;
        private String applyCompany;
        @ExcelField(name = "公司",columnWidth = 8000,sort = 5)
        private String applyCompanyName;
        private String applyDept;
        @ExcelField(name = "部门",columnWidth = 8000,sort = 6)
        private String applyDeptName;
        @ExcelField(name = "预期效益（万元）",columnWidth = 8000,sort = 7)
        private String economicBenefit;
        @ExcelField(name = "状态",columnWidth = 8000,sort = 8)
        private String status;
        private String startTime;
        private String endTime;
        private String typeAttrCode;
        private List<ProjectAttr> projectAttrList;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ProjectAttr{
        private String attrType;
        private String attrCode;
        private String attrName;
        private String attrValue;
    }
}
