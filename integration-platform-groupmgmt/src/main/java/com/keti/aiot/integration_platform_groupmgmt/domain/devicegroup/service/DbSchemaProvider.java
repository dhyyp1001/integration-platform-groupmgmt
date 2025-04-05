package com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.service;

import org.springframework.beans.factory.annotation.Value;

public class DbSchemaProvider {
    @Value("${db.schema}")
    private String schema;

/*    @Value("${db.table.device-name}")
    private String deviceName;

    @Value("${db.table.device-group-name}")
    private String deviceGroupName;

    @Value("${db.table.device-group-member-name}")
    private String deviceGroupMemberName;*/

    public String getSchema() {
        return schema.isBlank() ? null : schema;  // 스키마 없으면 null
    }
}
