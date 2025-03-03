package com.asiainfo.fsip.service.impl;

import com.asiainfo.fsip.entity.FsipMenuParamEntity;
import com.asiainfo.fsip.mapper.fsip.FispMenu2RoleMapper;
import com.asiainfo.fsip.mapper.fsip.FispStaff2RoleMapper;
import com.asiainfo.fsip.mapper.fsip.FsipMenuMapper;
import com.asiainfo.fsip.model.MenuInfoResp;
import com.asiainfo.fsip.model.Staff2RoleModel;
import com.asiainfo.fsip.service.MenuService;
import com.asiainfo.mcp.tmc.common.consts.IConstants;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MenuServiceImpl implements MenuService {

    @Resource
    private FsipMenuMapper fsipMenuMapper;

    @Resource
    private FispStaff2RoleMapper fispStaff2RoleMapper;

    @Resource
    private FispMenu2RoleMapper fispMenu2RoleMapper;

    @Override
    public TreeSet<MenuInfoResp> queryUserMenuList(StaffInfo staffInfo) {
        Map<String, TreeSet<MenuInfoResp>> userMenuMap = new HashMap<>();
        Map<String, TreeSet<MenuInfoResp>> tempMenuMap = new HashMap<>();
        //人员权限
        List<Staff2RoleModel> roleModelList = fispStaff2RoleMapper.selectByStaffId(staffInfo.getMainUserId());
        List<String> roleIsList = roleModelList.stream().map(Staff2RoleModel::getRoleId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(roleIsList)) {
            roleIsList = new ArrayList<>();
        }

        roleIsList.add("PTYG");

        //权限下的菜单
        List<String> userMenus = fispMenu2RoleMapper.selectByRoleIds(roleIsList);
        if (CollectionUtils.isEmpty(userMenus)) {
            return new TreeSet();
        }

        //直接尼玛一把查出所有的菜单
        List<FsipMenuParamEntity> allMenuList = fsipMenuMapper.selectAllMenus();
        //转换为map
        Map<String, FsipMenuParamEntity> allMenusMap = allMenuList.stream().collect(Collectors.toMap(FsipMenuParamEntity::getMenuId, a -> a));

        for (String menuId : userMenus) {
            FsipMenuParamEntity menuParam = allMenusMap.get(menuId);
            if (menuParam == null) {
                continue;
            }
            String parentMenuId = menuParam.getParentMenuId();
            TreeSet<MenuInfoResp> menuInfoResps = userMenuMap.get(parentMenuId);
            if (menuInfoResps == null && (menuInfoResps = tempMenuMap.get(parentMenuId)) == null) {
                menuInfoResps = new TreeSet<>();
                userMenuMap.put(parentMenuId, menuInfoResps);
                tempMenuMap.put(parentMenuId, menuInfoResps);
            }
            menuInfoResps.add(MenuInfoResp.builder().menuId(menuParam.getMenuId()).parentMenuId(menuParam.getParentMenuId())
                    .label(menuParam.getMenuName()).path(menuParam.getMenuPath()).sort(menuParam.getSort()).build());
        }

        while (true) {
            List<String> collect = userMenuMap.keySet().stream().filter(val -> !val.equals("-1")).collect(Collectors.toList());
            if (collect.isEmpty()) {
                break;
            }
            for (String s : collect) {
                TreeSet<MenuInfoResp> remove = userMenuMap.remove(s);
                FsipMenuParamEntity menuParam = allMenusMap.get(s);

                if (menuParam == null) {
                    continue;
                }

                String parentMenuId = menuParam.getParentMenuId();
                TreeSet<MenuInfoResp> menuInfoResps = userMenuMap.get(parentMenuId);
                if (menuInfoResps == null && (menuInfoResps = tempMenuMap.get(parentMenuId)) == null) {
                    menuInfoResps = new TreeSet<>();
                    userMenuMap.put(parentMenuId, menuInfoResps);
                    tempMenuMap.put(parentMenuId, menuInfoResps);
                }

                menuInfoResps.add(MenuInfoResp.builder().menuId(menuParam.getMenuId()).parentMenuId(menuParam.getParentMenuId())
                        .label(menuParam.getMenuName()).path(menuParam.getMenuPath()).sort(menuParam.getSort()).children(remove).build());
            }
        }

        return userMenuMap.get("-1");
    }
}
