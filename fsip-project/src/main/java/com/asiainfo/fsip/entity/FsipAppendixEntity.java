package com.asiainfo.fsip.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("fsip_appendix")
public class FsipAppendixEntity {
    @TableId("ID")
    private Long id;

    @TableField("EXT_ID")
    private String extId;

    @TableField("OSS_FILE_NAME")
    private String ossFileName;

    @TableField("FILE_PATH")
    private String filePath;

    @TableField("APPLY_DATE")
    private Date applyDate;
}
