package com.asiainfo.fsip.controller;

import com.asiainfo.fsip.model.FileModel;
import com.asiainfo.fsip.service.OssService;
import com.asiainfo.fsip.utils.FileUtils;
import com.asiainfo.mcp.tmc.common.base.annotation.RspResult;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;
import com.asiainfo.mcp.tmc.common.entity.base.BaseRsp;
import com.asiainfo.mcp.tmc.common.exception.BusinessException;
import com.asiainfo.mcp.tmc.common.util.MD5;
import com.asiainfo.mcp.tmc.common.util.RspHelp;
import com.asiainfo.mcp.tmc.sso.util.StaffInfoUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;

@RestController
@RequestMapping("file")
@Slf4j
@Api("文件上传下载接口")
public class FileController {

    @Resource
    private OssService ossService;

    /**
     * 文件上传
     */
    @ApiOperation("上传文件")
    @PostMapping("/uploadFile")
    @RspResult
    public FileModel uploadFile(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BusinessException("9999", "文件为空！");
        }



        StaffInfo staffInfo = StaffInfoUtil.getStaff();

        InputStream inputStream = file.getInputStream();
        String name = file.getOriginalFilename();
        log.info("begin to upload file {}", name);
        if (name != null) {
            int lastIndex = name.lastIndexOf(".");
            if (lastIndex > -1) {
                String fileName = name.substring(0, lastIndex);
                StringBuilder sb = new StringBuilder(fileName).append("-").append(staffInfo.getMainUserId())
                        .append("-").append(System.currentTimeMillis()).append(name.substring(lastIndex));
                name = sb.toString();
            }
        }
//        FileModel fileModel = ossService.uploadFile(name, inputStream);
//        fileModel.setOriginalFilename(file.getOriginalFilename());
        return null;
    }

    /**
     * 文件上传
     */
    @ApiOperation("通过md5上传文件")
    @PostMapping("/uploadMd5File")
    @RspResult
    public FileModel uploadMd5File(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BusinessException("9999", "文件为空！");
        }
        InputStream inputStream = file.getInputStream();
        String name = file.getOriginalFilename();
        String md5 = MD5.md5(file.getBytes());
        if (name != null) {
            int lastIndex = name.lastIndexOf(".");
            if (lastIndex > -1) {
                StringBuilder sb = new StringBuilder(md5).append(name.substring(lastIndex));
                md5 = sb.toString();
            }
        }
        FileModel fileModel = ossService.uploadFile(md5, inputStream);
        fileModel.setOriginalFilename(name);
        return fileModel;
    }

    /**
     * 删除文件
     */
    @ApiOperation("删除文件")
    @GetMapping("/deleteFile")
    @RspResult
    public BaseRsp<Void> deleteFile(@RequestParam("fileName") String fileName) {
        try {
            ossService.deleteFile(fileName);
            return RspHelp.success(null);
        } catch (Exception e) {
            log.info("Could not execute delete file, fileName =" + fileName, e);
            return RspHelp.fail(RspHelp.ERROR_CODE, e.getMessage());
        }
    }

    @ApiOperation("下载文件")
    @RequestMapping (path = "/downloadFile",method ={RequestMethod.POST,RequestMethod.GET} )
    @RspResult
    public void downloadFile(HttpServletResponse response,
                                 @RequestParam("fileName") String fileName) throws Exception {
        InputStream stream = ossService.downloadFile(fileName);
        response.setContentType("application/x-msdownload;charset=UTF-8");
        String encode = URLEncoder.encode(fileName, "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + encode);
        ServletOutputStream out = response.getOutputStream();
        FileUtils.writeFile(out, stream);
    }

    @ApiOperation("通过base64方式下载文件")
    @GetMapping("/downloadBase64")
    public String downloadBase64(@RequestParam String fileName) {
        if (StringUtils.isBlank(fileName)) {
            log.error("请求参数不能为空");
            return null;
        }

        try{
            FileModel fileModel = ossService.downloadBase64File(fileName);
            return fileModel == null ? null :fileModel.getContent();
        }catch (Exception e){
            log.error("Could not download base64", e);
        }
        return null;
    }

    @GetMapping("/downTemplate")
    public void downloadTemplate(HttpServletRequest request, HttpServletResponse response) {
        try {
            String fileName = request.getParameter("fileName");
            String filePath = "templates/" + fileName;
            InputStream in = this.getClass().getClassLoader().getResourceAsStream(filePath);
            response.setContentType("application/x-msdownload;charset=UTF-8");
            String name = FileUtils.buildFileName(request, fileName);
            response.setHeader("Content-Disposition", "attachment;filename=" + name);
            ServletOutputStream out = response.getOutputStream();
            FileUtils.writeFile(out, in);
        } catch (Exception e) {
            log.info("Could not download excel", e);
        }
    }

    /**
     * 文件路径重新生成
     */
    @RequestMapping("/rebuildFileUrl")
    @RspResult
    public FileModel rebuildFileUrl(@RequestParam String fileName) throws Exception{
        try {
            if (StringUtils.isBlank(fileName)) {
                throw new BusinessException("9999", "请求参数不能为空！");
            }

            InputStream is = ossService.downloadFile(fileName);
            String md5Name = FileUtils.buildMd5Name(is, fileName);
            return ossService.uploadFile(md5Name, is);
        } catch (Exception e) {
            log.info("Could not execute rebuildFileUrl, filePath =" + fileName, e);
            throw e;
        }
    }

}
