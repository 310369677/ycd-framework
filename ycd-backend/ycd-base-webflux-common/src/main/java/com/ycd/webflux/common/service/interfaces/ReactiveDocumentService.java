package com.ycd.webflux.common.service.interfaces;



import com.ycd.common.dto.FileInfo;
import com.ycd.common.entity.Document;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReactiveDocumentService extends LongPriReactiveService<Document> {


    Flux<String> saveFilePart(Flux<FilePart> flux, String type);

    Mono<FileInfo> findFile(String documentId);
}
