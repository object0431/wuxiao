package com.asiainfo.mcp.tmc.controller;

import com.asiainfo.mcp.tmc.common.entity.TitleInfo;
import com.asiainfo.mcp.tmc.service.TitleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/inner")
@Slf4j
public class InnerController {

    @Resource
    private TitleService titleService;

    /**
     * 生成标题
     */
    @PostMapping("/getTitle")
    public String getTitle(@RequestParam String type, @RequestBody TitleInfo titleInfo) {
        try {
            String title = titleService.generateTitle(type, titleInfo.getStaffInfo(), titleInfo.getKeys());
            log.info("title = " + title);
            return title;
        } catch (Exception e) {
            log.error("Could not execute getTitle, type =" + type, e);
            throw e;
        }
    }
}
