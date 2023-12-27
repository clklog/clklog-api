package com.zcunsoft.clklog.api.entity.clickhouse;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "visituri_detail_downpv_bydate")
@Data
public class VisituriDetailDownpvbydate extends BaseDetailbydate {

   

    @Column
    String uri;
    
    @Column
    Integer downPvCount;
    
    @Column 
    String title;
    
    @Column 
    String uriPath;

    @Column
    String host;
}
