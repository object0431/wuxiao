package com.asiainfo.fsip.mapper.fsip;

import com.asiainfo.fsip.entity.FsipMenuParamEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FsipMenuMapper extends BaseMapper<FsipMenuParamEntity> {

    List<FsipMenuParamEntity> selectAllMenus();

}
