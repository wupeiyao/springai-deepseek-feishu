package com.xiaowu.feishu.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <a
 * href="https://open.feishu.cn/document/server-docs/authentication-management/access-token/app_access_token_internal">...</a>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FeishuAkResponse extends ResponseEntity {

    @JsonProperty("app_access_token")
    private String appAccessToken;

    private Integer expire;

    @JsonProperty("tenant_access_token")
    private String tenantAccessToken;


}
