package com.zcunsoft.clklog.api.entity.clickhouse;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "visituri_detail_entry_bydate")
@Data
public class VisituriDetailEntrybydate extends BaseDetailbydate {

   

    @Column
    String uri;
    
    @Column
    Integer entryCount;
    
    @Column 
    String title;
    
    @Column 
    String uriPath;

    @Column
    String host;
}
