package com.asiainfo.fsip.service;

import cn.hutool.core.io.FileUtil;
import com.asiainfo.fsip.model.FileModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Base64;

@SpringBootTest
@Slf4j
public class OssServiceTest {

    @Resource
    private OssService ossService;
    @Test
    public void testUploadFile() throws Exception {
        ossService.uploadFile("基础电信企业助力乡村振兴的数字化服务体系建设", "E:/国家级成果.png");
    }

    @Test
    public void testDownloadBase64File() throws Exception {
        FileModel fileModel = ossService.downloadBase64File("基础电信企业助力乡村振兴的数字化服务体系建设");
        log.info(fileModel.getContent());

        Base64.Decoder decoder = Base64.getDecoder();
        // 去掉base64前缀 data:image/jpeg;base64,
        String base64 = fileModel.getContent();
        byte[] b = decoder.decode(base64);
        // 处理数据
        for (int i = 0; i < b.length; ++i) {
            if (b[i] < 0) {
                b[i] += 256;
            }
        }

        IOUtils.write(b, new FileOutputStream("E:/aa.jpg"));
    }

}
