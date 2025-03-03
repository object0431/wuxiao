package com.asiainfo.fsip.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.*;
import com.asiainfo.fsip.config.AliyunConfig;
import com.asiainfo.fsip.model.FileModel;
import com.asiainfo.fsip.service.OssService;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class OssServiceImpl implements OssService {

    @Resource
    private AliyunConfig aliyunConfig;

    @Resource
    private OSS ossClient;

    @Override
    public void uploadFile(String objectName, String uploadFile) throws Exception {
        try {
            objectName = aliyunConfig.getDir().concat(objectName);
            UploadFileRequest uploadFileRequest = new UploadFileRequest(aliyunConfig.getBucketName(), objectName);
            uploadFileRequest.setUploadFile(uploadFile);
            uploadFileRequest.setTaskNum(5);
            uploadFileRequest.setPartSize(1024 * 1024 * 1);
            uploadFileRequest.setEnableCheckpoint(true);

            ossClient.uploadFile(uploadFileRequest);

            Date date = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 365);
            String url = ossClient.generatePresignedUrl(aliyunConfig.getBucketName(), objectName, date).toString();
            log.info("url = " + url);
        } catch (Throwable e) {
            log.error("Could not execute uploadFile", e);
            throw new Exception(e);
        }
    }

    @Override
    public FileModel uploadFile(String objectName, InputStream is) {
        try {
            long startMilis = System.currentTimeMillis();
            log.info("begin to upload file to oss, objectName = " + objectName);

            objectName = aliyunConfig.getDir().concat(objectName);

            ossClient.putObject(aliyunConfig.getBucketName(), objectName, is);
            Date date = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 365);
            String url = ossClient.generatePresignedUrl(aliyunConfig.getBucketName(), objectName, date).toString();

            ObjectMetadata metadata = ossClient.getObjectMetadata(aliyunConfig.getBucketName(), objectName);

            long costTime = System.currentTimeMillis() - startMilis;
            log.info("upload file spent time " + costTime);
            return FileModel.builder().fileType(metadata.getContentType()).fileName(objectName).url(url).build();
        } catch (Exception e) {
            log.error("Could not execute uploadFile", e);
            throw e;
        }
    }

    @Override
    public FileModel uploadFileByBase64(String objectName, String content) throws Exception {
        try {
            byte[] bytes = Base64.getDecoder().decode(content);

            return uploadFile(objectName, new ByteArrayInputStream(bytes));
        } catch (Exception e) {
            log.error("Could not execute uploadFileByBase64", e);
            throw new Exception(e);
        }
    }

    @Override
    public void deleteFile(String objectName) {
        ObjectListing objectListing;
        String nextMarker = null;
        String bucketName = aliyunConfig.getBucketName();
        objectName = aliyunConfig.getDir().concat(objectName);
        try {
            do {
                ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName)
                        .withPrefix(objectName).withMarker(nextMarker);

                objectListing = ossClient.listObjects(listObjectsRequest);
                if (objectListing.getObjectSummaries().size() > 0) {
                    List<String> keys = new ArrayList<>();
                    for (OSSObjectSummary s : objectListing.getObjectSummaries()) {
                        keys.add(s.getKey());
                    }
                    DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName).withKeys(keys).withEncodingType("url");
                    DeleteObjectsResult deleteObjectsResult = ossClient.deleteObjects(deleteObjectsRequest);
                    deleteObjectsResult.getDeletedObjects();
                }

                nextMarker = objectListing.getNextMarker();
            } while (objectListing.isTruncated());
        } catch (Exception e) {
            log.error("Could not delete oss file, objectName = " + objectName, e);
        }
    }

    @Async
    @Override
    public void batchDeleteFile(List<String> objectNameList) {
        if (CollectionUtils.isEmpty(objectNameList)) {
            return;
        }

        for (String objectName : objectNameList) {
            deleteFile(objectName);
        }
    }

    @Override
    public InputStream downloadFile(String fileName) {
        try {
            fileName = aliyunConfig.getDir().concat(fileName);
            OSSObject ossObject = ossClient.getObject(aliyunConfig.getBucketName(), fileName);
            return ossObject.getObjectContent();
        } catch (Exception e) {
            log.error("Could not download file = " + fileName + ", bucketName = " + aliyunConfig.getBucketName(), e);
            throw e;
        }
    }

    @Override
    public FileModel downloadBase64File(String fileName) throws Exception {
        InputStream is = null;
        try {
            fileName = aliyunConfig.getDir().concat(fileName);
            OSSObject ossObject = ossClient.getObject(aliyunConfig.getBucketName(), fileName);

            ObjectMetadata metadata = ossObject.getObjectMetadata();
            is = ossObject.getObjectContent();
            byte[] bytes = IOUtils.toByteArray(is);
            String content = Base64.getEncoder().encodeToString(bytes);

            return FileModel.builder().fileType(metadata.getContentType()).fileName(fileName).content(content).build();
        } catch (Exception e) {
            log.error("Could not execute downloadBase64File, fileName = " + fileName, e);
            throw e;
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    @PreDestroy
    private void destory() {
        ossClient.shutdown();
    }
}
