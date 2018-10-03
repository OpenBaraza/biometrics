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

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONObject;

public class FingerPrint {
	Logger log = Logger.getLogger(FingerPrint.class.getName());
	
	Device dev = null;

	public FingerPrint(Device dev) {
		this.dev = dev;
	}
	
	public JSONObject scan(String deviceId) {
		String finger1Details = dev.scan(deviceId);

System.out.println(finger1Details);
		JSONObject jFingerScan = new JSONObject(finger1Details);
		if(finger1Details.contains("Scan quality is low.")) {
			System.out.println("Scan quality is low.");
		} else if(finger1Details.contains("Device is not connected.")) {
			System.out.println("Device is not connected.");
		} else if(finger1Details.contains("Device not found.")) {
			System.out.println("Device not found.");
		} else if(finger1Details.contains("Device Timed Out")) {
			System.out.println("Device Timed Out");
		}
		
		return jFingerScan;
	}
	
	public void verify(String deviceId) {
		JSONObject jFingerScan = scan(deviceId);
System.out.println("---------------");
		
		JSONObject jVerify = new JSONObject();
		jVerify.put("security_level","DEFAULT");
		jVerify.put("template0", jFingerScan.getString("template0"));
		jVerify.put("template1", jFingerScan.getString("template1"));
		String vResults = dev.verifyScan(deviceId, jVerify);
	}
}

