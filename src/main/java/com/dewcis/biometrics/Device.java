package com.dewcis.biometrics;

import com.github.sarxos.webcam.Webcam;
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

import com.dewcis.utils.webdav;

public class Device {
	Logger log = Logger.getLogger(Device.class.getName());

	base_url base = new base_url();
	Map<String, String> mapResults = base.base_url();
	String baseUrl = mapResults.get("baseUrl");
	
	//Getting sessionid for the logged in user
	public String login(String apiName, String userId, String password,String url) {
		
		String sessionId = null;
		
		JSONObject jLogin = new JSONObject();
		jLogin.put("mobile_app_version", "");
		jLogin.put("mobile_device_type", "");
		jLogin.put("mobile_os_version", "");
		jLogin.put("notification_token", "");
		jLogin.put("name", apiName);
		jLogin.put("password", password);
		jLogin.put("user_id", userId);
		
		httpClient client = new httpClient();
		String userFile = "/login";
        String uri = url + userFile;
		String cookies = client.getCookies(uri, jLogin.toString());
		
		System.out.println("BASE COOKIES : " + cookies);
		if(cookies != null) sessionId = cookies.split("=")[1].split(";")[0].trim();
		System.out.println("BASE SESSION : " + sessionId);
		
		return sessionId;
	}
	
	//Adding user to the biostar server
	public String addUser(JSONObject jStudent,String sessionId) {
	    String userFile = "/users";
	    String uri = baseUrl + userFile;
	    httpClient post = new httpClient();
	    String results = post.post(uri,jStudent.toString(), sessionId);

	    return results;

    }
    
    //Scanning fingerprint and getting the results
    public String scan(String deviceID,String sessionId ){
    
	    String userFile = "/devices/"+deviceID+"/scan_fingerprint";

	    JSONObject jscan = new JSONObject();
	    jscan.put("enroll_quality","80");
	    jscan.put("retrieve_raw_image",true);
	    
	    String uri = baseUrl + userFile;

	    httpClient post = new httpClient();
	    String results = post.post(uri, jscan.toString(), sessionId);

	    return results;
	}
	
	//Getting users list from the Biostar server
	public String userslist(String sessionId ) throws URISyntaxException{

		String userFile = "/users";
		String url = baseUrl + userFile;

		URI uri = new URIBuilder(url)
				.addParameter("limit", "0")
				.addParameter("offset", "0")
				.build();

		httpClient get = new httpClient();
		String results = get.get(uri, sessionId);

		return results;
	}
	
	//Enrolling user scanned finger prints to the server
	public String enroll(String user_id,String sessionId,JSONObject jenroll){
		
		String userFile = "/users/"+user_id+"/fingerprint_templates";
		String contentType = "application/json";

		String uri = baseUrl + userFile;

		httpClient put = new httpClient();
		String results = put.put(uri, jenroll.toString(), sessionId,contentType);

		return results;
	}
	
	//Activate or Deactivate a user
	public String acinUser(String user_id,String sessionId,JSONObject jACINuser){

	    String userFile = "/users/"+user_id+"";
	    String contentType = "application/json";

	    String uri = baseUrl + userFile;

	    httpClient put = new httpClient();
	    String results = put.put(uri, jACINuser.toString(), sessionId,contentType);
	    
	    return results;
	}
	
	//Getting one user details
	public String userDetails(String user_id,String sessionId) {
		String results =null;
        try {
            String userFile = "/users/"+user_id+"";
            String url = baseUrl + userFile;
            
            URI uri = new URIBuilder(url)
                    .build();
            
            httpClient get = new httpClient();
            results = get.get(uri, sessionId);
            
           
        } catch (URISyntaxException ex) {
            Logger.getLogger(Device.class.getName()).log(Level.SEVERE, null, ex);
        }
         return results;
	}
	
	//Deleting user from the device
	public void userDelDevice(String user_id,String deviceID,String sessionId) {
		String userFile = "/devices/"+deviceID+"/users/"+user_id;
		String uri = baseUrl + userFile;

		httpClient del = new httpClient();
		del.delete(uri,sessionId);
	}
	
	//Getting avilable device list from the server
	public String deviceList(String sessionId) {
        String results =null;
        try {
            String userFile = "/devices";
            String url = baseUrl + userFile;
            
            URI uri = new URIBuilder(url)
                    .addParameter("group_id", "")
                    .addParameter("limit", "0")
                    .addParameter("offset", "0")
                    .build();
            
            httpClient get = new httpClient();
            results = get.get(uri, sessionId);
            
            
        } catch (URISyntaxException ex) {
            Logger.getLogger(Device.class.getName()).log(Level.SEVERE, null, ex);
        }
        return results;
	}
	
	//Getting the log event types name,code and descriptions
	public String eventsType(String sessionId) {
        String results=null;
        try {
            
            String userFile = "/references/event_types";
            
            String url = baseUrl + userFile;
            
            URI uri = new URIBuilder(url)
                    .build();
            
            httpClient get = new httpClient();
            results = get.get(uri, sessionId);
            
            
            
        } catch (URISyntaxException ex) {
            Logger.getLogger(Device.class.getName()).log(Level.SEVERE, null, ex);
        }
        return results;
	}
	
	//Getting mothly log events occured
	public String mothlyLogEvent(JSONObject jEventlog,String sessionId){
	    
	    String userFile = "/monitoring/event_log/search_by_device";
            
        String uri = baseUrl + userFile;

	    httpClient post = new httpClient();
	    String results = post.post(uri, jEventlog.toString(), sessionId);

	    return results;
	}

	//Searching for log events that have occured by device
	public String searchLogEvent(JSONObject jEventlog,String sessionId){

        String userFile = "/monitoring/event_log/search_by_device";
        String uri = baseUrl + userFile;
        
        httpClient post = new httpClient();
        String results = post.post(uri, jEventlog.toString(), sessionId);

        return results;
	}
	
	//Add user to the device you want
	public void addUserDevice(JSONObject jUsersID,String deviceID,String sessionId) {

		String userFile = "/devices/"+deviceID+"/users";
		String uri = baseUrl + userFile;

		httpClient post = new httpClient();
		post.post(uri, jUsersID.toString(), sessionId);

	}
	
	//Take photo from a webcam connected to your machine
	public String takePhoto(String user_id){

		String encodedFile = null;

		Webcam webcam = Webcam.getDefault();

		if (webcam != null) {
            try {
                BufferedImage image = webcam.getImage();
                
                ByteArrayOutputStream os = new ByteArrayOutputStream();
				ImageIO.write(image, "png", os);
				InputStream is = new ByteArrayInputStream(os.toByteArray());
				
				// Repository access
				String wdUrl = "https://demo.dewcis.com/repository/webdav/ueab/";
				webdav wdv = new webdav(wdUrl, "repository", "baraza");
				
				wdv.saveFile(is, "pp_"+user_id+".png");
                
				base64Decoder myimage = new base64Decoder();
				ImageIO.write(image, "PNG", new File(""+myimage.results+"/user photo images/"+user_id+".PNG"));
            } catch (IOException ex) {
				log.log(Level.SEVERE, null, ex);
            }
		} else {
			JOptionPane.showMessageDialog(null,"No webcam detected");
		}

		return encodedFile;
	}

}
