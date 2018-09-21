package com.dewcis.biometrics;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.apache.http.client.utils.URIBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import com.github.sarxos.webcam.Webcam;

import com.dewcis.utils.webdav;

public class Device {
	Logger log = Logger.getLogger(Device.class.getName());
	
	Map<String, String> cfgs;
	String baseUrl = "";
	String sessionId = null;

	public Device(Map<String, String> cfgs, String sessionId) {
		this.cfgs = cfgs;
		this.sessionId = sessionId;
		if(sessionId == null) login();
	}
	
	//Getting sessionid for the logged in user
	public String login() {
		JSONObject jLogin = new JSONObject();
		jLogin.put("mobile_app_version", "");
		jLogin.put("mobile_device_type", "");
		jLogin.put("mobile_os_version", "");
		jLogin.put("notification_token", "");
		jLogin.put("name", cfgs.get("api_name"));
		jLogin.put("password", cfgs.get("user_password"));
		jLogin.put("user_id", cfgs.get("user_name"));
		
		baseUrl = cfgs.get("base_url");
		
		httpClient client = new httpClient(cfgs.get("domain"));
        String uri = baseUrl + "/login";
		String cookies = client.getCookies(uri, jLogin.toString());
		System.out.println("BASE COOKIES : " + cookies);
		
		if(cookies != null) sessionId = cookies.split("=")[1].split(";")[0].trim();
		System.out.println("BASE SESSION : " + sessionId);
		
		return sessionId;
	}
	
	//Adding user to the biostar server
	public String addUser(JSONObject jStudent) {
	    String uri = baseUrl + "/users";
	    httpClient client = new httpClient(cfgs.get("domain"));
	    String results = client.post(uri, jStudent.toString(), sessionId);
	    return results;
	}

    //Verify user Finger Print with the biostar server Fingerprints
	public String verifyScan(String deviceID, JSONObject jVerify) {
		String uri = baseUrl + "/devices/"+deviceID+"/verify_fingerprint";
		httpClient client = new httpClient(cfgs.get("domain"));
		String results = client.post(uri, jVerify.toString(), sessionId);
		return results;
	}
    
    //Scanning fingerprint and getting the results
	public String scan(String deviceID){
	    String uri = baseUrl + "/devices/" + deviceID + "/scan_fingerprint";

	    JSONObject jscan = new JSONObject();
	    jscan.put("enroll_quality","80");
	    jscan.put("retrieve_raw_image",true);

	    httpClient client = new httpClient(cfgs.get("domain"));
	    String results = client.post(uri, jscan.toString(), sessionId);

	    return results;
	}
	
	// Getting users list from the Biostar server
	public String usersList() {
		String url = baseUrl + "/users";
		String results = null;
		
		try {
			URI uri = new URIBuilder(url)
				.addParameter("limit", "0")
				.addParameter("offset", "0")
				.build();

			httpClient client = new httpClient(cfgs.get("domain"));
			results = client.get(uri, sessionId);
		} catch (URISyntaxException ex) {
			log.severe("URI Error " + ex);
		}

		return results;
	}

	// Getting user Fingerprint list from the Biostar server
	public String userFingerPrint(String user_id) {
		String url = baseUrl + "/users"+user_id+"/fingerprint_templates";
		String results = null;
		
		try {
			URI uri = new URIBuilder(url).build();
			httpClient client = new httpClient(cfgs.get("domain"));
			results = client.get(uri, sessionId);
		} catch (URISyntaxException ex) {
			log.severe("URI Error " + ex);
		}

		return results;
	}

	
	//Enrolling user scanned finger prints to the server
	public String enroll(String user_id, JSONObject jenroll) {
		String uri = baseUrl + "/users/" + user_id + "/fingerprint_templates";
		String contentType = "application/json";

		httpClient client = new httpClient(cfgs.get("domain"));
		String results = client.put(uri, jenroll.toString(), sessionId, contentType);

		return results;
	}
	
	//Activate or Deactivate a user
	public String acinUser(String user_id, JSONObject jACINuser){
	    String uri = baseUrl + "/users/" + user_id;
	    String contentType = "application/json";

	    httpClient client = new httpClient(cfgs.get("domain"));
	    String results = client.put(uri, jACINuser.toString(), sessionId, contentType);
	    
	    return results;
	}
	
	//Getting one user details
	public String userDetails(String user_id) {
		String url = baseUrl + "/users/" + user_id;
		String results = null;
		try {
			URI uri = new URIBuilder(url)
				.build();

			httpClient client = new httpClient(cfgs.get("domain"));
			results = client.get(uri, sessionId);
		} catch (URISyntaxException ex) {
			log.severe("URI Error " + ex);
		}
		return results;
	}
	
	//Deleting user from the device
	public void userDelDevice(String user_id, String deviceID) {
		String uri = baseUrl + "/devices/" + deviceID + "/users/" + user_id;
		httpClient del = new httpClient(cfgs.get("domain"));
		del.delete(uri, sessionId);
	}
	
	//Getting avilable device list from the server
	public String deviceList() {
		String url = baseUrl + "/devices";
		String results =null;
		try {
			URI uri = new URIBuilder(url)
				.addParameter("group_id", "")
				.addParameter("limit", "0")
				.addParameter("offset", "0")
				.build();

			httpClient client = new httpClient(cfgs.get("domain"));
			results = client.get(uri, sessionId);            
        } catch (URISyntaxException ex) {
			log.severe("URI Error " + ex);
		}
		return results;
	}
	
	//Getting the log event types name,code and descriptions
	public String eventsType() {
		String url = baseUrl + "/references/event_types";
		String results=null;
		try {
			URI uri = new URIBuilder(url)
					.build();
			
			httpClient client = new httpClient(cfgs.get("domain"));
			results = client.get(uri, sessionId);
		} catch (URISyntaxException ex) {
			log.severe("URI Error " + ex);
		}
		return results;
	}
	
	//Getting mothly log events occured
	public String mothlyLogEvent(JSONObject jEventlog){
	    String uri = baseUrl + "/monitoring/event_log/search_by_device";
	    httpClient client = new httpClient(cfgs.get("domain"));
	    String results = client.post(uri, jEventlog.toString(), sessionId);
	    return results;
	}

	//Searching for log events that have occured by device
	public String searchLogEvent(JSONObject jEventlog){
		String uri = baseUrl + "/monitoring/event_log/search_by_device";
		httpClient client = new httpClient(cfgs.get("domain"));
		String results = client.post(uri, jEventlog.toString(), sessionId);
		return results;
	}
	
	//Add user to the device you want
	public void addUserDevice(JSONObject jUsersID, String deviceID) {
		String uri = baseUrl + "/devices/"+deviceID+"/users";
		httpClient client = new httpClient(cfgs.get("domain"));
		client.post(uri, jUsersID.toString(), sessionId);
	}
	
	//Take photo from a webcam connected to your machine
	public BufferedImage takePhoto(String user_id){
		BufferedImage image = null;

		Webcam webcam = Webcam.getDefault();

		if (webcam != null) {
			try {
				image = webcam.getImage();
				
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				ImageIO.write(image, "png", os);
				InputStream is = new ByteArrayInputStream(os.toByteArray());
				
				// Repository access
				webdav wdv = new webdav(cfgs.get("webdav_path"), cfgs.get("webdav_username"), cfgs.get("webdav_password"));
				wdv.saveFile(is, "pp_"+user_id+".png");
			} catch (IOException ex) {
				log.log(Level.SEVERE, null, ex);
			}
		} else {
			JOptionPane.showMessageDialog(null,"No webcam detected");
		}

		return image;
	}
	
	public Map<String, String> getConfigs() {
		return cfgs;
	}
}
