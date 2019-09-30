package com.ycd.servlet.common.web;


import com.ycd.common.Result;
import com.ycd.common.anno.TimeCalculate;
import com.ycd.common.dto.FileInfo;
import com.ycd.common.dto.Page;
import com.ycd.common.entity.Document;
import com.ycd.common.util.PageUtil;
import com.ycd.common.util.SimpleUtil;
import com.ycd.common.web.AbstractController;
import com.ycd.servlet.common.service.interfaces.DocumentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;

@RequestMapping("document")
@Api(tags = "文件处理")
@TimeCalculate
public class DocumentController extends AbstractController {


    @Autowired
    DocumentService documentService;

    @PostMapping("upload")
    @ResponseBody
    @ApiOperation("文件上传")
    public Result<List<String>> upload(HttpServletRequest request) {
        if (!(request instanceof MultipartHttpServletRequest)) {
            logger.warn("不是一个MultiPart请求");
            return Result.ok(Collections.emptyList());
        }
        MultipartHttpServletRequest req = (MultipartHttpServletRequest) request;
        MultiValueMap<String, MultipartFile> map = req.getMultiFileMap();
        if (SimpleUtil.isEmpty(map)) {
            return Result.ok(Collections.emptyList());
        }
        List<MultipartFile> list = map.get("files");
        String type = req.getParameter("type");
        return Result.ok(documentService.saveFiles(list, type));
    }


    @GetMapping("/download/{documentId}")
    @ApiOperation("文件下载")
    @ApiImplicitParam(name = "documentId", value = "文件id", required = true, type = "path")
    public void download(HttpServletResponse response, @PathVariable("documentId") String documentId) throws IOException {
        FileInfo fileInfo = documentService.findFile(documentId);
        String name = fileInfo.getFileName();
        name = URLEncoder.encode(name, "UTF-8");
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + name);
        response.addHeader("Content-Length", "" + fileInfo.getSize());
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM.getType());
        response.getOutputStream().write(fileInfo.getContent());
    }


    @PostMapping("list")
    @ResponseBody
    public Result<Page<Document>> currentUserFileList() {
        List<Document> list = documentService.currentUserFileList();
        return Result.ok(PageUtil.getLocalPage());
    }

}
