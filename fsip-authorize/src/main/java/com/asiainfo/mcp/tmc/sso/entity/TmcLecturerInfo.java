package com.asiainfo.mcp.tmc.sso.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;

/**
 * @author wzl
 * @Description:
 * @date 2022/9/14 14:26
 */
@Data
@TableName("tmc_lecturer_info")
public class TmcLecturerInfo {

   /**
    * 编码
    */
   @TableId
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
    * 专业
    */
   @TableField(exist = false)
   private List<TmcLecturerItem> itemList;

   /**
    * 专业编码
    */
   @TableField(exist = false)
   private List<String> codeList;

}
