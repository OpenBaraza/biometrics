package com.dewcis.biometrics;

import java.util.logging.Logger;

import java.time.LocalDate;
import java.time.Month;
import java.time.Clock;
import java.time.Duration;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

public class logEvent {
	Logger log = Logger.getLogger(logEvent.class.getName());

	private Vector<Vector<String>> rowData;
	private Vector<String> columnNames;

	Device dev = null;

	private JSONArray jEventcode;
	private Vector<String> eventCodeName, logListcode;

	public logEvent(Device dev) {
		this.dev = dev;
		
		columnNames = new Vector<String>();
		columnNames.add("Date Time"); columnNames.add("Device ID"); columnNames.add("Device Name");
		columnNames.add("Entity ID"); columnNames.add("User Name");columnNames.add("Event");

		//get codlist referance logs name and code.
		String referencResult = dev.eventsType();

		JSONObject jsonObject = new JSONObject(referencResult);
		JSONArray tsmresponse = (JSONArray) jsonObject.get("records");

		rowData = new Vector<Vector<String>>();
		eventCodeName = new Vector<String>();
		logListcode = new Vector<String>();

		jEventcode = new JSONArray();
		eventCodeName.add("None");
		logListcode.add("0000");
		for(int i=0; i<tsmresponse.length(); i++) {
			eventCodeName.add(tsmresponse.getJSONObject(i).getString("description"));
			logListcode.add(""+tsmresponse.getJSONObject(i).getInt("code"));
			jEventcode.put(tsmresponse.getJSONObject(i).getInt("code"));
		}
    }
    
    public void getMonthLogs() {
        //get available device ID list
		String avalableDevice = dev.deviceList();

		JSONObject jAvailable = new JSONObject(avalableDevice);
		JSONArray aAvailable = jAvailable.getJSONArray("records");

		JSONObject jDevicecomp = new JSONObject();
		JSONArray jDevicelist = new JSONArray();
	
		int j = 01;
		if (aAvailable.length() == 0) {
			System.out.println("No avilable deviceList");
			jDevicecomp.put("device_id", "0000");
			jDevicecomp.put("end_datetime", YearMonth.now()+"-"+Month.from(LocalDate.now()).length(true)+"T23:59:00.00Z");
			jDevicecomp.put("start_datetime", YearMonth.now()+"-0"+j+"T00:00:00.00Z");

			jDevicelist.put(jDevicecomp);
		} else {
			for(int i=0; i<aAvailable.length(); i++) {
				jDevicecomp.put("device_id", aAvailable.getJSONObject(i).getInt("id"));
				jDevicecomp.put("end_datetime", YearMonth.now()+"-"+Month.from(LocalDate.now()).length(true)+"T23:59:00.00Z");
				jDevicecomp.put("start_datetime", YearMonth.now()+"-0"+j+"T00:00:00.00Z");

				jDevicelist.put(jDevicecomp);
			}
		}

		JSONObject jEventlog = new JSONObject();
		jEventlog.put("device_query_list", jDevicelist);
		jEventlog.put("event_type_code_list", jEventcode);
		jEventlog.put("limit", 0);
		jEventlog.put("offset", 0);
		
		//adding the log results to the logTable in the logs panel
		String eventLogView = dev.mothlyLogEvent(jEventlog);

		JSONObject jLog = new JSONObject(eventLogView);
		JSONArray aLog = (JSONArray) jLog.get("records");

		rowData.clear();
		for(int i=0; i<aLog.length(); i++){
			Vector<String> row = new Vector<String>();  
			row.add(aLog.getJSONObject(i).getString("datetime"));
			row.add(aLog.getJSONObject(i).getJSONObject("device").getInt("id")+"");
			row.add(aLog.getJSONObject(i).getJSONObject("device").getString("name"));
			if(aLog.getJSONObject(i).has("user")) {
System.out.println(aLog.getJSONObject(i).getJSONObject("user").toString());
				row.add(aLog.getJSONObject(i).getJSONObject("user").getString("user_id"));
			} else {
				row.add(" ");
			} if(aLog.getJSONObject(i).has("user")&&aLog.getJSONObject(i).getJSONObject("user").has("name")) {
				row.add(aLog.getJSONObject(i).getJSONObject("user").getString("name"));
			} else {
				row.add(" ");
			}
			row.add(aLog.getJSONObject(i).getJSONObject("event_type").getString("description"));
			rowData.add(row);
       	}
	}
	
	public void getLogs(String startTime, String endTime) {
		JSONArray jLogDate = new JSONArray();
		jLogDate.put(startTime);
		jLogDate.put(endTime);
		JSONObject jLogReq = new JSONObject();
		jLogReq.put("datetime", jLogDate);
		jLogReq.put("limit", 0);
		jLogReq.put("offset", 0);
System.out.println("Date : " + jLogReq.toString());

		String vResults = dev.getEventLog(jLogReq);
System.out.println(vResults);
	}
	
	public void getLogs(String deviceId, String startTime, String endTime) {
		JSONArray jEvent = new JSONArray();
		JSONObject jEventDetails = new JSONObject();
		jEventDetails.put("device_id", deviceId);
		jEventDetails.put("end_datetime", endTime);
		jEventDetails.put("start_datetime", startTime);
		jEvent.put(jEventDetails);

		JSONObject jSearchEvent = new JSONObject();
		jSearchEvent.put("device_query_list", jEvent);
		jSearchEvent.put("event_type_code_list", jEventcode);
		jSearchEvent.put("limit", 0);
		jSearchEvent.put("offset", 0);

System.out.println(jSearchEvent.toString());
		String eventLogs = dev.searchLogEvent(jSearchEvent);
		
System.out.println("\n\n" + eventLogs);
	}
	
	public Vector<String> getColumnNames() {
		return columnNames;
	}

	public Vector<Vector<String>> getRowData() {
		return rowData;
	}

	public Vector<String> getEventCodeName() {
		return eventCodeName;
	}

	public Vector<String> getLogListcode() {
		return logListcode;
	}
    
}
