package com.ycd.servlet.common.service.interfaces;



import com.ycd.common.entity.DocumentMapping;

import java.util.List;

public interface DocumentMappingService extends LongPriService<DocumentMapping> {

    /**
     * 绑定单个文档和单个业务
     * @param documentId 文档id
     * @param businessId 业务id
     */
    void bindDocumentBusiness(String documentId, String businessId);

    /**
     * 绑定单个文档和多个业务id
     * @param documentId 单个文档id
     * @param businessIds 多个业务ids用逗号分隔
     */
    void bindDocumentBusinesses(String documentId, String businessIds);

    /**
     * 绑定单个文档和多个业务id
     * @param documentId 文档id
     * @param businessIds list类型的业务ids
     */
    void bindDocumentBusinesses(String documentId, List<String> businessIds);

    /**
     * 绑定单个业务id和多个文档
     * @param businessId 单个业务id
     * @param documentIds 多个文档ids,逗号分隔
     */
    void bindBusinessDocuments(String businessId, String documentIds);

    /**
     * 绑定单个业务id和多个文档
     * @param businessId 单个业务id
     * @param documentIds 多个文档ids,list类型
     */
    void bindBusinessDocuments(String businessId, List<String> documentIds);

    /**
     * 通过文档的id解绑关系
     * @param documentId 文档id
     */
    void unbindByDocumentId(String documentId);

    /**
     * 通过业务id解绑
     * @param businessId 业务id
     */
    void unbindByBusinessId(String businessId);

    /**
     * 解绑单个业务和文档
     * @param documentId 文档id
     * @param businessId 业务id
     */
    void unbindByDocumentBusiness(String documentId, String businessId);

    /**
     * 解绑单个文档和对应的多个业务
     * @param documentId 文档id
     * @param businessIds 多个业务id
     */
    void unbindByDocumentBusinesses(String documentId, String businessIds);

    /**
     * 解绑单个文档和对应的多个业务
     * @param documentId 文档id
     * @param businessIds 多个业务id,list类型
     */
    void unbindByDocumentBusinesses(String documentId, List<String> businessIds);

    /**
     * 解绑单个业务id和多个文档ids
     * @param businessId 单个业务id
     * @param documentIds 多个文档ids
     */
    void  unbindByBusinessDocuments(String businessId, String documentIds);

    /**
     * 解绑单个业务id和多个文档ids
     * @param businessId 单个业务id
     * @param documentIds 多个文档ids,list类型
     */
    void  unbindByBusinessDocuments(String businessId, List<String> documentIds);
}
