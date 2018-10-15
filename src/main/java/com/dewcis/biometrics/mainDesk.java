package com.dewcis.biometrics;

import java.sql.Connection;

import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;

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
import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableModel;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.Month;
import java.time.Clock;
import java.time.Duration;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dewcis.utils.tableModel;

public class mainDesk extends JPanel implements MouseListener , ActionListener{
	Logger log = Logger.getLogger(mainDesk.class.getName());

	Connection db = null;
	String mySql = "";

	Enrolment enrolment = null;
	Configs cfgs = null;
	Device dev = null;
	EventLogs eventLogs = null;
	
	Vector<Vector<String>> rowData;
	Vector<String> columnNames;

	List<JButton> btns;
	List<JTextField> txfs;
	List<JComboBox> cmbs;
	
	Vector<String> deviceNames;
	Vector<Integer> deviceIds;
	Map<String, String> fields;
	List<String> fieldNames;
	
	JTabbedPane tabbedPane = new JTabbedPane();
	JTabbedPane searchPane = new JTabbedPane();
	
	JPanel nonRegPanel, regPanel, verifyPanel, acInPanel, logPanel, filterPanel;
	JPanel devicePanel, searchPanel, buttonPanel, studentPanel, statusPanel;
	JTable tableReg, tableNon, tableIN,	tableLog;
	JTextField filterData;
	JComboBox fieldList, filterList;
	tableModel tModel, tNonRegModel, tINModel;
	DefaultTableModel logModel;

	Vector<String> eventCodeName, logListcode;

	public mainDesk(Connection db) {
		super(new BorderLayout());
		this.db = db;
		
		deviceNames = new Vector<String>();
		deviceIds = new Vector<Integer>();

		// Non Registred user panel
		nonRegPanel = new JPanel(new BorderLayout());
		tabbedPane.addTab("Not Registred", nonRegPanel);

		//Registred user panel
		regPanel = new JPanel(new BorderLayout());
		tabbedPane.addTab("Registred", regPanel);

		//activate Deactivate user panel
		acInPanel = new JPanel(new BorderLayout());
		tabbedPane.addTab("Inactivate Users", acInPanel);

		//log user panel
		logPanel = new JPanel(new BorderLayout());
		tabbedPane.addTab("users Logs", logPanel);
		
		// Getting details from the config.txt from class base_url
		cfgs = new Configs(db);
		dev = new Device(cfgs.getConfigs(), null);
		getDeviceList();

		// Add students on table
		getStudents();

		eventLogs = new EventLogs(dev);
		eventLogs.getMonthLogs();
		rowData = eventLogs.getRowData();
		columnNames = eventLogs.getColumnNames();

		eventCodeName = eventLogs.getEventCodeName();
		logListcode = eventLogs.getLogListcode();
		
		logModel = new DefaultTableModel(rowData,columnNames);
		tableLog = new JTable(logModel);
		JScrollPane scrollPanereg = new JScrollPane(tableLog);
		logPanel.add(scrollPanereg, BorderLayout.CENTER);

		//Verify user panel
		btns = new ArrayList<JButton>();
		txfs = new ArrayList<JTextField>();
		cmbs = new ArrayList<JComboBox>();

		verifyPanel = new JPanel(null);
		
		// Device panel for selecting device
		devicePanel = new JPanel(null);
		addPanel(devicePanel, "Verify User", 5, 5, 800, 50);
		addDevice("Device ID ", 10, 20, 100, 20, 400);

		// Search panel for enter search parameters
		searchPanel = new JPanel(new GridLayout(0, 4));
		addSearch("Start Date ", "2018-10-04");
		addSearch("End Date ", "2018-10-04");
		addCombox("Event names ", eventCodeName);
		logPanel.add(searchPanel, BorderLayout.PAGE_END);

		// Butons panel
		buttonPanel = new JPanel(null);
		addPanel(buttonPanel, "Functions", 5, 200, 800, 70);
		addButton("Verify", 150, 20, 150, 25, true);
		addButton("Search", 350, 20, 150, 25, true);
		addButton("Logs", 550, 20, 150, 25, true);
		
		// Student details
		studentPanel = new JPanel(null);
		addPanel(studentPanel, "Student Details", 5, 280, 800, 300);
		
		// Status log panel
		statusPanel = new JPanel(null);
		addPanel(statusPanel, "Status", 5, 590, 800, 100);


		tabbedPane.addTab("Verify User / Search logs", verifyPanel);
		super.add(tabbedPane, BorderLayout.CENTER);
		
		filterData = new JTextField(25);
		filterData.setActionCommand("Filter");
		filterData.addActionListener(this);
		String[] filterstr = {"ILIKE", "LIKE", "=", ">", "<", "<=", ">="};	
		fieldList = new JComboBox<String>(tNonRegModel.getTitles());
		filterList = new JComboBox<String>(filterstr);
		filterPanel = new JPanel(new FlowLayout());
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
		tNonRegModel = new tableModel(fields, enrolment.getUnRegistred());
		tableNon = new JTable(tNonRegModel);
		tableNon.addMouseListener(this);
		JScrollPane scrollPane = new JScrollPane(tableNon);
		nonRegPanel.add(scrollPane, BorderLayout.CENTER);
		
		//Creating table for Registered students
		tModel = new tableModel(fields, enrolment.getRegistred());
		tableReg = new JTable(tModel);
		tableReg.addMouseListener(this);
		JScrollPane scrollPanereg = new JScrollPane(tableReg);
		regPanel.add(scrollPanereg, BorderLayout.CENTER);

		//Creating table for Inactive students
		tINModel = new tableModel(fields, enrolment.getInActive());
		tableIN = new JTable(tINModel);
		tableIN.addMouseListener(this);
		JScrollPane scrollPaneInAc = new JScrollPane(tableIN);
		acInPanel.add(scrollPaneInAc, BorderLayout.CENTER);
	}
	
	public void addPanel(JPanel nPanel, String btTitle, int x, int y, int w, int h) {
		nPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), btTitle));
		nPanel.setBounds(x, y, w, h);
		verifyPanel.add(nPanel);
	}

	public void addButton(String btTitle, int x, int y, int w, int h, boolean enabled) {
		JButton btn = new JButton(btTitle);
		btn.setBounds(x, y, w, h);
		btn.addActionListener(this);
		btn.setEnabled(enabled);
		buttonPanel.add(btn);
		btns.add(btn);
	}

	public void addDevice(String fieldTitle, int x, int y, int w, int h, int dw) {
		JLabel lbTitle = new JLabel(fieldTitle + " : ");
		lbTitle.setBounds(x, y, w, h);
		devicePanel.add(lbTitle);
				
		JComboBox cmbDevice = new JComboBox(deviceNames);
		cmbDevice.setBounds(x + w + 5, y, dw, h);
		devicePanel.add(cmbDevice);
		cmbs.add(cmbDevice);
	}

	public void addSearch(String fieldTitle, String fieldValue) {
		JLabel lbTitle = new JLabel(fieldTitle + " : ");
		searchPanel.add(lbTitle);
		
		JTextField tfDevice = new JTextField();
		tfDevice.setText(fieldValue);
		searchPanel.add(tfDevice);
		txfs.add(tfDevice);
	}

	public void addCombox(String fieldTitle, Vector<String> fieldValue) {
		JLabel lbTitle = new JLabel(fieldTitle + " : ");
		searchPanel.add(lbTitle);
		
		JComboBox cmbValues = new JComboBox(fieldValue);
		searchPanel.add(cmbValues);
		cmbs.add(cmbValues);
	}
	
	public void getDeviceList() {	
		JSONObject jResp = new JSONObject(dev.deviceList());
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
		String deviceId = deviceIds.get(cmbs.get(0).getSelectedIndex()).toString();
		int selectedIndex = tabbedPane.getSelectedIndex();
		if(selectedIndex == 0) {
			// Selected Row in the Non Registerd users in the first JTabbedPane called "Non Registred".
			int bRow = tableNon.getSelectedRow();
			if ((bRow != -1) && (ev.getClickCount() == 2)) {
				int index = tableNon.convertRowIndexToModel(bRow);
				enrollDesk eDesk = new enrollDesk(tNonRegModel.getTitles(), tNonRegModel.getRowValues(index), dev, deviceId);
				filter();		// Filter
			}
		} else if(selectedIndex == 1) {
			// Selected Row in the Registerd users in the second JTabbedPane called "Registred".
			int aRow = tableReg.getSelectedRow();
			if ((aRow != -1) && (ev.getClickCount() == 2)) {
				int index = tableReg.convertRowIndexToModel(aRow);
				updateDesk uDesk = new updateDesk(tModel.getTitles(), tModel.getRowValues(index), dev, deviceId);
			}
		} else if(selectedIndex == 2) {
			// Selected Row in the Active/Inactive users in the third JTabbedPane called "Activate / Inactivate Users ".
			int cRow = tableIN.getSelectedRow();
			if ((cRow != -1) && (ev.getClickCount() == 2)) {
				int index = tableIN.convertRowIndexToModel(cRow);
				Vector<String> rowData = tINModel.getRowValues(index);
				String user_id = rowData.get(2);
				String userResults = dev.userDetails(user_id);
				activateDesk aDesk = new activateDesk(tINModel.getTitles(), userResults, dev);
			}
		}
	}

	public void actionPerformed(ActionEvent ev) {
		if(ev.getActionCommand().equals("Verify")) {
			String deviceId = deviceIds.get(cmbs.get(0).getSelectedIndex()).toString();
System.out.println("Device ID : " + deviceId);

			JSONArray jEvents = eventLogs.getLogs(deviceId, 60*24*3);
System.out.println(jEvents.length());
			if(jEvents.length() > 0) {
				JSONObject jLastEvent = jEvents.getJSONObject(jEvents.length() - 1);
System.out.println(jLastEvent.toString());
			}
		} else if(ev.getActionCommand().equals("Logs")) {
			String deviceId = deviceIds.get(cmbs.get(0).getSelectedIndex()).toString();
System.out.println("Device ID : " + deviceId);

			JSONArray jEvents = eventLogs.getLogs(deviceId, 30);
		} else if(ev.getActionCommand().equals("Search")) {
			String eventLOG = null;
			int eventLogName = cmbs.get(1).getSelectedIndex();
			String deviceId = deviceIds.get(cmbs.get(0).getSelectedIndex()).toString();
			
			if(0 != eventLogName) {
				eventLOG = logListcode.get(eventLogName);
				JSONArray jCode = new JSONArray();
				for(String logCode : logListcode) jCode.put(logCode);

				JSONArray jEvent = new JSONArray();
				JSONObject jEventDetails = new JSONObject();
				jEventDetails.put("device_id", deviceId);
				jEventDetails.put("end_datetime", txfs.get(1).getText()+"T23:59:00.00Z");
				jEventDetails.put("start_datetime", txfs.get(0).getText()+"T00:00:00.00Z");
				jEvent.put(jEventDetails);

				JSONObject jSearchEvent = new JSONObject();
				jSearchEvent.put("device_query_list", jEvent);
				jSearchEvent.put("event_type_code_list", jCode);
				jSearchEvent.put("limit", 0);
				jSearchEvent.put("offset", 0);
System.out.println(jSearchEvent.toString());

				String eventlogView = dev.searchLogEvent(jSearchEvent);
System.out.println(eventlogView);
				searchLogDesk srch = new searchLogDesk(eventlogView);
			} else if (0==eventLogName) {
				JOptionPane.showMessageDialog(null, "You can't Search For None Log Events");
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

		//Creating table for Inactive students
		tINModel = new tableModel(fields, enrolment.getInActive());
		tableIN.repaint();
		tableIN.revalidate();
	}
}
