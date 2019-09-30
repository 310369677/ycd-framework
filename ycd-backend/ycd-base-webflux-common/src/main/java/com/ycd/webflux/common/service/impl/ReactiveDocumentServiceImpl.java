package com.ycd.webflux.common.service.impl;



import com.ycd.common.config.CommonConfig;
import com.ycd.common.constant.BooleanEnum;
import com.ycd.common.dto.FileInfo;
import com.ycd.common.entity.Document;
import com.ycd.common.repo.mybatis.DocumentMapper;
import com.ycd.common.util.DateUtil;
import com.ycd.common.util.IOUtil;
import com.ycd.common.util.SimpleUtil;
import com.ycd.webflux.common.reactivetransaction.WebFluxTransactional;
import com.ycd.webflux.common.service.interfaces.ReactiveDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.*;
import java.util.UUID;


public class ReactiveDocumentServiceImpl extends LongPriAbstractReactiveService<Document> implements ReactiveDocumentService {


    @Autowired
    private CommonConfig commonConfig;

    @Autowired
    private DocumentMapper mapper;


    @Override
    @WebFluxTransactional
    public Flux<String> saveFilePart(Flux<FilePart> filePartFlux, String type) {
        File parent = new File(commonConfig.getFileStorePath());
        if (!parent.exists()) {
            if (!parent.mkdirs()) {
                return Flux.empty();
            }
        }
        return filePartFlux.flatMap(filePart -> {
            String parentPath = parent.getPath();
            Document document = new Document();
            document.setName(filePart.filename());
            return filePart.content().reduce((t1, t2) -> {
                t1.write(t2);
                return t1;
            }).flatMap(t -> {
                document.setSize((long) t.readableByteCount());
                //根据日期生成新的文件夹
                String now = DateUtil.getNowStr("yyyy-MM-dd");
                //生成日期文件夹
                File dir = new File(SimpleUtil.pathJoin(parentPath, now));
                if (!dir.exists()) {
                    SimpleUtil.trueAndThrows(!dir.mkdirs(), "创建日期文件失败");
                }
                //生成新的名字
                String newName = UUID.randomUUID().toString().replace("-", "") + "_" + document.getName();
                //得到后缀
                String fileSuffix = fileSuffix(document.getName());
                document.setFileSuffix(fileSuffix);
                if (!SimpleUtil.isEmpty(type)) {
                    document.setType(type);
                }
                String storePath = SimpleUtil.pathJoin(parentPath, now, newName);
                document.setPath(storePath);
                File file = new File(storePath);
                writeBytes2File(file, t.asInputStream());
                document.setStatus(BooleanEnum.NO.code());
                //保存文件到数据库
                return save(document).map(id -> id + "");
            });
        });
    }

    @Override
    public Mono<FileInfo> findFile(String documentId) {
        return Mono.fromSupplier(() -> {
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
        });
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

    private void writeBytes2File(File file, InputStream in) {
        try {
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    return;
                }
            }
            FileOutputStream outputStream = new FileOutputStream(file, false);
            IOUtil.writeInputStream2OutputStream(in, outputStream, true);
        } catch (IOException e) {
            throw SimpleUtil.newBusinessException("文件写入磁盘异常");
        }

    }
}
