package com.zcunsoft.clklog.api.entity.clickhouse;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import java.sql.Timestamp;

@Entity(name = "visitor_base_bydate")
@Data
public class VisitorBasebydate {

	@Id
    @Column
    Timestamp statDate;

    @Id
    @Column
    String lib;

    @Id
    @Column
    String projectName;

    @Column
    String isFirstDay;
    
    @Column
    String country;

    @Column
    String province;

    @Column
    String distinctId;

    @Column
    Integer newCount;
    
    @Column
    Integer activeCount;
    
    @Column
    Integer revisitCount;
    
    @Column
    Integer silentCount;
    
    @Column
    Integer churnCount;
    
    @Column
    Integer continuousActiveCount;
}
