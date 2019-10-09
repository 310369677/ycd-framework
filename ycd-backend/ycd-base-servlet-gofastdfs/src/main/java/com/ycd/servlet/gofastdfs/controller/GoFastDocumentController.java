package com.ycd.servlet.gofastdfs.controller;

import com.ycd.common.Result;
import com.ycd.common.anno.TimeCalculate;
import com.ycd.common.util.SimpleUtil;
import com.ycd.servlet.gofastdfs.service.impl.interfaces.GoFastDfsDocumentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

@RequestMapping("goFastDfsDocument")
@Api(tags = "fastDfs文件接口")
@TimeCalculate
public class GoFastDocumentController {


    private static final Logger log = LoggerFactory.getLogger(GoFastDocumentController.class);


    @Autowired
    GoFastDfsDocumentService dfsDocumentService;

    @PostMapping("upload")
    @ResponseBody
    @ApiOperation("文件上传")
    public Result<List<String>> upload(HttpServletRequest request) {
        if (!(request instanceof MultipartHttpServletRequest)) {
            log.warn("不是一个MultiPart请求");
            return Result.ok(Collections.emptyList());
        }
        MultipartHttpServletRequest req = (MultipartHttpServletRequest) request;
        MultiValueMap<String, MultipartFile> map = req.getMultiFileMap();
        if (SimpleUtil.isEmpty(map)) {
            return Result.ok(Collections.emptyList());
        }
        List<MultipartFile> list = map.get("files");
        String type = req.getParameter("type");
        return Result.ok(dfsDocumentService.saveFiles(list, type));
    }


}
