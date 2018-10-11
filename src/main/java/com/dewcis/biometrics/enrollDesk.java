package com.dewcis.biometrics;

import java.sql.Connection;
import java.io.InputStream;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Logger;
import java.net.MalformedURLException;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.border.EtchedBorder;
import javax.swing.JDesktopPane;
import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.border.LineBorder;

import org.json.JSONObject;
import org.json.JSONArray;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

public class enrollDesk implements ActionListener {
	Logger log = Logger.getLogger(enrollDesk.class.getName());

	Connection db = null;
	
	Device dev = null;
	
	JFrame eFrame;
	JDialog eDialog;
	JSONObject jStudent;
	JPanel mainPanel, detailPanel, buttonPanel, fpPanel, camPanel, statusPanel;
	List<JButton> btns;
	List<JLabel> lbls;
	List<JLabel> msg;
	List<JLabel> lblPhoto;
	List<JDesktopPane> dsk;

	String sessionId = "";
	String rgResults = "";

	JTextField tfDevice;				//device text field

	JSONObject jfinger = new JSONObject();
	JSONArray jarrayFinger = new JSONArray();
	JSONObject jfingerItem1 = new JSONObject();
	JSONObject jfingerItem2 = new JSONObject();
	
	imageManager imageMgr = null;
	ImageIcon fImage1 = null;
	Image fimage1 = null;
	Image fNewimg1 = null;

	public enrollDesk(Vector<String> titles, Vector<String> rowData, Device dev, String deviceId) {
		this.dev = dev;

		imageMgr = new imageManager(dev.getConfigs());
		fImage1 = new ImageIcon(imageMgr.getImage("ftemplate1.png"));
		fimage1 = fImage1.getImage();
		fNewimg1 = fimage1.getScaledInstance(180,200,  Image.SCALE_SMOOTH);
		
		mainPanel = new JPanel(null);
		
		// Fields panel with fields
		detailPanel = new JPanel(null);
		detailPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Details"));
		detailPanel.setBounds(5, 5, 900, 150);
		mainPanel.add(detailPanel);
		
		addField(titles.get(0), rowData.get(0), 10, 10, 120, 20, 300);
		addField(titles.get(1), rowData.get(1), 400, 10, 120, 20, 300);
		addField(titles.get(2), rowData.get(2), 10, 30, 120, 20, 300);
		addField(titles.get(3), rowData.get(3), 400, 30, 120, 20, 300);
		addField(titles.get(4), rowData.get(4), 10, 50, 120, 20, 300);

		addJstudent(rowData);

		// Butons panel
		buttonPanel = new JPanel(null);
		buttonPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Functions"));
		buttonPanel.setBounds(5, 500, 900, 70);
		mainPanel.add(buttonPanel);
		
		btns = new ArrayList<JButton>();
		addButton("Register", 10, 20, 100, 25, true);
		addButton("Scan 1", 120, 20, 90, 25, false);
		addButton("Scan 2", 220, 20, 90, 25, false);
		addButton("Camera", 320, 20, 90, 25, false);
		addButton("Photo", 420, 20, 90, 25, false);
		addButton("Enroll", 520, 20, 90, 25, false);
		addButton("Close", 620, 20, 90, 25, true);
		addButton("Cancel", 720, 20, 90, 25, false);
		
		// Fingerprint panel
		lbls = new ArrayList<JLabel>();
		fpPanel = new JPanel(null);
		fpPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Finger Prints"));
		fpPanel.setBounds(5, 180, 400, 300);
		mainPanel.add(fpPanel);

		fImage1 = new ImageIcon(fNewimg1);

		addDevice("Device ID", deviceId, 10, 20, 100, 20, 200);
		addFinger(fImage1,10,80,180,200);
		addFinger(fImage1,200,80,180,200);

		// Camera panel
		lblPhoto = new ArrayList<JLabel>();
		dsk = new ArrayList<JDesktopPane>();
		camPanel = new JPanel(null);
		camPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Photo"));
		camPanel.setBounds(425, 180, 350, 300);
		mainPanel.add(camPanel);

		addDesktop(10,30,330, 240);
		addPhoto(10,30,330, 240);

		// Status panel
		msg = new ArrayList<JLabel>();
		statusPanel = new JPanel(null);
		statusPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Status"));
		statusPanel.setBounds(5, 580, 900, 70);
		mainPanel.add(statusPanel);

		addMessage("Message", 10, 10, 120, 20, 700);
		
		// Load on main form
		eFrame = new JFrame("Enroll");
		eDialog = new JDialog(eFrame , "Enroll User", true);
		eDialog.setSize(910, 700);
		eDialog.getContentPane().add(mainPanel, BorderLayout.CENTER);
		eDialog.setVisible(true);
	}
	
	public void addField(String fieldTitle, String fieldValue, int x, int y, int w, int h, int dw) {
		JLabel lbTitle = new JLabel(fieldTitle + " : ");
		lbTitle.setBounds(x, y, w, h);
		detailPanel.add(lbTitle);
		
		JLabel lbValue = new JLabel(fieldValue);
		lbValue.setBounds(x + w + 10, y, dw, h);
		detailPanel.add(lbValue);
	}

	public void addMessage(String fieldTitle, int x, int y, int w, int h, int dw) {
		JLabel lbTitle = new JLabel(fieldTitle + " : ");
		lbTitle.setBounds(x, y, w, h);
		statusPanel.add(lbTitle);
		
		JLabel lbValue = new JLabel();
		lbValue.setBounds(x + w + 10, y, dw, h);
		statusPanel.add(lbValue);
		msg.add(lbValue);
	}

	public void addFinger(ImageIcon fingerTemplate, int x, int y, int w, int h) {

		JLabel lbFinger = new JLabel();
		lbFinger.setBounds(x, y, w, h);
		lbFinger.setIcon(fingerTemplate);
		lbFinger.setBorder(new LineBorder(Color.black, 3));
		fpPanel.add(lbFinger);
		lbls.add(lbFinger);
	}

	public void addPhoto(int x, int y, int w, int h) {

		JLabel photoView = new JLabel();
		photoView.setBounds(x, y, w, h);
		photoView.setBorder(new LineBorder(Color.black, 2));
		camPanel.add(photoView);
		lblPhoto.add(photoView);
	}

	public void addDevice(String fieldTitle, String fieldValue, int x, int y, int w, int h, int dw) {
		JLabel lbTitle = new JLabel(fieldTitle + " : ");
		lbTitle.setBounds(x, y, w, h);
		fpPanel.add(lbTitle);
		
		tfDevice = new JTextField();
		tfDevice.setBounds(x + w + 5, y, dw, h);
		tfDevice.setText(fieldValue);
		fpPanel.add(tfDevice);
	}
	
	public void addButton(String btTitle, int x, int y, int w, int h, boolean enabled) {
		
		JButton btn = new JButton(btTitle);
		btn.setBounds(x, y, w, h);
		buttonPanel.add(btn);
		btn.addActionListener(this);
		btn.setEnabled(enabled);
		btns.add(btn);
	}

	public void addDesktop(int x, int y, int w, int h) {
		JDesktopPane desktopCam = new JDesktopPane();
		desktopCam.setBounds(x, y, w, h);
		desktopCam.setBackground(Color.black);
		desktopCam.setVisible(true);
		camPanel.add(desktopCam);
		dsk.add(desktopCam);
	}
	
	public void actionPerformed(ActionEvent ev) {
		if(ev.getActionCommand().equals("Register")) {
			rgResults = dev.addUser(jStudent);
			System.out.println("BASE REGISTER : " + rgResults);
			JSONObject jResults = new JSONObject(rgResults);
			msg.get(0).setText(jResults.getString("message"));

			if(jResults.has("status_code")) {
				if(jResults.getString("status_code").equals("CREATED")) {
					//Enabling buttons and disabling
					btns.get(0).setEnabled(false);
					btns.get(1).setEnabled(true);
					btns.get(6).setEnabled(false);
					btns.get(7).setEnabled(true);
					btns.get(1).requestFocus();
				}
			}
		} else if(ev.getActionCommand().equals("Scan 1")) {
			fImage1 = new ImageIcon(fNewimg1);
			lbls.get(0).setIcon(fImage1);
			
			FingerPrint fp = new FingerPrint(dev);
			Map<String, String> fpm = fp.scanVerify(tfDevice.getText());
			msg.get(0).setText(fpm.get("message"));
			System.out.println(fpm.get("message"));

			if(!fpm.containsKey("status_code")) {
				jfingerItem1.put("is_prepare_for_duress", false);
				jfingerItem1.put("template0", fpm.get("template0"));
				jfingerItem1.put("template1", fpm.get("template1"));
				
				BufferedImage sImage = imageMgr.saveImage(fpm.get("template_image0"), "fp_" + jStudent.getString("user_id") + "_t1.png");
				ImageIcon imageF1 = new ImageIcon(sImage);
				Image imgF1 = imageF1.getImage();
				Image newF1 = imgF1.getScaledInstance(180,200,  Image.SCALE_SMOOTH);
				imageF1 = new ImageIcon(newF1);
				lbls.get(0).setIcon(imageF1);
				
				//Enabling buttons and disabling
				btns.get(1).setEnabled(false);
				btns.get(2).setEnabled(true);
				btns.get(2).requestFocus();
			}
		} else if(ev.getActionCommand().equals("Scan 2")) {
			fImage1 = new ImageIcon(fNewimg1);
			lbls.get(1).setIcon(fImage1);
			
			FingerPrint fp = new FingerPrint(dev);
			Map<String, String> fpm = fp.scanVerify(tfDevice.getText());
			msg.get(0).setText(fpm.get("message"));
			System.out.println(fpm.get("message"));
			
			if(!fpm.containsKey("status_code")) {
				jfingerItem2.put("is_prepare_for_duress", false);
				jfingerItem2.put("template0", fpm.get("template0"));
				jfingerItem2.put("template1", fpm.get("template1"));
			
				BufferedImage sImage = imageMgr.saveImage(fpm.get("template_image0"), "fp_" + jStudent.getString("user_id") + "_t2.png");
				ImageIcon imageF2 = new ImageIcon(sImage);
				Image imgF2 = imageF2.getImage();
				Image newF2 = imgF2.getScaledInstance(180,200,  Image.SCALE_SMOOTH);
				imageF2 = new ImageIcon(newF2);
				lbls.get(1).setIcon(imageF2);
	            
	            //Enabling buttons and disabling
				btns.get(2).setEnabled(false);
				btns.get(3).setEnabled(true);
				btns.get(3).requestFocus();
			}
		} else if(ev.getActionCommand().equals("Camera")) {
			Dimension[] nonStandardResolutions = new Dimension[] {WebcamResolution.HD.getSize(),};
			Webcam webcam = Webcam.getDefault();

			if (webcam != null){
				webcam.setCustomViewSizes(nonStandardResolutions);
				webcam.setViewSize(WebcamResolution.HD.getSize());
				webcam.open(true);
				msg.get(0).setText("Webcam Opened");

				WebcamPanel panel = new WebcamPanel(webcam, false);
				panel.setPreferredSize(WebcamResolution.QVGA.getSize());
				panel.setFPSDisplayed(false);
				panel.setFPSLimited(true);
				panel.setFPSLimit(20);
				panel.start();

				JInternalFrame window = new JInternalFrame();
				((javax.swing.plaf.basic.BasicInternalFrameUI)window.getUI()).setNorthPane(null);
				window.add(panel);
				window.pack();
				window.setMaximumSize(WebcamResolution.QVGA.getSize());
				window.setVisible(true);

				dsk.get(0).add(window);
				
				//Enabling buttons and disabling
				btns.get(3).setEnabled(false);
				btns.get(4).setEnabled(true);
				btns.get(4).requestFocus();
			} else {
				JOptionPane.showMessageDialog(null,"No webcam detected");
				msg.get(0).setText("No webcam detected");
			}
        } if(ev.getActionCommand().equals("Photo")){
			BufferedImage photoTaken = dev.takePhoto(jStudent.getString("user_id"));

			if (photoTaken != null) {
				Webcam webcam = Webcam.getDefault();
				webcam.close();
				
				dsk.get(0).setVisible(false);

				ImageIcon pImage = new ImageIcon(photoTaken);
				Image imgP = pImage.getImage();
				Image newPImg = imgP.getScaledInstance(300,240,  Image.SCALE_SMOOTH);
				pImage = new ImageIcon(newPImg);

				msg.get(0).setText("Photo Taken Successfully");
				lblPhoto.get(0).setVisible(true);
				lblPhoto.get(0).setIcon(pImage);
				
				//Enabling buttons and disabling
				btns.get(3).setEnabled(true);
				btns.get(4).setEnabled(false);
				btns.get(5).setEnabled(true);
				btns.get(5).requestFocus();
			}
			
		} else if(ev.getActionCommand().equals("Enroll")) {
			if(jfingerItem1.has("template0") && jfingerItem2.has("template0")) {
				jarrayFinger.put(jfingerItem1);
				jarrayFinger.put(jfingerItem2);
				jfinger.put("fingerprint_template_list", jarrayFinger);
				
				//System.out.println("BASE 2010 Finger Prints : " + jfinger.toString());
				
				String enResults = dev.enroll(jStudent.getString("user_id"), jfinger);
				JSONObject jResults = new JSONObject(enResults);
System.out.println("BASE 2010 : " + enResults);
				
				msg.get(0).setText(jResults.getString("message"));
				
				//Enabling buttons and disabling
				btns.get(3).setEnabled(false);
				btns.get(5).setEnabled(false);
			    btns.get(6).setEnabled(true);
			    btns.get(6).requestFocus();
			}
		} else if(ev.getActionCommand().equals("Close")) {
			eDialog.dispose();
		} else if(ev.getActionCommand().equals("Cancel")) {
			eDialog.dispose();
		}
		
	}

	public void addJstudent(Vector<String> rowData) {
		jStudent = new JSONObject();
		
		Map<String, String> cfgs = dev.getConfigs();
		
		jStudent.put("login_id", rowData.get(0));
		jStudent.put("name", rowData.get(1));
		jStudent.put("phone_number", rowData.get(3));
		jStudent.put("email", rowData.get(4));
		jStudent.put("user_id", rowData.get(2));
		jStudent.put("password", "password");
		jStudent.put("pin", "");
		jStudent.put("security_level", "");
		jStudent.put("start_datetime", "2017-01-13T00:00:00.000Z");
		jStudent.put("expiry_datetime", "2030-01-13T23:59:59.000Z");
		jStudent.put("status", "AC");
		
		JSONArray jAccessGroups = new JSONArray();
		JSONObject jAccessGroup = new JSONObject();
		jAccessGroup.put("id", cfgs.get("access_group_id"));
		jAccessGroup.put("included_by_user_group", "Yes");
		jAccessGroup.put("name", cfgs.get("access_group_name"));
		jAccessGroups.put(jAccessGroup);
		
		jStudent.put("access_groups", jAccessGroups);
		
		JSONObject jUserGroup = new JSONObject();
		jUserGroup.put("id", cfgs.get("user_group_id"));
		jUserGroup.put("name", cfgs.get("user_group_name"));
		
		jStudent.put("user_group", jUserGroup);
		
		JSONObject jpermission = new JSONObject();
		jpermission.put("id", "255");
		jpermission.put("name", "User");
		
		
		JSONArray jpermissions = new JSONArray();
		JSONObject jpermissionls = new JSONObject();
		jpermissionls.put("allowed_group_id_list", "[1]");
		jpermissionls.put("module", "CARD");
		jpermissionls.put("read", true);
		jpermissionls.put("write", true);
		jpermissions.put(jpermissionls);
		
		jpermission.put("permissions", jpermissions);
		jStudent.put("permission", jpermission);        
	}
}
