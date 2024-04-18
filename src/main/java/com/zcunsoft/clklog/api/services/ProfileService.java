package com.zcunsoft.clklog.api.services;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.zcunsoft.clklog.api.utils.ClientProfileFile;

import lombok.extern.slf4j.Slf4j;

@Service
public class ProfileService {

	@Resource
	private ClientProfileFile clientProfileFile;

	public String loadKey() {
		return clientProfileFile.getClientProfile().getClientId();
	}

}
