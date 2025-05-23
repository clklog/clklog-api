package com.zcunsoft.clklog.api.cfg;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;

@ConfigurationProperties("clklog-api")
public class ClklogApiSetting {

    private String projectName;

    /**
     * 渠道对照表
     */
    private LinkedHashMap<String, String> libTypeMap;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }


    public LinkedHashMap<String, String> getLibTypeMap() {
        return libTypeMap;
    }

    public void setLibTypeMap(LinkedHashMap<String, String> libTypeMap) {
        this.libTypeMap = libTypeMap;
    }
}
