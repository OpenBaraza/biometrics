package com.dewcis.biometrics;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.json.JSONArray;
import org.json.JSONObject;

public class searchLogDesk implements ActionListener {

	JFrame eFrame;
	JPanel buttonPanel;
	JDialog eDialog;
	
	public searchLogDesk(String eventLogView) {
		Vector<Vector<String>> rowData = new Vector<Vector<String>>();
		Vector<String> columnNames = new Vector<String>();
		columnNames.add("Date Time"); columnNames.add("Device ID"); columnNames.add("Device Name"); 
		columnNames.add("Entity ID"); columnNames.add("User Name");columnNames.add("Event");

		DefaultTableModel logModel = new DefaultTableModel(rowData,columnNames);
		JTable tableLog = new JTable(logModel);
		JScrollPane scrollPanereg = new JScrollPane(tableLog);

		JSONObject jLog = new JSONObject(eventLogView);
		JSONArray aLog = (JSONArray) jLog.get("records");

		for(int i=0; i<aLog.length(); i++){
			Vector<String> row = new Vector<String>();  
			row.add(""+aLog.getJSONObject(i).getString("datetime")+"");
			row.add(""+aLog.getJSONObject(i).getJSONObject("device").getInt("id")+"");
			row.add(""+aLog.getJSONObject(i).getJSONObject("device").getString("name")+"");
			if(aLog.getJSONObject(i).has("user")) {
				row.add(""+aLog.getJSONObject(i).getJSONObject("user").getInt("user_id")+"");
			} else {
				row.add(" ");
			} if(aLog.getJSONObject(i).has("user")&&aLog.getJSONObject(i).getJSONObject("user").has("name")) {
				row.add(""+aLog.getJSONObject(i).getJSONObject("user").getString("name")+"");
			} else {
				row.add(" ");
			}
			row.add(""+aLog.getJSONObject(i).getJSONObject("event_type").getString("description")+"");
			rowData.add(row);
			
			logModel.setDataVector(rowData, columnNames);
			logModel.fireTableDataChanged();
       	}

        eFrame = new JFrame("Search log");
		eDialog = new JDialog(eFrame , "Search log Details", true);
		eDialog.setSize(900, 700);
		eDialog.getContentPane().add(scrollPanereg, BorderLayout.CENTER);
		
System.out.println("BASE 4010");
		buttonPanel = new JPanel();
		JButton btn = new JButton("Close");
		btn.setBounds(5, 5, 120, 20);
		btn.addActionListener(this);
		buttonPanel.add(btn);
		eDialog.getContentPane().add(buttonPanel, BorderLayout.PAGE_END);
		
		eDialog.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent ev) {
		if(ev.getActionCommand().equals("Close")) {
			eDialog.dispose();
		}
	}
    
}
