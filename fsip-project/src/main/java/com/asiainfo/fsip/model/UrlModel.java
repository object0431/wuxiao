package com.asiainfo.fsip.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UrlModel {

    private String pcUrl;

    private String mobileUrl;

    private String title;
}
