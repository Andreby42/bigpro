package com.thc.platform.modules.express.dto;

import lombok.Data;

@Data
public class OrgDaySearch {
    private String orgId;
    private String orgName;
    private Integer daySearchNum;
}
