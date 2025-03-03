package com.asiainfo.fsip.mapper;

import com.asiainfo.fsip.mapper.fsip.FsipProjectAchievementItemMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
@Slf4j
public class FsipProjectAchievementItemMapperTest {

    @Resource
    private FsipProjectAchievementItemMapper fsipProjectAchievementItemMapper;

    @Test
    public void test(){
        fsipProjectAchievementItemMapper.selectByAchievementAndJudgeId("HN231226144629052861", "qingxiao", "1");
    }
}
