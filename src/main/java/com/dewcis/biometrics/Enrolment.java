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

public class Enrolment {
	Logger log = Logger.getLogger(Enrolment.class.getName());

	private List<String> enrolledActive;
	private List<String> enrolledInActive;
	private Map<String, Vector<String>> students;
	
	public Enrolment() {
		enrolledActive = new ArrayList<String>();
		enrolledInActive = new ArrayList<String>();
		students = new HashMap<String, Vector<String>>();
	}

	public void usersList(Device dev){
		enrolledActive.clear();
		enrolledInActive.clear();

		String results = dev.usersList();
System.out.println("BASE 2010  : ");

		if(results != null) {
			JSONObject jResults = new JSONObject(results);
			JSONArray jresponse = (JSONArray) jResults.get("records");
			
System.out.println("BASE 2020  : " + jresponse.toString());

			for(int i=0; i<jresponse.length(); i++) {
				if (jresponse.getJSONObject(i).getString("status").equals("AC"))
					enrolledActive.add(jresponse.getJSONObject(i).getString("user_id"));
				else if (jresponse.getJSONObject(i).getString("status").equals("IN"))
					enrolledInActive.add(jresponse.getJSONObject(i).getString("user_id"));
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
			log.severe("Database connection SQL Error : " + ex);
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
}

