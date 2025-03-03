package com.asiainfo.fsip.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.TreeSet;

/**
 * @author wzl
 * @Description:
 * @date 2022/9/14 14:26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuInfoResp implements Comparable<MenuInfoResp>{

    /**
     * 菜单ID
     */
    private String menuId;

    /**
     * 菜单ID
     */
    private String parentMenuId;

    /**
     * 分类名称
     */
    private String label;

    /**
     * 分类菜单地址
     */
    private String path;

    /**
     * 扩展参数
     */
    private String extParam;

    /**
     * 序号
     */
    private String sort;

    /**
     * 分类菜单集合
     */
    private TreeSet<MenuInfoResp> children;

    @Override
    public int compareTo(MenuInfoResp o) {
        return Integer.compare(Integer.parseInt(this.getSort()),Integer.parseInt(o.getSort()));
    }
}
