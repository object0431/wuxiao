package com.asiainfo.mcp.tmc.sso.entity;

import lombok.Data;

/**
 * @author wzl
 * @Description:
 * @date 2022/9/14 14:26
 */
@Data
public class TmcLecturerItemResp {

   private String lecturerId;
   private String attrType;
   private String attrCode;
   private String attrValue;
   private String attrDesc;

}
