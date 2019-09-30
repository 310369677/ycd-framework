package com.ycd.common.entity;

import javax.persistence.Table;

@Table(name = "t_document_mapping")
public class DocumentMapping extends AbstractEntity {

    private String documentId;

    private String businessId;

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }
}
