package com.ycd.common.repo.mybatis;


import com.ycd.common.entity.Document;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface DocumentMapper extends Mapper<Document> {


    List<Document> findDocumentByBusinessAndType(@Param("businessId") String businessId, @Param("type") String type);

    List<Document> findCurrentUserFileList(@Param("currentUserId") String currentUserId);
}