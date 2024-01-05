package com.zcunsoft.clklog.api.entity.clickhouse;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "visituri_detail_exit_bydate")
@Data
public class VisituriExitDetailbydate extends BaseDetailbydate {

   

    @Column
    String uri;
    
    @Column
    Integer exitCount;
    
    @Column 
    String title;
    
    @Column 
    String uriPath;

    @Column
    String host;
}
