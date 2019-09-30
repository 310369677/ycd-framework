package com.ycd.servlet.common.service.interfaces;


import com.ycd.common.dto.FileInfo;
import com.ycd.common.entity.Document;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentService extends LongPriService<Document> {


    List<String> saveFiles(List<MultipartFile> list, String type);

    FileInfo findFile(String documentId);

    List<Document> findDocumentByBusinessId(String businessId, String type);


    /**
     * 当前用户的文件列表
     *
     * @return 文件列表
     */
    List<Document> currentUserFileList();
}
