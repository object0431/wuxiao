package com.asiainfo.mcp.tmc.sso.entity;

import lombok.Data;

import java.util.List;

/**
 * @author wzl
 * @Description:
 * @date 2022/9/14 14:26
 */
@Data
public class TmcLecturerResp {

   /**
    * 编码
    */
   private String lecturerId;
   /**
    * 讲师名称
    */
   private String lecturerName;
   /**
    * 讲师来源
    */
   private String lecturerSource;
   /**
    * 培训供应商
    */
   private String supplier;
   /**
    * 讲师级别
    */
   private String lecturerLevel;

   /**
    * 讲师级别名称
    */
   private String lecturerLevelName;
   /**
    * 性别
    */
   private String sex;
   /**
    * 身份证号
    */
   private String certId;
   /**
    * 手机号
    */
   private String mobilePhone;
   /**
    * 简介
    */
   private String introduction;

   /**
    * 时长
    */
   private String courseHours;
   /**
    * 专业
    */
   private List<TmcLecturerItemResp> itemList;

}
