package com.asiainfo.fsip.service;

import com.asiainfo.fsip.model.FileModel;

import java.io.InputStream;
import java.util.List;

public interface OssService {

    void deleteFile(String objectName);

    void batchDeleteFile(List<String> objectNameList);

    void uploadFile(String objectName, String uploadFile) throws Exception;

    FileModel uploadFile(String objectName, InputStream is) throws Exception;

    FileModel uploadFileByBase64(String objectName, String content) throws Exception;

    InputStream downloadFile(String fileName) throws Exception;

    FileModel downloadBase64File(String fileName) throws Exception;

}
