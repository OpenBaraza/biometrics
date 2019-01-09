/**
 * @author      Dennis W. Gichangi <dennis.gichangi@dewcis.com>
 * @version     2018.0329
 * @since       1.6
 * website		www.dewcis.com
 */
package com.dewcis.biometrics;

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

public class StudentList {
	Logger log = Logger.getLogger(StudentList.class.getName());

	Connection db;
	String mySql;
	Map<String, String> fields;
	public StudentList(Connection db, String mySql, Map<String, String> fields) {
		this.db = db;
		this.mySql = mySql;
		this.fields = fields;
	}

	public Vector<Vector<String>> getStudents(String stdSql) {
		Vector<Vector<String>> students = new Vector<Vector<String>>();
		try {
			Statement st = db.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = st.executeQuery(stdSql);
			while(rs.next()) {
				Vector<String> row = new Vector<String>();
				for(String field : fields.keySet())
					row.add(rs.getString(field));
				students.add(row);
			}
		} catch (SQLException ex) {
			log.severe("Database connection SQL Error : " + ex);
		}
		return students;
	}
	
	public Vector<Vector<String>> getUnRegistred(String filter) {
		String stdSql = mySql + filter + " AND (s.has_biometrics = false) ORDER BY s.studentid LIMIT 200";
		return getStudents(stdSql);
	}
	
	public Vector<Vector<String>> getRegistred(String filter) {
		String stdSql = mySql + filter + " AND (s.has_biometrics = true) AND (s.biometrics_active = true) ORDER BY s.studentid LIMIT 200";
		return getStudents(stdSql);
	}
	
	public Vector<Vector<String>> getInActive(String filter) {
		String stdSql = mySql + filter + " AND (s.has_biometrics = true) AND (s.biometrics_active = false) ORDER BY s.studentid LIMIT 200";
		return getStudents(stdSql);
	}
	
}

