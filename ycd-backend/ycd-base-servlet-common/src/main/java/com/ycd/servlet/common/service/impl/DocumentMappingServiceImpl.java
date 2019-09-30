package com.ycd.servlet.common.service.impl;



import com.ycd.common.entity.DocumentMapping;
import com.ycd.common.util.SimpleUtil;
import com.ycd.servlet.common.service.interfaces.DocumentMappingService;

import java.util.Arrays;
import java.util.List;

public class DocumentMappingServiceImpl extends AbstractServiceWithCreateEntity<DocumentMapping> implements DocumentMappingService {
    @Override
    public void bindDocumentBusiness(String documentId, String businessId) {
        if (SimpleUtil.isEmpty(documentId) || SimpleUtil.isEmpty(businessId)) {
            return;
        }
        DocumentMapping documentMapping = new DocumentMapping();
        documentMapping.setBusinessId(businessId);
        documentMapping.setDocumentId(documentId);
        DocumentMapping dataBase = mapper.selectOne(documentMapping);
        if (SimpleUtil.isNotEmpty(dataBase)) {
            return;
        }
        mapper.insertSelective(documentMapping);
    }

    @Override
    public void bindDocumentBusinesses(String documentId, String businessIds) {
        if (SimpleUtil.isEmpty(documentId) || SimpleUtil.isEmpty(businessIds)) {
            return;
        }
        bindDocumentBusinesses(documentId, Arrays.asList(businessIds.split(",")));
    }

    @Override
    public void bindDocumentBusinesses(String documentId, List<String> businessIds) {
        if (SimpleUtil.isEmpty(documentId) || SimpleUtil.isEmpty(businessIds)) {
            return;
        }
        businessIds.forEach(businessId -> {
            bindDocumentBusiness(documentId, businessId);
        });
    }

    @Override
    public void bindBusinessDocuments(String businessId, String documentIds) {
        if (SimpleUtil.isEmpty(businessId) || SimpleUtil.isEmpty(documentIds)) {
            return;
        }
        bindBusinessDocuments(businessId, Arrays.asList(documentIds.split(",")));
    }

    @Override
    public void bindBusinessDocuments(String businessId, List<String> documentIds) {
        if (SimpleUtil.isEmpty(businessId) || SimpleUtil.isEmpty(documentIds)) {
            return;
        }
        documentIds.forEach(documentId -> {
            bindDocumentBusiness(documentId, businessId);
        });
    }

    @Override
    public void unbindByDocumentId(String documentId) {
        if (SimpleUtil.isEmpty(documentId)) {
            return;
        }
        DocumentMapping documentMapping = new DocumentMapping();
        documentMapping.setDocumentId(documentId);
        mapper.delete(documentMapping);
    }

    @Override
    public void unbindByBusinessId(String businessId) {
        if (SimpleUtil.isEmpty(businessId)) {
            return;
        }
        DocumentMapping documentMapping = new DocumentMapping();
        documentMapping.setBusinessId(businessId);
        mapper.delete(documentMapping);
    }

    @Override
    public void unbindByDocumentBusiness(String documentId, String businessId) {
        if (SimpleUtil.isEmpty(documentId) || SimpleUtil.isEmpty(businessId)) {
            return;
        }
        DocumentMapping param = new DocumentMapping();
        param.setBusinessId(businessId);
        param.setDocumentId(documentId);
        DocumentMapping dataBase = mapper.selectOne(param);
        if (SimpleUtil.isEmpty(dataBase)) {
            return;
        }
        mapper.delete(param);
    }

    @Override
    public void unbindByDocumentBusinesses(String documentId, String businessIds) {
        if (SimpleUtil.isEmpty(documentId) || SimpleUtil.isEmpty(businessIds)) {
            return;
        }
        unbindByDocumentBusinesses(documentId, Arrays.asList(businessIds.split(",")));
    }

    @Override
    public void unbindByDocumentBusinesses(String documentId, List<String> businessIds) {
        if (SimpleUtil.isEmpty(documentId) || SimpleUtil.isEmpty(businessIds)) {
            return;
        }
        businessIds.forEach(businessId -> {
            unbindByDocumentBusiness(documentId, businessId);
        });
    }

    @Override
    public void unbindByBusinessDocuments(String businessId, String documentIds) {
        if (SimpleUtil.isEmpty(businessId) || SimpleUtil.isEmpty(documentIds)) {
            return;
        }
        unbindByBusinessDocuments(businessId, Arrays.asList(documentIds.split(",")));
    }

    @Override
    public void unbindByBusinessDocuments(String businessId, List<String> documentIds) {
        if (SimpleUtil.isEmpty(businessId) || SimpleUtil.isEmpty(documentIds)) {
            return;
        }
        documentIds.forEach(documentId -> {
            unbindByDocumentBusiness(documentId, businessId);
        });
    }
}
