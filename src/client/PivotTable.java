package client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PivotTable {	
	String column1,column2;
	int leadCount = 0;
	public List<Row> rows = new ArrayList<Row>();
	public PivotTable(String column1,String column2) {
		this.column1 = column1;
		this.column2 = column2;
	}
	public void incrementLeadCount() {
		this.leadCount++;
	}
	public int getLeadCount() {
		return this.leadCount;
	}
	public void addRow(String name) {
		rows.add(new Row(name));
	}
	public Row getRow(String name) {
		for(Row row: this.rows) {
			if(row.report.getName().equalsIgnoreCase(name))
				return row;
		}
		return null;
	}
	public boolean containsRow(String name) {
		for(Row row: this.rows) {
			if(row.report.getName().equalsIgnoreCase(name))
				return true;
		}
		return false;
	}
	public class Row {
		String name;
		public AgentReport report;
		public HashMap<String, AgentReport> rows = new HashMap<String, AgentReport>();
		public Row(String name) {
			this.name = name;
			report = new AgentReport(name);
		}
		public void addRow(String name) {
			rows.put(name,new AgentReport(name));
		}
		public AgentReport getRow(String name) {
			return rows.get(name);
		}
		public boolean containsRow(String name) {
			return rows.containsKey(name);
		}
	}
}
