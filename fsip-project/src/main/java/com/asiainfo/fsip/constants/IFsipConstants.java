package com.asiainfo.fsip.constants;

/**
 * 常量类
 */
public interface IFsipConstants {

    String FSIP_CACHE_PARAM_MAP = "FSIP_CACHE_PARAM_MAP";

    interface ApprovalLevel {

        //部门领导审批
        String APPROVAL_LEVEL_DEPT_LEADER = "BMJL";
    }

    interface TaskType {
        //项目立项申请
        String LXSQ = "LXSQ";

        //项目成果申请
        String CGSQ = "CGSQ";

        //地市转省分成果申请
        String SJCGZSJ = "SJCGZSJ";

        //咨询工单
        String ZXGD = "ZXGD";
    }

    interface Status {
        //暂存
        String ZC = "ZC";
        //部门领导审批
        String BMLDSP = "01";
        //分管领导审批
        String FGBMLD = "02";
        //工会主席审批
        String GHZX = "03";
        //评审委员会审批
        String PSWYH = "04";
        //转发他人审批
        String ZFTRSP = "05";
        //审批完成
        String FINISH = "00";

        //市级转省级
        String CITY2PROV = "SZS";

        //退回修改
        String TH = "TH";
        //撤回
        String CH = "CH";

        //省级归档为地市级
        String PROV2CITY = "12";

        //地市级归档为部门级
        String CITY2DEPT = "11";
    }

    //审核结果 TG=通过、BH=驳回
    String APPROVAL_RET_TG = "TG";
    String APPROVAL_RET_BH = "BH";
    //状态
    String STATE = "STATE";

    //任务类型
    String TASK_TYPE = "TASK_TYPE";
    //项目成果
    String ACHIEVEMENT = "ACHIEVEMENT";

    interface NodeCode {
        String ZFTRSP = "ZFTRSP";
        //评审委员会
        String PSWYH = "PSWYH";
    }

    interface RoleId {
        //部门经理
        String BMJL = "BMJL";

        //管理员
        String GWL = "GWL";

        //省级专干
        String SJGG = "SJGG";

        //专家人才
        String ZJRC = "ZJRC";
    }

    interface AchievementType {

        String NATIONAL = "NATIONAL";
        //评审委员会
        String PROV = "PROV";

        String CITY="CITY";

        String DEPT="DEPT";
    }

    interface StaticParamType{
        // 项目类型
        String XMLX = "XMLX";

        // 创新类型
        String CXLX = "CXLX";
    }

    //转发他人审批
    String ZFTRSP = "ZFTRSP";
}
