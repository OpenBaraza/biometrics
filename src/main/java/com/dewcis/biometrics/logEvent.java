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

	private String[] eventCodeName, logListcode = null;

	public logEvent(Device dev) {
		this.dev = dev;
		
		columnNames = new Vector<String>();
		columnNames.add("Date Time"); columnNames.add("Device ID"); columnNames.add("Device Name");
		columnNames.add("Entity ID"); columnNames.add("User Name");columnNames.add("Event");

		//get codlist referance logs name and code.
		String referencResult = dev.eventsType();

		JSONObject jsonObject = new JSONObject(referencResult);
		JSONArray tsmresponse = (JSONArray) jsonObject.get("records");

		ArrayList<String> list = new ArrayList<String>();
		ArrayList<String> list1 = new ArrayList<String>();

		JSONArray jEventcode = new JSONArray();
		list.add("None");
		list1.add("0000");
		for(int i=0; i<tsmresponse.length(); i++){
			list.add(tsmresponse.getJSONObject(i).getString("description"));
			list1.add(""+tsmresponse.getJSONObject(i).getInt("code")+"");
			jEventcode.put(tsmresponse.getJSONObject(i).getInt("code"));
		}

		eventCodeName = list.toArray(new String[0]);
		logListcode = list1.toArray(new String[0]);

        
        //get available device ID list
		String avalableDevice = dev.deviceList();

		JSONObject jAvailable = new JSONObject(avalableDevice);
		JSONArray aAvailable = (JSONArray) jAvailable.get("records");

		JSONObject jDevicecomp = new JSONObject();
		JSONArray jDevicelist = new JSONArray();

		int j = 01;
		if (aAvailable.length()==0) {
			System.out.println("No avilable deviceList");
			jDevicecomp.put("device_id", "0000");
			jDevicecomp.put("end_datetime", YearMonth.now()+"-"+Month.from(LocalDate.now()).length(true)+"T23:59:00.00Z");
			jDevicecomp.put("start_datetime", YearMonth.now()+"-0"+j+"T00:00:00.00Z");

			jDevicelist.put(jDevicecomp);
		}else if (aAvailable.length()!=0) {
			for(int i=0; i<aAvailable.length(); i++){
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

		rowData = new Vector<Vector<String>>();

		for(int i=0; i<aLog.length(); i++){
			Vector<String> row = new Vector<String>();  
			row.add(aLog.getJSONObject(i).getString("datetime"));
			row.add(aLog.getJSONObject(i).getJSONObject("device").getInt("id")+"");
			row.add(aLog.getJSONObject(i).getJSONObject("device").getString("name"));
			if(aLog.getJSONObject(i).has("user")) {
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

	public Vector<String> getColumnNames() {
		return columnNames;
	}

	public Vector<Vector<String>> getRowData() {
		return rowData;
	}

	public String[] getEventCodeName() {
		return eventCodeName;
	}

	public String[] getLogListcode() {
		return logListcode;
	}
    
}
