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

import org.json.JSONArray;
import org.json.JSONObject;

public class VerifyFingerPrint {
	Logger log = Logger.getLogger(VerifyFingerPrint.class.getName());

	Device dev = null;
	Map<String, String> fingerPrints;
	Vector<String> duplicateFP;
	
	public VerifyFingerPrint(Device dev) {
		this.dev = dev;
		fingerPrints = new HashMap<String, String>();
		duplicateFP = new Vector<String>();

		String results = dev.usersList();
		if(results != null) {
			JSONObject jResults = new JSONObject(results);
			JSONArray jresponse = jResults.getJSONArray("records");
			for(int i=0; i<jresponse.length(); i++) {
				String userId = jresponse.getJSONObject(i).getString("user_id");
				String userFingerDetails = dev.getFingerPrint(userId);
				if(userFingerDetails != null) {
					JSONObject jFingetPrint = new JSONObject(userFingerDetails);
					if(jFingetPrint.has("fingerprint_template_list")) {
						JSONArray jFPa = jFingetPrint.getJSONArray("fingerprint_template_list");
						if(jFPa.length() > 0) {
							String template0 = jFPa.getJSONObject(0).getString("template0");
							fingerPrints.put(userId, template0);
						}
					}
				}
			}
		}
	}
	
	public Vector<String> verify(String deviceId) {
		duplicateFP.clear();
		for(String userId0 : fingerPrints.keySet()) {
			String template0 = fingerPrints.get(userId0);
			for(String userId1 : fingerPrints.keySet()) {
				if(!userId0.equals(userId1)) {
					String template1 = fingerPrints.get(userId1);
					if(verify(deviceId, template0, template1)) {
						duplicateFP.add(userId0);
						break;
					}
				}
			}
		}
		return duplicateFP;
	}
	
	public boolean verify(String deviceId, String template0, String template1) {
		JSONObject jVerify = new JSONObject();
		jVerify.put("security_level","DEFAULT");
		jVerify.put("template0", template0);
		jVerify.put("template1", template1);
		String vResults = dev.verifyScan(deviceId, jVerify);
		JSONObject jResults = new JSONObject(vResults);
		
		boolean duplicate = false;
		if ("Processed Successfully".equals(vResults)) duplicate = true;
		
		return duplicate;
	}
	
}

