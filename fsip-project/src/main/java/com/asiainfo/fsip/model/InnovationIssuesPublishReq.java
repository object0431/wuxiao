package com.asiainfo.fsip.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InnovationIssuesPublishReq {
    private String title;
    private String content;
    private String canJoin;
    private Integer partnerNum;
    private Scope scope;
    private List<Attr> attrList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Scope{
        private String type;
        private List<Value> values;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Value{
        private String code;
        private String name;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Attr{
        private String attrType;
        private String attrCode;
        private String attrValue;
    }
}
