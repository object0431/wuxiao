package com.asiainfo.fsip.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("FSIP_MENU_PARAM")
public class FsipMenuParamEntity {

    private String menuId;

    private String menuName;

    private String menuPath;

    private String parentMenuId;

    private String sort;

}
