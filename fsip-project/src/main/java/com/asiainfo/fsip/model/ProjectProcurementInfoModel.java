package com.asiainfo.fsip.model;

//import com.amazonaws.services.dynamodbv2.xspec.S;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "项目采购model")
public class ProjectProcurementInfoModel {
    @ApiModelProperty("id")
    private Integer id;
    @ApiModelProperty("项目名称")
    private String name;

    @ApiModelProperty("所履区域")
    private String region;

    @ApiModelProperty("所履区域名称")
    private String regionName;

    @ApiModelProperty("招标代理公司")
    private String tenderAgent;

    @ApiModelProperty("代理项目经理")
    private String projectManager;

    @ApiModelProperty("需求经理")
    private String requirementManager;

    @ApiModelProperty("采购经理")
    private String procurementManager;

    @ApiModelProperty("预算金额（万元）")
    private String budgetAmount;

    @ApiModelProperty("采购方式")
    private String procurementMethod;

    @ApiModelProperty("代理服务费（万元）")
    private String agencyServiceFee;

    @ApiModelProperty("与需求对接情况（采购需求时间）")
    private String requirementIntegration;

    @ApiModelProperty("上会情况")
    private List<Meeting> meetings;

    @ApiModelProperty("编制过程情况")
    private List<Drafting> draftings;

    @ApiModelProperty("评审信息")
    private List<Review> reviews;

    @ApiModelProperty("合同签订情况")
    private List<Contract> contracts;

    @ApiModelProperty("需求审批情况")
    private String requirementApproval;

    @ApiModelProperty("方案审批情况")
    private List<String> proposalApproval;

    @ApiModelProperty("项目报名时间")
    private List<List<String>> enrollmentTime;

    @ApiModelProperty("公示期异议情况")
    private Other gsqyyqk;

    @ApiModelProperty("结果审批情况")
    private Other resultApproval;

    @ApiModelProperty("履约保证金缴纳情况")
    private String performanceBondPayment;

    @ApiModelProperty("保证金退还情况")
    private String performanceBondRefund;

    @ApiModelProperty("是否招标/比选失败转谈判")
    private String negotiationFailureToTender;

    @ApiModelProperty("备注")
    private String remarks;


    @Data
    public static class Attach{
        @ApiModelProperty("附件名")
        private String name;
        @ApiModelProperty("附件地址")
        private String path;
    }

    @Data
    public static class Meeting {
        @ApiModelProperty("会议类型")
        private String type;
        @ApiModelProperty("时间")
        private String time;
        @ApiModelProperty("附件")
        private List<Attach> attaches;
    }

    @Data
    public static class Drafting {
        @ApiModelProperty("内容")
        private String content;
        @ApiModelProperty("时间")
        private String time;
        @ApiModelProperty("附件")
        private List<Attach> attaches;
    }

    @Data
    public static class Review {
        @ApiModelProperty("开始时间 yyyy-MM-dd HH:mm:ss")
        private String startTime;
        @ApiModelProperty("结束时间 yyyy-MM-dd HH:mm:ss")
        private String endTime;
        @ApiModelProperty("评委信息")
        private List<Judge> judges;
    }

    @Data
    public static class Judge {
        @ApiModelProperty("评委类型")
        private String judgeType;
        @ApiModelProperty("评委")
        private List<String> judge;
    }
    @Data
    public static class Contract {
        @ApiModelProperty("标段")
        private String section;
        @ApiModelProperty("份额")
        private String share;
        @ApiModelProperty("供应商名称")
        private String supplierName;
        @ApiModelProperty("签订情况")
        private String signingStatus;
        @ApiModelProperty("中标通知书发出日期 yyyy-MM-dd")
        private String bidNoticeDate;
        @ApiModelProperty("合同生效日期 yyyy-MM-dd")
        private String contractEffectiveDate;
        @ApiModelProperty("履约保证金缴纳日期 yyyy-MM-dd")
        private String performanceBondPaymentDate;
    }

    @Data
    public static class Other {
        @ApiModelProperty("内容")
        private String content;
        @ApiModelProperty("附件")
        private List<Attach> attaches;
    }

    @Data
    public static class ListQuery {

        @ApiModelProperty(value = "每页大小")
        private Integer pageSize;

        @ApiModelProperty(value = "页码")
        private Integer pageIndex;

        @ApiModelProperty("所履区域")
        private String region;
        @ApiModelProperty("项目名称")
        private String name;
    }

    @Data
    public static class ListRsp {
        @ApiModelProperty("id")
        private Integer id;
        @ApiModelProperty("项目名称")
        private String name;
        @ApiModelProperty("所履区域")
        private String region;
        @ApiModelProperty("所履区域名称")
        private String regionName;
        @ApiModelProperty("招标代理公司")
        private String tenderAgent;
        @ApiModelProperty("项目经理")
        private String projectManager;
        @ApiModelProperty("需求经理")
        private String requirementManager;
        @ApiModelProperty("采购经理")
        private String procurementManager;
        @ApiModelProperty("预算金额（万元）")
        private BigDecimal budgetAmount;
        @ApiModelProperty("采购方式")
        private String procurementMethod;
    }

    @Data
    public static class DetailReq {
        @ApiModelProperty("id")
        private Integer id;
    }

    @Data
    public static class ProcessInfo {

        @ApiModelProperty("项目进程ID")
        private Integer id;
        @ApiModelProperty("项目采购主ID")
        private Integer infoId;
        @ApiModelProperty("进程时间")
        private String processTime;
        @ApiModelProperty("进程描述")
        private String description;
    }

}
