package com.asiainfo.fsip.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("FSIP_MENU_2_ROLE")
public class FispMenu2RoleEntity {
    @TableId("MENU_ID")
    private String menuId;

    @TableField("ROLE_ID")
    private String roleId;

}
