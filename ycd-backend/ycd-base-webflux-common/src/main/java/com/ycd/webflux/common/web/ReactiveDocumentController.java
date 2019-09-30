package com.ycd.webflux.common.web;


import com.ycd.common.Result;
import com.ycd.common.web.AbstractController;
import com.ycd.webflux.common.service.interfaces.ReactiveDocumentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ZeroCopyHttpOutputMessage;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;


@RequestMapping("document")
@Api(tags = "文件处理")
public class ReactiveDocumentController extends AbstractController {


    @Autowired
    protected ReactiveDocumentService reactiveDocumentService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiOperation("文件上传")
    @ResponseBody
    public Mono<Result<List<String>>> upload(@RequestPart("files") Flux<FilePart> flux, String type) {
        Flux<String> result = reactiveDocumentService.saveFilePart(flux, type);
        return result.reduceWith((Supplier<ArrayList<String>>) ArrayList::new, (list, e) -> {
            list.add(e);
            return list;
        }).defaultIfEmpty(new ArrayList<>()).map(Result::ok);
    }


    @GetMapping("/download/{documentId}")
    @ApiOperation("文件下载")
    @ApiImplicitParam(name = "documentId", value = "文件id", required = true, type = "path")
    @ResponseBody
    public Mono<Void> download(ServerHttpResponse response, @PathVariable("documentId") String documentId) {
        ZeroCopyHttpOutputMessage zeroCopyResponse = (ZeroCopyHttpOutputMessage) response;
        return reactiveDocumentService.findFile(documentId).flatMap(fileInfo -> {
            String name = fileInfo.getFileName();
            name = encodeUtf8(name);
            response.getHeaders().set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + name);
            response.getHeaders().set("Content-Length", "" + fileInfo.getSize());
            response.getHeaders().setContentType(MediaType.APPLICATION_OCTET_STREAM);
            DataBuffer dataBuffer = zeroCopyResponse.bufferFactory().wrap(fileInfo.getContent());
            return zeroCopyResponse.writeWith(Mono.just(dataBuffer));
        });
    }


    private String encodeUtf8(String param) {
        try {
            return URLEncoder.encode(param, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e);
        }
    }
}
