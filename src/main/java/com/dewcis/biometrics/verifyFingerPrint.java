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

public class verifyFingerPrint {
	Logger log = Logger.getLogger(verifyFingerPrint.class.getName());

	public String verifyFingerPrint(Device dev, String template0, String deviceID){
		String results = dev.usersList();
		String verificationResults = null;

		if(results != null) {
			JSONObject jResults = new JSONObject(results);
			JSONArray jresponse = jResults.getJSONArray("records");

			while (verificationResults!=null){
				for(int i=0; i<jresponse.length(); i++) {
					String user_id = jresponse.getJSONObject(i).getString("user_id");
					String userFingerDetails = dev.userFingerPrint(user_id);
					if (userFingerDetails==null) {
						System.out.println("No user Finger print");
					}else if (userFingerDetails!=null) {
						System.out.println("Avilabele user Finger print" + userFingerDetails);
						JSONObject jFingettemp = new JSONObject(userFingerDetails);
						String template1 = jFingettemp.getJSONArray("fingerprint_template_list").getJSONObject(0).getString("template0");
						JSONObject jVerify = new JSONObject();
						jVerify.put("security_level","DEFAULT");
						jVerify.put("template0",template0);
						jVerify.put("template1",template1);
						String vResults = dev.verifyScan(deviceID, jVerify);
						if ("Processed Successfully".equals(vResults)) {
							String userResults = dev.userDetails(user_id);
							VerifyDesk ver = new VerifyDesk(userResults, dev);
						}else if (!"Processed Successfully".equals(vResults)) {
							String template2 = jFingettemp.getJSONArray("fingerprint_template_list").getJSONObject(1).getString("template0");
							JSONObject jVerify2 = new JSONObject();
							jVerify2.put("security_level","DEFAULT");
							jVerify2.put("template0",template0);
							jVerify2.put("template1",template2);
							String vResults2 = dev.verifyScan(deviceID,jVerify2);
							if ("Processed Successfully".equals(vResults2)) {
								String userResults = dev.userDetails(user_id);
								VerifyDesk ver = new VerifyDesk(userResults, dev);
							}else if (!"Processed Successfully".equals(vResults2)) {
								verificationResults = "There user is not in the system";
								System.out.println(verificationResults);
							}
						}
					}

				}
			}
		}

		return verificationResults;
	}
	
}

