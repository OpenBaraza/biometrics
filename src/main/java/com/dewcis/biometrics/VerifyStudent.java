package com.dewcis.biometrics;

import java.util.logging.Logger;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.HashMap;
import java.util.Map;

public class VerifyStudent {
	Logger log = Logger.getLogger(VerifyStudent.class.getName());
	
	public Map<String, String> getStudent(Connection db, String userId) {
		Map<String, String> student = new HashMap<String, String>();
		try {
			String mySql = "SELECT s.studentid, s.studentname, s.telno, s.email, e.entity_id "
			+ "FROM studentdegreeview s INNER JOIN entitys e ON s.studentid = e.user_name "
			+ "WHERE e.entity_id = " + userId;
			Statement st = db.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = st.executeQuery(mySql);
			if(rs.next()) {
				student.put("studentid", rs.getString("studentid"));
				student.put("studentname", rs.getString("studentname"));
				student.put("telno", rs.getString("telno"));
				student.put("email", rs.getString("email"));
			}
			rs.close();
			st.close();
		} catch (SQLException ex) {
			log.severe("Database connection SQL Error : " + ex);
		}
		return student;
	}
}
