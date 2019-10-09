package com.ycd.servlet.gofastdfs.service.impl.interfaces;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface GoFastDfsDocumentService {

    /**
     * 上传文件到gofastdfs里面
     *
     * @param list 文件列表
     * @param type 类型
     * @return 文件保存后的id
     */
    List<String> saveFiles(List<MultipartFile> list, String type);
}
