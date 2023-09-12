package com.zcunsoft.clklog.api.entity.clickhouse.accesslog;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "nginx_access")
@Data
public class Accesslogbydate  {

	@Id
    @Column
    Timestamp timeDatepart;
	
    @Column
    String host;
    
    @Column
    String uri;
	
	@Column
	BigDecimal pv;
	
	@Column
	BigDecimal slowPv;
	
	@Column
	BigDecimal maxVisitTime;
	
	@Column
	BigDecimal requestLength;
	
	@Column
	BigDecimal bodyBytesSent;
	
	@Column
	BigDecimal ipCount;
	
	@Column
	BigDecimal visitTime;
	
	@Column
	BigDecimal avgVisitTime;
	
}
