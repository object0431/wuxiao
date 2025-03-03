package com.asiainfo.fsip.mapper.fsip;

import com.asiainfo.fsip.entity.FsipStaticParamEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FsipStaticParamMapper extends BaseMapper<FsipStaticParamEntity> {

    List<FsipStaticParamEntity> selectByProp(@Param("req") FsipStaticParamEntity req);

    List<FsipStaticParamEntity> selectByExtCode(String attrType, String extCode);

    FsipStaticParamEntity selectMaxSortByAttrType(String attrType);

    int updateByAttrCode(@Param("req") FsipStaticParamEntity req);

    int deleteByAttrCode(@Param("attrType") String attrType, @Param("attrCodeList") List<String> attrCodeList);

}
