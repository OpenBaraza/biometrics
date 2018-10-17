/**
 * @author      Dennis W. Gichangi <dennis@openbaraza.org>
 * @version     2011.0329
 * @since       1.6
 * website		www.openbaraza.org
 * The contents of this file are subject to the GNU Lesser General Public License
 * Version 3.0 ; you may use this file in compliance with the License.
 */
package com.dewcis.utils;


import java.util.Map;

import java.util.logging.Logger;
import java.util.Vector;
import java.util.logging.Level;

import javax.swing.table.AbstractTableModel;

public class DTableModel extends AbstractTableModel {
	Logger log = Logger.getLogger(DTableModel.class.getName());

	private Vector<Vector<String>> data;
	private Vector<String> titles;

	public DTableModel(Vector<String> fields, Vector<Vector<String>> newData) {
		titles = new Vector<String>(fields);
		data = new Vector<Vector<String>>(newData);
	}

	public DTableModel(Map<String, String> fields, Vector<Vector<String>> newData) {
		titles = new Vector<String>();
		data = new Vector<Vector<String>>(newData);
		
		// Add the titles
		for(String field : fields.keySet())
			titles.add(fields.get(field));
	}

	public int getColumnCount() {
		return titles.size();
	}

	public int getRowCount() {
		return data.size();
	}

	public String getColumnName(int col) {
		return titles.get(col);
	}

	public String getValueAt(int row, int col) {
		return data.get(row).get(col);
	}
	
	public Vector<String> getRowValues(int row) {
		return data.get(row);
	}
	
	public Vector<String> getTitles() {
		return titles;
	}

	public Class getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}

	public boolean isCellEditable(int row, int col) {
		return false;
	}
	
	public void refresh(Vector<Vector<String>> newData) { // Get all rows.
		data.clear();
		data = new Vector<Vector<String>>(newData);
		fireTableChanged(null); // Tell the listeners a new table has arrived.
	}

}
