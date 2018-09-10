package com.dewcis.biometrics;

import java.util.logging.Logger;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.HashMap;
import java.util.Map;

public class Configs {
	Logger log = Logger.getLogger(Configs.class.getName());
	
	Map<String, String> cfgs;

	public Configs(Connection db) {
		cfgs = new HashMap<>();
		try {
			String mySql = "SELECT config_name, config_value FROM sys_configs WHERE config_type_id = 1";
			Statement st = db.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = st.executeQuery(mySql);
			while(rs.next()) cfgs.put(rs.getString("config_name"), rs.getString("config_value"));
			rs.close();
			st.close();
		} catch (SQLException ex) {
			log.severe("Database connection SQL Error : " + ex);
		}
	}

	public Map<String, String> getConfigs() {
		return cfgs;
	}
	
	public String getConfig(String configName) {
		return cfgs.get(configName);
	}
}
