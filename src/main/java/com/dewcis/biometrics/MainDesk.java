package com.dewcis.biometrics;

import java.sql.Connection;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.Date;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.Month;
import java.time.Clock;
import java.time.Duration;
import java.time.YearMonth;
import java.text.SimpleDateFormat;

import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Image;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JPanel;
import javax.swing.JDialog;
import javax.swing.JTabbedPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.BorderFactory;
import javax.swing.border.EtchedBorder;
import javax.swing.JDesktopPane;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dewcis.utils.DTableModel;

public class MainDesk extends JPanel implements MouseListener , ActionListener{
	Logger log = Logger.getLogger(MainDesk.class.getName());

	Connection db = null;
	String mySql = "";

	Enrolment enrolment = null;
	Configs cfgs = null;
	Device dev = null;
	ImageManager imageMgr = null;
	EventLogs eventLogs = null;
	VerifyStudent verifyStudent = null;
	boolean notVerifiying = true;

	List<JButton> btns;
	List<JTextField> txfs;
	JComboBox cmbDevices, cmbEventNames;
	
	Vector<String> deviceNames;
	Vector<Integer> deviceIds;
	Map<String, String> fields;
	List<String> fieldNames;
	Map<String, JLabel> stdFields;
	
	JTabbedPane tabbedPane = new JTabbedPane();
	JTabbedPane searchPane = new JTabbedPane();
	
	JPanel nonRegPanel, regPanel, verifyPanel, acInPanel, logPanel, filterPanel;
	JPanel devicePanel, searchPanel, studentPanel, picPanel, statusPanel;
	JTable tableReg, tableNon, tableIN,	tableLog;
	JTextField filterData;
	JLabel statusMsg, photoView;
	JComboBox fieldList, filterList;
	DTableModel logModel, tModel, tNonRegModel, tINModel;

	Vector<String> eventCodeName;

	public MainDesk(Connection db) {
		super(new BorderLayout());
		this.db = db;
		
		deviceNames = new Vector<String>();
		deviceIds = new Vector<Integer>();
		btns = new ArrayList<JButton>();
		txfs = new ArrayList<JTextField>();
		stdFields = new HashMap<String, JLabel>();

		// Non Registred user panel
		nonRegPanel = new JPanel(new BorderLayout());
		tabbedPane.addTab("Not Registred", nonRegPanel);

		//Registred user panel
		regPanel = new JPanel(new BorderLayout());
		tabbedPane.addTab("Registred", regPanel);

		//activate Deactivate user panel
		acInPanel = new JPanel(new BorderLayout());
		tabbedPane.addTab("In Activate Users", acInPanel);

		//log user panel
		logPanel = new JPanel(new BorderLayout());
		tabbedPane.addTab("Users Logs", logPanel);
		
		// Getting details from the config.txt from class base_url
		cfgs = new Configs(db);
		dev = new Device(cfgs.getConfigs(), null);
		getDeviceList();
		imageMgr = new ImageManager(dev.getConfigs());

		// Add students on table
		getStudents();

		verifyStudent = new VerifyStudent();
		
		eventLogs = new EventLogs(dev);
		JSONArray rLog = eventLogs.getLogs(getCurrentDate(), getCurrentDate());
		eventCodeName = eventLogs.getEventCodeName();
		
		logModel = new DTableModel(eventLogs.getColumnNames(), eventLogs.getRecords(rLog));
		tableLog = new JTable(logModel);
		JScrollPane scrollPanereg = new JScrollPane(tableLog);
		logPanel.add(scrollPanereg, BorderLayout.CENTER);

		//Verify user panel
		verifyPanel = new JPanel(null);

		// Search panel for enter search parameters
		searchPanel = new JPanel(new GridLayout(0, 4));
		addSearch("Start Date");
		addSearch("End Date");
		addCombox("Event names", eventCodeName);
		addButton(searchPanel, "Search", 350, 20, 150, 25, true);
		addButton(searchPanel, "Logs", 550, 20, 150, 25, true);
		logPanel.add(searchPanel, BorderLayout.PAGE_END);

		// Student details
		studentPanel = new JPanel(null);
		addPanel(studentPanel, "Verify", 5, 10, 900, 150);
		addField("studentid", "Student ID", 10, 20, 120, 20, 300);
		addField("studentname", "Student Name", 400, 20, 120, 20, 300);
		addField("telno", "Tel No", 10, 50, 120, 20, 300);
		addField("email", "Email", 400, 50, 120, 20, 300);
		addButton(studentPanel, "Verify", 800, 100, 90, 25, true);
		
		picPanel = new JPanel(null);
		photoView = new JLabel();
		photoView.setBounds(10, 30, 330, 240);
		picPanel.add(photoView);
		addPanel(picPanel, "Photo", 10, 180, 350, 300);

		// Status log panel
		statusPanel = new JPanel(null);
		addPanel(statusPanel, "Status", 5, 500, 800, 100);
		addMessage("Message", 10, 10, 120, 20, 600);

		tabbedPane.addTab("Verify User / Search logs", verifyPanel);
		super.add(tabbedPane, BorderLayout.CENTER);

		filterPanel = new JPanel(new FlowLayout());
		addDevice("Device ID ");
		filterData = new JTextField(25);
		filterData.setActionCommand("Filter");
		filterData.addActionListener(this);
		String[] filterstr = {"ILIKE", "LIKE", "=", ">", "<", "<=", ">="};	
		fieldList = new JComboBox<String>(tNonRegModel.getTitles());
		filterList = new JComboBox<String>(filterstr);
		filterPanel.add(fieldList);
		filterPanel.add(filterList);
		filterPanel.add(filterData);
		super.add(filterPanel, BorderLayout.PAGE_END);
	}
	
	public void getStudents() {
		mySql = "SELECT s.studentid, s.studentname, s.telno, s.email, e.entity_id "
			+ "FROM studentdegreeview s INNER JOIN entitys e ON s.studentid = e.user_name "
			+ "INNER JOIN qstudents qs ON s.studentdegreeid = qs.studentdegreeid "
			+ "INNER JOIN quarters q ON qs.quarterid = q.quarterid "
			+ "WHERE (q.active = true) AND (s.telno is not null) AND (s.email is not null) "
			+ "LIMIT 200";

		fields = new HashMap<String, String>();
		fields.put("studentid", "Student ID");
		fields.put("studentname", "Student Name");
		fields.put("telno", "Tel No");
		fields.put("email", "EMail");
		fields.put("entity_id", "Entity ID");
		fieldNames = new ArrayList<String>(fields.keySet());
        
        // Get device and registered list
		enrolment = new Enrolment();
		enrolment.usersList(dev, false);
		enrolment.getStudents(db, mySql, fields);
        
		//Creating table for Non Registered students.
		tNonRegModel = new DTableModel(fields, enrolment.getUnRegistred());
		tableNon = new JTable(tNonRegModel);
		tableNon.addMouseListener(this);
		JScrollPane scrollPane = new JScrollPane(tableNon);
		nonRegPanel.add(scrollPane, BorderLayout.CENTER);
		
		//Creating table for Registered students
		tModel = new DTableModel(fields, enrolment.getRegistred());
		tableReg = new JTable(tModel);
		tableReg.addMouseListener(this);
		JScrollPane scrollPanereg = new JScrollPane(tableReg);
		regPanel.add(scrollPanereg, BorderLayout.CENTER);

		//Creating table for Inactive students
		tINModel = new DTableModel(fields, enrolment.getInActive());
		tableIN = new JTable(tINModel);
		tableIN.addMouseListener(this);
		JScrollPane scrollPaneInAc = new JScrollPane(tableIN);
		acInPanel.add(scrollPaneInAc, BorderLayout.CENTER);
	}
	
	public void addDevice(String fieldTitle) {
		cmbDevices = new JComboBox(deviceNames);
		filterPanel.add(cmbDevices);
	}
	
	public void addPanel(JPanel nPanel, String btTitle, int x, int y, int w, int h) {
		nPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), btTitle));
		nPanel.setBounds(x, y, w, h);
		verifyPanel.add(nPanel);
	}

	public void addButton(JPanel nPanel, String btTitle, int x, int y, int w, int h, boolean enabled) {
		JButton btn = new JButton(btTitle);
		btn.setBounds(x, y, w, h);
		btn.addActionListener(this);
		btn.setEnabled(enabled);
		nPanel.add(btn);
		btns.add(btn);
	}

	public void addSearch(String fieldTitle) {
		JLabel lbTitle = new JLabel(fieldTitle + " : ");
		searchPanel.add(lbTitle);
		
		String dateValue = getCurrentDate();
		
		JTextField tfDevice = new JTextField(50);
		tfDevice.setText(dateValue);
		searchPanel.add(tfDevice);
		txfs.add(tfDevice);
	}
	
	public String getCurrentDate() {
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
		String dateValue = sdfDate.format(new Date());
		return dateValue;
	}

	public void addCombox(String fieldTitle, Vector<String> fieldValue) {
		JLabel lbTitle = new JLabel(fieldTitle + " : ");
		searchPanel.add(lbTitle);
		
		cmbEventNames = new JComboBox(fieldValue);
		searchPanel.add(cmbEventNames);
	}
	
	public void addMessage(String fieldTitle, int x, int y, int w, int h, int dw) {
		JLabel lbTitle = new JLabel(fieldTitle + " : ");
		lbTitle.setBounds(x, y, w, h);
		statusPanel.add(lbTitle);
		
		statusMsg = new JLabel();
		statusMsg.setBounds(x + w + 10, y, dw, h);
		statusPanel.add(statusMsg);
	}
	
	public void addField(String fieldKey, String fieldTitle, int x, int y, int w, int h, int dw) {
		JLabel lbTitle = new JLabel(fieldTitle + " : ");
		lbTitle.setBounds(x, y, w, h);
		studentPanel.add(lbTitle);
		
		JLabel lbValue = new JLabel("");
		lbValue.setBounds(x + w + 10, y, dw, h);
		stdFields.put(fieldKey, lbValue);
		studentPanel.add(lbValue);
	}
	
	public void getDeviceList() {	
		JSONObject jResp = new JSONObject(dev.getDeviceList());
		JSONArray jRecord = jResp.getJSONArray("records");

		for(int i = 0; i < jRecord.length(); i++) {                                     
			JSONObject jDev = jRecord.getJSONObject(i);
			deviceNames.add(jDev.getString("name"));
			deviceIds.add(jDev.getInt("id"));
		}
	}
	
	public void mousePressed(MouseEvent ev) {}
	public void mouseReleased(MouseEvent ev) {}
	public void mouseEntered(MouseEvent ev) {}
	public void mouseExited(MouseEvent ev) {}

	public void mouseClicked(MouseEvent ev) {
		// for getting the current JTabbedPane that is active
		String deviceId = deviceIds.get(cmbDevices.getSelectedIndex()).toString();
		int selectedIndex = tabbedPane.getSelectedIndex();
		if(selectedIndex == 0) {
			// Selected Row in the Non Registerd users in the first JTabbedPane called "Non Registred".
			int bRow = tableNon.getSelectedRow();
			if ((bRow != -1) && (ev.getClickCount() == 2)) {
				int index = tableNon.convertRowIndexToModel(bRow);
				EnrollDesk eDesk = new EnrollDesk(tNonRegModel.getTitles(), tNonRegModel.getRowValues(index), dev, deviceId);
				filter();		// Filter
			}
		} else if(selectedIndex == 1) {
			// Selected Row in the Registerd users in the second JTabbedPane called "Registred".
			int aRow = tableReg.getSelectedRow();
			if ((aRow != -1) && (ev.getClickCount() == 2)) {
				int index = tableReg.convertRowIndexToModel(aRow);
				UpdateDesk uDesk = new UpdateDesk(tModel.getTitles(), tModel.getRowValues(index), dev, deviceId);
				filter();		// Filter
			}
		} else if(selectedIndex == 2) {
			// Selected Row in the Active/Inactive users in the third JTabbedPane called "Activate / Inactivate Users ".
			int cRow = tableIN.getSelectedRow();
			if ((cRow != -1) && (ev.getClickCount() == 2)) {
				int index = tableIN.convertRowIndexToModel(cRow);
				Vector<String> rowData = tINModel.getRowValues(index);
				String user_id = rowData.get(2);
				String userResults = dev.userDetails(user_id);
				ActivateDesk aDesk = new ActivateDesk(tINModel.getTitles(), userResults, dev);
				filter();		// Filter
			}
		}
	}

	public void actionPerformed(ActionEvent ev) {
		String deviceId = deviceIds.get(cmbDevices.getSelectedIndex()).toString();
		if(ev.getActionCommand().equals("Verify")) {
			if(notVerifiying) {
				notVerifiying = false;
				JSONArray jEvents = eventLogs.getLogs(deviceId, 2);
				
				photoView.setIcon(null);
				if(jEvents.length() > 0) {
					JSONObject jLastEvent = jEvents.getJSONObject(jEvents.length() - 1);
System.out.println(jLastEvent.toString());
					if(jLastEvent.has("user")) {
						String userId = jLastEvent.getJSONObject("user").getString("user_id");
						String userName = jLastEvent.getJSONObject("user").getString("name");
						Map<String, String> std = verifyStudent.getStudent(db, userId);
						if(std.size() == 0) {
							statusMsg.setText("No students details in database for " + userName);
						} else {
							statusMsg.setText("Found : " + userName);
							for(String fieldKey : std.keySet())
								stdFields.get(fieldKey).setText(std.get(fieldKey));
						}
						
						if(imageMgr.ifExists("pp_" + userId + ".png")) {
							ImageIcon pImage = new ImageIcon(imageMgr.getImage("pp_" + userId + ".png"));
							Image pnewimg1 = pImage.getImage().getScaledInstance(330, 240, Image.SCALE_SMOOTH);
							ImageIcon imagePic = new ImageIcon(pnewimg1);
							photoView.setIcon(imagePic);
						}
					} else {
						statusMsg.setText("Student not registred");
					}
				} else {
					statusMsg.setText("Place finger on the device first");
				}
				notVerifiying = true;
			}
		} else if(ev.getActionCommand().equals("Logs")) {
			String startDate = txfs.get(0).getText() + "T00:00:00.00Z";
			String endDate = txfs.get(1).getText() + "T23:59:00.00Z";
			JSONArray rLog = eventLogs.getLogs(deviceId, startDate, endDate);
			logModel.refresh(eventLogs.getRecords(rLog));
			tableLog.repaint();
			tableLog.revalidate();
		} else if(ev.getActionCommand().equals("Search")) {
			int eventLogId = cmbEventNames.getSelectedIndex();
			if (eventLogId == 0) {
				JOptionPane.showMessageDialog(null, "You can't Search For None Log Events");
			} else {
				String startDate = txfs.get(0).getText() + "T00:00:00.00Z";
				String endDate = txfs.get(1).getText() + "T23:59:00.00Z";
				JSONArray rLog = eventLogs.getLogs(eventLogId, deviceId, startDate, endDate);
				logModel.refresh(eventLogs.getRecords(rLog));
				tableLog.repaint();
				tableLog.revalidate();
			}
		} else if(ev.getActionCommand().equals("Filter")) {
			filter();
		}
	}
	
	public void filter() {
		String whereSql = " AND " + fieldNames.get(fieldList.getSelectedIndex());
		if(filterData.getText().trim().equals("")) {
			whereSql = "";
		} else {
			whereSql += " " + filterList.getSelectedItem();
			if(filterList.getSelectedIndex() < 2) whereSql += " '%" + filterData.getText() + "%'";
			else whereSql += " '" + filterData.getText() + "'";
		}
		
		mySql = "SELECT s.studentid, s.studentname, s.telno, s.email, e.entity_id "
			+ "FROM studentdegreeview s INNER JOIN entitys e ON s.studentid = e.user_name "
			+ "INNER JOIN qstudents qs ON s.studentdegreeid = qs.studentdegreeid "
			+ "INNER JOIN quarters q ON qs.quarterid = q.quarterid "
			+ "WHERE (q.active = true) AND (s.telno is not null) AND (s.email is not null) "
			+ whereSql
			+ " LIMIT 200";

		refresh();
	}

	public void refresh() {
		enrolment.getStudents(db, mySql, fields);
		enrolment.usersList(dev, false);
		
		tNonRegModel.refresh(enrolment.getUnRegistred());
		tableNon.repaint();
		tableNon.revalidate();
		
		tModel.refresh(enrolment.getRegistred());
		tableReg.repaint();
		tableReg.revalidate();

		tINModel.refresh(enrolment.getInActive());
		tableIN.repaint();
		tableIN.revalidate();
	}
}
