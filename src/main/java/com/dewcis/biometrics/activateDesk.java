package com.dewcis.biometrics;

import java.sql.Connection;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Logger;


import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.border.EtchedBorder;
import javax.swing.JDesktopPane;
import javax.swing.ImageIcon;

import org.json.JSONObject;
import java.awt.Image;

import javax.swing.border.LineBorder;

public class activateDesk implements ActionListener {
	Logger log = Logger.getLogger(enrollDesk.class.getName());

	Connection db = null;
	Device dev = new Device();
	
	JFrame eFrame;
	JDialog eDialog;
	JSONObject jStudent;
	JPanel mainPanel, detailPanel, buttonPanel, fpPanel, camPanel, statusPanel;
	List<JButton> btns;
	List<JLabel> lbls;
	List<JLabel> lblPhoto;
	List<JDesktopPane> dsk;

	String sessionId = "";
	
	base64Decoder myImage = new base64Decoder();

	public activateDesk(Vector<String> titles,String student, String sessionId) {
		this.sessionId = sessionId;
		
		jStudent = new JSONObject(student);

		mainPanel = new JPanel(null);
		
		// Fields panel with fields
		detailPanel = new JPanel(null);
		detailPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Details"));
		detailPanel.setBounds(5, 5, 800, 150);
		mainPanel.add(detailPanel);
		
		addField(titles.get(0), jStudent.getString("login_id"), 10, 10, 120, 20, 200);
		addField(titles.get(1), jStudent.getString("name"), 400, 10, 120, 20, 200);
		addField(titles.get(2), jStudent.getString("user_id"), 10, 30, 120, 20, 200);
		addField(titles.get(3), jStudent.getString("phone_number"), 400, 30, 120, 20, 200);
		addField(titles.get(4), jStudent.getString("email"), 10, 50, 120, 20, 200);
		addField("Status",		jStudent.getString("status"), 400, 50, 120, 20, 200);

		// Butons panel
		buttonPanel = new JPanel(null);
		buttonPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Functions"));
		buttonPanel.setBounds(5, 500, 850, 70);
		mainPanel.add(buttonPanel);
		
		btns = new ArrayList<JButton>();
		addButton("Activate", 600, 20, 120, 25, false);
		addButton("Close", 750, 20, 75, 25, true);
        btns.get(0).setVisible(true);
		
		// Fingerprint panel
		lbls = new ArrayList<JLabel>();
		fpPanel = new JPanel(null);
		fpPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Finger Prints"));
		fpPanel.setBounds(5, 180, 400, 300);
		mainPanel.add(fpPanel);

		ImageIcon image1 = new ImageIcon(""+myImage.results+"/finger print images/"+jStudent.getString("user_id")+"T1"+".PNG");
        ImageIcon image2 = new ImageIcon(""+myImage.results+"/finger print images/"+jStudent.getString("user_id")+"T2"+".PNG");
		Image fimage1 = image1.getImage();
        Image fimage2 = image2.getImage();
		Image fnewimg1 = fimage1.getScaledInstance(180,200,  Image.SCALE_SMOOTH);
        Image fnewimg2 = fimage2.getScaledInstance(180,200,  Image.SCALE_SMOOTH);
		image1 = new ImageIcon(fnewimg1);
        image2 = new ImageIcon(fnewimg2);

		addFinger(image1,10,80,180,200);
		addFinger(image2,200,80,180,200);

		// Camera panel
		lblPhoto = new ArrayList<JLabel>();
		dsk = new ArrayList<JDesktopPane>();
		camPanel = new JPanel(null);
		camPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Photo"));
		camPanel.setBounds(425, 180, 350, 300);
		mainPanel.add(camPanel);

		ImageIcon pImage = new ImageIcon(""+myImage.results+"/user photo images/"+jStudent.getString("user_id")+".PNG");
		Image pimage1 = pImage.getImage();
		Image pnewimg1 = pimage1.getScaledInstance(330,240,  Image.SCALE_SMOOTH);
		pImage = new ImageIcon(pnewimg1);

		addPhoto(pImage,10,30,330, 240);


		// Status panel
		statusPanel = new JPanel(null);
		statusPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Status"));
		statusPanel.setBounds(5, 580, 850, 70);
		mainPanel.add(statusPanel);
		
		// Load on main form
		eFrame = new JFrame("Enroll");
		eDialog = new JDialog(eFrame , "Activate User", true);
		eDialog.setSize(900, 700);
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

	public void addFinger(ImageIcon fingerTemplate, int x, int y, int w, int h) {

		JLabel lbFinger = new JLabel();
		lbFinger.setBounds(x, y, w, h);
		lbFinger.setIcon(fingerTemplate);
		lbFinger.setBorder(new LineBorder(Color.black, 3));
		fpPanel.add(lbFinger);
		lbls.add(lbFinger);
	}

	public void addPhoto(ImageIcon photo,int x, int y, int w, int h) {

		JLabel photoView = new JLabel();
		photoView.setBounds(x, y, w, h);
		photoView.setIcon(photo);
		photoView.setBorder(new LineBorder(Color.black, 2));
		camPanel.add(photoView);
		lblPhoto.add(photoView);
	}

	public void addButton(String btTitle, int x, int y, int w, int h, boolean enabled) {
		
		JButton btn = new JButton(btTitle);
		btn.setBounds(x, y, w, h);
		buttonPanel.add(btn);
		btn.addActionListener(this);
		btn.setVisible(enabled);
		btns.add(btn);
	}
	
	public void actionPerformed(ActionEvent ev) {
		if(ev.getActionCommand().equals("Close")) {
			eDialog.dispose();
		}

		if(ev.getActionCommand().equals("Activate")) {
            jStudent.remove("status");
            jStudent.put("status", "AC");
            dev.acinUser(jStudent.getString("user_id"),sessionId,jStudent);
            btns.get(0).setEnabled(false);
            
        }
		
	}
    
}
