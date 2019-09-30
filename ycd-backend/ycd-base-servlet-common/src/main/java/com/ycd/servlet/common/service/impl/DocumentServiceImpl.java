package com.ycd.servlet.common.service.impl;


import com.ycd.common.config.CommonConfig;
import com.ycd.common.constant.BooleanEnum;
import com.ycd.common.context.UserContext;
import com.ycd.common.dto.FileInfo;
import com.ycd.common.entity.Document;
import com.ycd.common.repo.mybatis.DocumentMapper;
import com.ycd.common.util.DateUtil;
import com.ycd.common.util.IOUtil;
import com.ycd.common.util.PageUtil;
import com.ycd.common.util.SimpleUtil;
import com.ycd.servlet.common.service.interfaces.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


public class DocumentServiceImpl extends AbstractServiceWithCreateEntity<Document> implements DocumentService {

    @Autowired
    private CommonConfig commonConfig;

    @Autowired
    private DocumentMapper mapper;

    @Override
    public List<String> saveFiles(List<MultipartFile> list, String type) {
        if (SimpleUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        File parent = new File(commonConfig.getFileStorePath());
        if (!parent.exists()) {
            if (!parent.mkdirs()) {
                return Collections.emptyList();
            }
        }
        List<String> result = new ArrayList<>();
        for (MultipartFile part : list) {
            String parentPath = parent.getPath();
            Document document = new Document();
            document.setName(part.getOriginalFilename());
            try {
                document.setSize((long) part.getBytes().length);
                //根据日期生成新的文件夹
                String now = DateUtil.getNowStr("yyyy-MM-dd");
                //生成日期文件夹
                File dir = new File(SimpleUtil.pathJoin(parentPath, now));
                if (!dir.exists()) {
                    SimpleUtil.trueAndThrows(!dir.mkdirs(), "创建日期文件失败");
                }
                //得到后缀
                String fileSuffix = fileSuffix(document.getName());
                document.setFileSuffix(fileSuffix);
                if (!SimpleUtil.isEmpty(type)) {
                    document.setType(type);
                }
                //生成新的名字
                String newName = UUID.randomUUID().toString().replace("-", "") + "_" + document.getName();
                String storePath = SimpleUtil.pathJoin(parentPath, now, newName);
                document.setPath(storePath);
                File file = new File(storePath);
                part.transferTo(file);
                document.setStatus(BooleanEnum.NO.code());
                result.add(save(document) + "");
            } catch (IOException e) {
                LOGGER.error("读取文件异常", e);
                throw SimpleUtil.newBusinessException("读取文件异常");
            }
        }
        return result;
    }


    @Override
    public FileInfo findFile(String documentId) {
        SimpleUtil.assertNotEmpty(documentId, "documentId不能为空");
        Document document = mapper.selectByPrimaryKey(documentId);
        SimpleUtil.trueAndThrows(SimpleUtil.isEmpty(document), String.format("为查找到对应的id:%s的文档", documentId));
        FileInfo info = new FileInfo();
        info.setFileName(document.getName());
        info.setSize(document.getSize().intValue());
        String filePath = document.getPath();
        //读取文件
        FileInputStream in = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            in = new FileInputStream(new File(filePath));
            IOUtil.writeInputStream2OutputStream(in, out);
            info.setContent(out.toByteArray());
        } catch (Exception e) {
            LOGGER.error("读取文件失败", e);
            //设置一个空数组
            info.setContent(new byte[0]);
            info.setSize(0);
        } finally {
            IOUtil.close(out, in);
        }
        return info;
    }

    @Override
    public List<Document> findDocumentByBusinessId(String businessId, String type) {
        if (SimpleUtil.isEmpty(businessId) || SimpleUtil.isEmpty(type)) {
            return Collections.emptyList();
        }
        return mapper.findDocumentByBusinessAndType(businessId, type);
    }

    @Override
    public List<Document> currentUserFileList() {
        PageUtil.start();
        List<Document> documentList = mapper.findCurrentUserFileList(UserContext.getCurrentUser().getId() + "");
        PageUtil.end(documentList);
        return documentList;
    }

    private String fileSuffix(String name) {
        if (SimpleUtil.isEmpty(name)) {
            return null;
        }
        int index = name.lastIndexOf(".");
        if (index < 0) {
            return null;
        }
        return name.substring(index + 1);
    }


}
