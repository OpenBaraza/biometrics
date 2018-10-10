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

		JSONObject jFingerScan = new JSONObject(finger1Details);

		for(String scanKeys : jFingerScan.keySet())
			System.out.println(scanKeys);
			
		if(jFingerScan.has("template0")) {
			System.out.println(jFingerScan.getString("template0"));
		}
		
		return jFingerScan;
	}
	
	public JSONArray getFingerPrint(String userId) {
		JSONArray aFP = new JSONArray();
		String sFP = dev.getFingerPrint(userId);
		if(sFP != null) {
			System.out.println("\n" + sFP);
			JSONObject jFP = new JSONObject(sFP);
			if(jFP.has("fingerprint_template_list")) {
				if(jFP.getJSONArray("fingerprint_template_list").length()==2) {
					aFP.put(jFP.getJSONArray("fingerprint_template_list").getJSONObject(0).getString("template0"));
					aFP.put(jFP.getJSONArray("fingerprint_template_list").getJSONObject(1).getString("template0"));
				}
			}
		}
		return aFP;
	}
	
	public void verify(String deviceId, String template0, String template1) {
		JSONObject jVerify = new JSONObject();
		jVerify.put("security_level", "DEFAULT");
		jVerify.put("template0", template0);
		jVerify.put("template1", template1);
		String vResults = dev.verifyScan(deviceId, jVerify);

System.out.println(vResults);
	}
}

