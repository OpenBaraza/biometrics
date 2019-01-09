/**
 * @author      Dennis W. Gichangi <dennis.gichangi@dewcis.com>
 * @version     2018.0329
 * @since       1.6
 * website		www.dewcis.com
 */
package com.dewcis.biometrics;

import java.net.URISyntaxException;

import java.util.logging.Logger;

import java.util.Date;
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

public class Enrolment {
	Logger log = Logger.getLogger(Enrolment.class.getName());

	private List<String> enrolledActive;
	private List<String> enrolledInActive;
	private Map<String, Vector<String>> students;
	private Map<String, JSONArray> fingerPrints;
	
	FingerPrint fp;
	
	public Enrolment() {
		enrolledActive = new ArrayList<String>();
		enrolledInActive = new ArrayList<String>();
		students = new HashMap<String, Vector<String>>();
		fingerPrints = new HashMap<String, JSONArray>();
	}

	public void usersList(Device dev, boolean fpList) {
		enrolledActive.clear();
		enrolledInActive.clear();
		fingerPrints.clear();

		String results = dev.usersList();
		
		fp = new FingerPrint(dev);

//System.out.println("BASE 2020 : " + results);
		if(results != null) {
			JSONObject jResults = new JSONObject(results);
			JSONArray jresponse = (JSONArray) jResults.get("records");

			for(int i=0; i<jresponse.length(); i++) {
				String userId = jresponse.getJSONObject(i).getString("user_id");
				if (jresponse.getJSONObject(i).getString("status").equals("AC"))
					enrolledActive.add(userId);
				else if (jresponse.getJSONObject(i).getString("status").equals("IN"))
					enrolledInActive.add(userId);
				
				if(fpList) {
					JSONArray aFP = fp.getFingerPrint(userId);
					if(aFP.length() == 2) fingerPrints.put(userId, aFP);
				}
			}
		}
	}

	public void getStudents(Connection db, String mySql, Map<String, String> fields) {
		students.clear();
		try {
			Statement st = db.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = st.executeQuery(mySql);
			while(rs.next()) {
				Vector<String> row = new Vector<String>();
				for(String field : fields.keySet())
					row.add(rs.getString(field));
				students.put(rs.getString("entity_id"), row);
			}
		} catch (SQLException ex) {
			log.severe("Database SQL Error : " + ex);
		}
	}
	
	public void updateRegistred(Connection db) {
		try {
			for(String key : students.keySet()) {
				if(enrolledActive.contains(key)) {
					String updSql = "UPDATE students SET has_biometrics  = true, biometrics_active = true "
						+ "WHERE (studentid = '" + students.get(key).get(0) + "')";
					Statement stUP = db.createStatement();
					stUP.executeUpdate(updSql);
					stUP.close();
				}
			}
		} catch (SQLException ex) {
			log.severe("Database SQL Error : " + ex);
		}
	}
	
	public Vector<Vector<String>> getUnRegistred() {
		Map<String, Vector<String>> urs = new HashMap<String, Vector<String>>(students);
		urs.keySet().removeAll(enrolledActive);
		urs.keySet().removeAll(enrolledInActive);
		Vector<Vector<String>> ursv = new Vector<Vector<String>>(urs.values());
		return ursv;
	}
	
	public Vector<Vector<String>> getRegistred() {
		Vector<Vector<String>> rsv = new Vector<Vector<String>>();
		for(String key : enrolledActive) {
			Vector<String> rs = students.get(key);
			if(rs != null) rsv.add(rs);
		}
		return rsv;
	}
	
	public Vector<Vector<String>> getInActive() {
		Vector<Vector<String>> iasv = new Vector<Vector<String>>();
		for(String key : enrolledInActive) {
			Vector<String> ias = students.get(key);
			if(ias != null) iasv.add(ias);
		}
		return iasv;
	}
	
	public void verify(String FPTemplate) {
		for(String userId : fingerPrints.keySet()) {
			fp.verify("541612052", fingerPrints.get(userId).getString(0), FPTemplate);
			fp.verify("541612052", fingerPrints.get(userId).getString(1), FPTemplate);
		}
	}
}

