package com.dewcis.biometrics;

import java.util.logging.Logger;

import java.util.TimeZone;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import java.time.LocalDate;
import java.time.Month;
import java.time.Clock;
import java.time.Duration;
import java.time.YearMonth;

import java.text.SimpleDateFormat;

import org.json.JSONArray;
import org.json.JSONObject;

public class EventLogs {
	Logger log = Logger.getLogger(EventLogs.class.getName());

	private Vector<Vector<String>> rowData;
	private Vector<String> columnNames;

	Device dev = null;

	private JSONArray jEventCode;
	private JSONArray deviceList;
	private Vector<String> eventCodeName;
	private Vector<Integer> logListcode;

	public EventLogs(Device dev) {
		this.dev = dev;
		
		columnNames = new Vector<String>();
		columnNames.add("Date Time"); columnNames.add("Device ID"); columnNames.add("Device Name");
		columnNames.add("Entity ID"); columnNames.add("User Name");columnNames.add("Event");
		
		//get available device ID list
		String avalableDevice = dev.getDeviceList();
		JSONObject jAvailable = new JSONObject(avalableDevice);
		deviceList = jAvailable.getJSONArray("records");
System.out.println(jAvailable.toString());

		//get codlist referance logs name and code.
		String referencResult = dev.eventsType();
		JSONObject jsonObject = new JSONObject(referencResult);
		JSONArray tsmresponse = (JSONArray) jsonObject.get("records");

		rowData = new Vector<Vector<String>>();
		eventCodeName = new Vector<String>();
		logListcode = new Vector<Integer>();

		jEventCode = new JSONArray();
		eventCodeName.add("None");
		logListcode.add(0);
		for(int i=0; i<tsmresponse.length(); i++) {
			eventCodeName.add(tsmresponse.getJSONObject(i).getString("description"));
			logListcode.add(tsmresponse.getJSONObject(i).getInt("code"));
			jEventCode.put(tsmresponse.getJSONObject(i).getInt("code"));
		}
    }
    
    public JSONArray getLogs(String startDate, String EndDate) {
		JSONArray rLog = new JSONArray();
		if (deviceList.length() > 0) {
			JSONObject jDevicecomp = new JSONObject();
			JSONArray jDevicelist = new JSONArray();

			for(int i=0; i<deviceList.length(); i++) {
				jDevicecomp.put("device_id", deviceList.getJSONObject(i).getInt("id"));
				jDevicecomp.put("end_datetime", EndDate + "T23:59:00.00Z");
				jDevicecomp.put("start_datetime", startDate + "T00:00:00.00Z");
				jDevicelist.put(jDevicecomp);
			}
		
			JSONObject jEventlog = new JSONObject();
			jEventlog.put("device_query_list", jDevicelist);
			jEventlog.put("event_type_code_list", jEventCode);
			jEventlog.put("limit", 0);
			jEventlog.put("offset", 0);
			
			//adding the log results to the logTable in the logs panel
			String eventLogView = dev.mothlyLogEvent(jEventlog);
			JSONObject jLog = new JSONObject(eventLogView);
			rLog = jLog.getJSONArray("records");
		}
		return rLog;
	}
	
	public JSONArray getLogs(JSONArray jECodes, String deviceId, String startTime, String endTime) {
		JSONArray jEvent = new JSONArray();
		JSONObject jEventDetails = new JSONObject();
		jEventDetails.put("device_id", deviceId);
		jEventDetails.put("end_datetime", endTime);
		jEventDetails.put("start_datetime", startTime);
		jEvent.put(jEventDetails);

		JSONObject jEventReq = new JSONObject();
		jEventReq.put("device_query_list", jEvent);
		jEventReq.put("event_type_code_list", jECodes);
		jEventReq.put("limit", 0);
		jEventReq.put("offset", 0);
System.out.println("\n" + jEventReq.toString());

		String vResults = dev.searchLogEvent(jEventReq);
		JSONObject jLog = new JSONObject(vResults);
		JSONArray rLog = jLog.getJSONArray("records");
		
System.out.println("\n" + vResults);
		return rLog;
	}
	
	public JSONArray getLogs(int eventLogId, String deviceId, String startTime, String endTime) {
		JSONArray jECodes = new JSONArray();
		jECodes.put(logListcode.get(eventLogId));
		return getLogs(jECodes, deviceId, startTime, endTime);
	}
	
	public JSONArray getLogs(String deviceId, String startTime, String endTime) {
		return getLogs(jEventCode, deviceId, startTime, endTime);
	}
	
	public JSONArray getAllLogs(String startTime, String endTime) {
		JSONArray jLogDate = new JSONArray();
		jLogDate.put(startTime);
		jLogDate.put(endTime);
		JSONObject jLogReq = new JSONObject();
		jLogReq.put("datetime", jLogDate);
		jLogReq.put("limit", 0);
		jLogReq.put("offset", 0);

		String vResults = dev.getEventLog(jLogReq);
		JSONObject jLog = new JSONObject(vResults);
		JSONArray rLog = jLog.getJSONArray("records");
		
		return rLog;
	}
	
	public  Vector<Vector<String>> getRecords(JSONArray rLog) {
		Vector<Vector<String>> recData = new Vector<Vector<String>>();
		for(int i = rLog.length() - 1; i >= 0; i--) {
			Vector<String> row = new Vector<String>();  
			row.add(rLog.getJSONObject(i).getString("datetime"));
			row.add(rLog.getJSONObject(i).getJSONObject("device").getInt("id")+"");
			row.add(rLog.getJSONObject(i).getJSONObject("device").getString("name"));
			if(rLog.getJSONObject(i).has("user")) {
				row.add(rLog.getJSONObject(i).getJSONObject("user").getString("user_id"));
			} else {
				row.add(" ");
			} if(rLog.getJSONObject(i).has("user") && rLog.getJSONObject(i).getJSONObject("user").has("name")) {
				row.add(rLog.getJSONObject(i).getJSONObject("user").getString("name"));
			} else {
				row.add("");
			}
			row.add(rLog.getJSONObject(i).getJSONObject("event_type").getString("description"));
			recData.add(row);
		}
		return recData;
	}	
	
	public JSONArray getLogs(String deviceId, int minutes) {
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		sdfDate.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		Date now = new Date();
		Date before = new Date(now.getTime()  - (1000 * 60 * minutes));
		String startDate = sdfDate.format(before) + ".00Z";
		String endDate = sdfDate.format(now) + ".00Z";
		
		return getLogs(deviceId, startDate, endDate);
	}
	
	public JSONArray getLogs(int minutes) {
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		sdfDate.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		Date now = new Date();
		Date before = new Date(now.getTime()  - (1000 * 60 * minutes));
		String startDate = sdfDate.format(before) + ".00Z";
		String endDate = sdfDate.format(now) + ".00Z";
		
		return getLogs(startDate, endDate);
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
    
}
