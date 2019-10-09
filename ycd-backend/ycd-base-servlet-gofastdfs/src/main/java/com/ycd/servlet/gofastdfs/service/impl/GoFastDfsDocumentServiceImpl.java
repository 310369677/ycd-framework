package com.ycd.servlet.gofastdfs.service.impl;

import com.ycd.common.util.SimpleUtil;
import com.ycd.common.util.SyncTaskExecutor;
import com.ycd.servlet.gofastdfs.config.GoFastDfsConfigProperties;
import com.ycd.servlet.gofastdfs.service.impl.interfaces.GoFastDfsDocumentService;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class GoFastDfsDocumentServiceImpl implements GoFastDfsDocumentService {

    private static final Logger log = LoggerFactory.getLogger(GoFastDfsDocumentServiceImpl.class);

    @Autowired
    GoFastDfsConfigProperties goFastDfsConfigProperties;

    @Override
    public List<String> saveFiles(List<MultipartFile> list, String type) {
        if (SimpleUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        SyncTaskExecutor taskExecutor = new SyncTaskExecutor();
        for (MultipartFile file : list) {
            taskExecutor.submit(UUID.randomUUID().toString(), () -> {
                //上传文件
                String result = uploadFile2FastDfs(file);
                return result;
            });
        }
        Map<String, Object> result = taskExecutor.startAll();
        return result.values().stream().map(Object::toString).collect(Collectors.toList());
    }

    private String uploadFile2FastDfs(MultipartFile file) {
        String result = "";
        try {
            OkHttpClient httpClient = new OkHttpClient();
            MultipartBody multipartBody = new MultipartBody.Builder().
                    setType(MultipartBody.FORM)
                    .addFormDataPart("file", file.getOriginalFilename(),
                            RequestBody.create(MediaType.parse("multipart/form-data;charset=utf-8"),
                                    file.getBytes()))
                    .addFormDataPart("output", "json")
                    .build();

            Request request = new Request.Builder()
                    .url(goFastDfsConfigProperties.getUploadUrl())
                    .post(multipartBody)
                    .build();

            Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                ResponseBody body = response.body();
                if (body != null) {
                    result = body.string();
                    System.out.println(result);
                }
            }
        } catch (Exception e) {
            log.error("上传到远程服务器出错", e);
        }
        return result;
    }
}
