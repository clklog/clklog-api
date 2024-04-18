package com.zcunsoft.clklog.api.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.util.StringUtils;
import org.yaml.snakeyaml.Yaml;

import com.zcunsoft.clklog.api.models.ClientProfile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientProfileFile extends File {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String CLIENT_ID_KEY = "client-id";

	public ClientProfileFile(String pathname) {
		super(pathname);
	}

	private ClientProfile clientProfile;

	public void init() {
		if (this.exists()) {
			Yaml yaml = new Yaml();
			try {
//				FileReader reader = new FileReader(this);
				Map<String, Object> obj = yaml.load(new FileReader(this));
				if (obj.containsKey(CLIENT_ID_KEY) && StringUtils.hasLength(obj.get(CLIENT_ID_KEY).toString())) {
					clientProfile = new ClientProfile();
					clientProfile.setClientId(obj.get(CLIENT_ID_KEY).toString());
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				log.error("failed to load profiles.", e);
			} finally {
			}
		}
		if (clientProfile == null) {
			log.debug("initing profiles...");
			try {
				Files.createDirectories(Paths.get(this.getParent()));
				clientProfile = new ClientProfile();
				clientProfile.setClientId(UUID.randomUUID().toString());
				Map<String, Object> data = new LinkedHashMap<String, Object>();
				data.put(CLIENT_ID_KEY, clientProfile.getClientId());
				Yaml yaml = new Yaml();
				FileWriter writer;
				writer = new FileWriter(this);
				yaml.dump(data, writer);
			} catch (IOException e) {
				e.printStackTrace();
				log.error("failed to init profiles.", e);
			}
		}
	}

	public ClientProfile getClientProfile() {
		return clientProfile;
	}

}
