package com.zcunsoft.clklog.api.entity.clickhouse;


import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity(name = "user_visituri_bydate")
@Data
public class UserVisituribydate {

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
    String country;

    @Column
    String province;

    @Column
    String isFirstDay;
    
    @Column
    Timestamp updateTime;
	
    @Column
    private int pv1Uri;

    @Column
    private int pv2Uri;
    
    @Column
    private int pv3Uri;
    
    @Column
    private int pv4Uri;
    
    @Column
    private int pv5Uri;
    
    @Column
    private int pv6Uri;
    
    @Column
    private int pv7Uri;
    
    @Column
    private int pv8Uri;
    
    @Column
    private int pv9Uri;
    
    @Column
    private int pv10Uri;
    
    @Column
    private int pv11_15Uri;
    
    @Column
    private int pv16_20Uri;
    
    @Column
    private int pv21Uri;
    
    
}
