/**
 * @author      Dennis W. Gichangi <dennis.gichangi@dewcis.com>
 * @version     2018.0329
 * @since       1.6
 * website		www.dewcis.com
 */
package com.dewcis.biometrics;

import java.net.URISyntaxException;

import java.util.logging.Logger;

import java.util.Vector;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;

import java.text.SimpleDateFormat;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONObject;

public class FingerPrint {
	Logger log = Logger.getLogger(FingerPrint.class.getName());
	
	Device dev = null;
	logEvent devLogs = null;

	public FingerPrint(Device dev, logEvent devLogs) {
		this.dev = dev;
		this.devLogs = devLogs;
	}
	
	public JSONObject scan(String deviceId) {
		String finger1Details = dev.scan(deviceId);

		JSONObject jFingerScan = new JSONObject(finger1Details);

		for(String scanKeys : jFingerScan.keySet())
			System.out.println(scanKeys);
			
		if(jFingerScan.has("template0")) {
			System.out.println(jFingerScan.getString("template0"));
		}
		
		return jFingerScan;
	}
	
	public void verify(String deviceId) {
		JSONObject jFinger0 = scan(deviceId);
		JSONObject jFinger1 = scan(deviceId);
System.out.println("---------------");
	
		if(jFinger0.has("template0") && jFinger1.has("template0")) {
			JSONObject jVerify = new JSONObject();
			jVerify.put("security_level","DEFAULT");
			jVerify.put("template0", jFinger0.getString("template0"));
			jVerify.put("template1", jFinger1.getString("template0"));
			String vResults = dev.verifyScan(deviceId, jVerify);
System.out.println(vResults);

			getLogs(deviceId);
		}
	}
	
	public void getLogs(String deviceId) {
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Date now = new Date();
		Date before = new Date(now.getTime()  - 10000000);
		String startDate = sdfDate.format(before) + ".00Z";
		String endDate = sdfDate.format(now) + ".00Z";
		
		devLogs.getLogs(startDate, endDate);
		
		devLogs.getLogs(deviceId, startDate, endDate);
		
	}
}

