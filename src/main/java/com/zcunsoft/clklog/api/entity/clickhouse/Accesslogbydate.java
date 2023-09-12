package com.zcunsoft.clklog.api.entity.clickhouse;

import lombok.Data;

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
    Integer visitCount;
}
