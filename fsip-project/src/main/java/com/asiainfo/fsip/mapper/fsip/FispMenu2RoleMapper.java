package com.asiainfo.fsip.mapper.fsip;

import com.asiainfo.fsip.entity.FispMenu2RoleEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FispMenu2RoleMapper extends BaseMapper<FispMenu2RoleEntity> {

    List<String> selectByRoleIds(List<String> roleIds);
}
