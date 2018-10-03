package com.dewcis.biometrics;

import java.util.logging.Logger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.awt.GridLayout;
import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import com.dewcis.swing.BImagePanel;

public class loginDesk implements ActionListener {
	Logger log = Logger.getLogger(loginDesk.class.getName());

	Connection db = null;

	JFrame frame;
	BImagePanel imagePanel;
	JPanel loginPanel;
	JLabel lUserName, lPassword, clearStatus, loginStatus;
	JTextField tfUserName;
	JPasswordField pwPassword;
	JButton btClear, btOkay;

	public static void main(String args[]) {
		loginDesk ld = new loginDesk();
	}

	public loginDesk() {
		imagePanel = new BImagePanel("/images/background.jpg");
		loginPanel = new JPanel(new GridLayout(0, 2, 2, 2));
		loginPanel.setOpaque(false);
		imagePanel.add(loginPanel);
		loginPanel.setLocation(250, 200);
		loginPanel.setSize(400, 120);

		lUserName = new JLabel("User Name : ");
		tfUserName = new JTextField(25);
		loginPanel.add(lUserName);
		loginPanel.add(tfUserName);
		
		lPassword = new JLabel("Password : ");
		pwPassword = new JPasswordField();
		
		pwPassword.setActionCommand("Login");
		pwPassword.addActionListener(this);
		loginPanel.add(lPassword);
		loginPanel.add(pwPassword);

		btClear = new JButton("Clear");
		btClear.addActionListener(this);
		btOkay = new JButton("Login");
		btOkay.addActionListener(this);
		loginPanel.add(btClear);
		loginPanel.add(btOkay);

		clearStatus = new JLabel();
		loginStatus = new JLabel();
		loginPanel.add(clearStatus);
		loginPanel.add(loginStatus);
		
		frame = new JFrame("Baraza Project");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(imagePanel, BorderLayout.CENTER);
		frame.setSize(1000, 800);
		frame.setVisible(true);
	}
	
	
	public void actionPerformed(ActionEvent ev) {
		String aKey = ev.getActionCommand();

		if("Clear".equals(aKey)) {
			tfUserName.setText("");
			pwPassword.setText("");
		} else if("Login".equals(aKey)) {
			String myPassword = new String(pwPassword.getPassword());
			connectDB("jdbc:postgresql://umisdb/ueab", tfUserName.getText(), myPassword);
			
			if(db == null) {
				clearStatus.setText("Login error");
				loginStatus.setText("Invalid credentials");
			} else {
				frame.remove(imagePanel);

				mainDesk md = new mainDesk(db);
				frame.getContentPane().add(md, BorderLayout.CENTER);
				
				// Repaint main panel
				md.revalidate();
				md.repaint();

				// Repaint frame
				frame.revalidate();
				frame.repaint();
			}
		}
	}

	public void connectDB(String dbpath, String dbuser, String dbpassword) {
		try {
			db = DriverManager.getConnection(dbpath, dbuser, dbpassword);
		} catch (SQLException ex) {
			log.severe("Database connection Error. " + ex);
		}
	}

}
