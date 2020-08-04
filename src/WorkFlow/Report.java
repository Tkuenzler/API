package WorkFlow;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.json.JSONException;
import org.json.JSONObject;

import client.AgentReport;
import client.DatabaseClient;
import client.InfoDatabase;
import client.PivotTable;
import client.ReportClient;
import client.RoadMapClient;

@Path("Report")
public class Report {
	NumberFormat df = NumberFormat.getPercentInstance();
		
	@Path("GetRequalifys")
	@GET()
	@Produces(MediaType.TEXT_HTML)
	public String GetRequalifys(@QueryParam("to") String to,
			@QueryParam("from") String from)  {
		ReportClient client = new ReportClient("Info_Table");
		String response = client.GetRequalify(to, from);
		client.close();
		return response;
	}
	@Path("GetChasePercent")
	@GET()
	@Produces(MediaType.TEXT_HTML)
	public String GetChasePercent(@QueryParam("database") String database,
			@QueryParam("roadmap") String roadmap)  {
		DatabaseClient client = new DatabaseClient(database);
		int live_refax_count = client.GetLiveCount(roadmap)+client.GetRefaxCount(roadmap);
		int blank = client.GetTotalBlank(roadmap);
		client.close();
		StringBuilder sb = new StringBuilder();
		sb.append("<table border='1'>");
		sb.append("<tr bgcolor='#d3d3d3'>");
		sb.append("<td>Ready</td>");
		sb.append("<td>Blank</td>");
		sb.append("<td>Pending %</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td>"+live_refax_count+"</td>");
		sb.append("<td>"+blank+"</td>");
		sb.append("<td>"+df.format((double)live_refax_count/(double)blank)+"</td>");
		sb.append("</tr>");
		sb.append("</table>");
		return sb.toString();
	}
	
	@Path("FaxByPharmacy")
	@GET()
	@Produces(MediaType.TEXT_HTML)
	public String GetFaxesSentByPharmacy(@QueryParam("database") String database,
			@QueryParam("to") String to,
			@QueryParam("from") String from) {
		ReportClient client = new ReportClient(database);
		HashMap<String, Integer> list = client.GetFaxesSentByPharmacy(to, from);
		client.close();
		StringBuilder sb = new StringBuilder();
		sb.append("<table border='1'>");
		sb.append("<tr bgcolor='#d3d3d3'>");
		sb.append("<td>Pharmacy</td>");
		sb.append("<td>Count</td>");
		sb.append("</tr>");
		Iterator<Entry<String, Integer>> it = list.entrySet().iterator();
		int total = 0;
		while (it.hasNext()) {
			Map.Entry<String, Integer> pair = (Map.Entry<String, Integer>)it.next();
			String key = pair.getKey();
			sb.append("<tr>");
			sb.append("<td>"+key+"</td>");
			sb.append("<td>"+list.get(key)+"</td");
			sb.append("</tr>");
			total += list.get(key);
		}
		sb.append("<tr>");
		sb.append("<td>Total</td>");
		sb.append("<td>"+total+"</td");
		sb.append("</tr>");
		sb.append("</table>");
		return sb.toString();
	}
	
	
	@Path("GetColumns")
	@GET()
	@Produces(MediaType.TEXT_HTML)
	public String GetColumns(@QueryParam("table") String table,
							@QueryParam("database") String database) throws JSONException {
		ReportClient client = new ReportClient(database);
		JSONObject obj = client.GetColumns(table);
		client.close();
		return obj.toString();
	}
	
	@Path("GetCallCenters")
	@GET()
	@Produces(MediaType.TEXT_HTML)
	public String GetCallCenters(@QueryParam("database") String database) throws JSONException {
		InfoDatabase client = new InfoDatabase();
		JSONObject obj = client.GetCallCenters(database);
		client.close();
		return obj.toString();
	}
	
	@Path("GetAgents")
	@GET()
	@Produces(MediaType.TEXT_HTML)
	public String GetAgents(@QueryParam("call_center") String call_center,
							@QueryParam("database") String database) throws JSONException {
		if(call_center==null)
			call_center = "";
		String params = null;
		if(!call_center.equalsIgnoreCase(""))
			params = " AND `CALL_CENTER` = '"+call_center+"'";
		else
			params = "";
		InfoDatabase client = new InfoDatabase();
		JSONObject obj = client.GetAgents(params,database);
		client.close();
		return obj.toString();
	}
	
	@Path("GetPharmacies")
	@GET()
	@Produces(MediaType.TEXT_HTML)
	public String GetColumns(@QueryParam("roadmap") String roadmap) throws JSONException {
		RoadMapClient client = new RoadMapClient(roadmap);
		JSONObject obj = client.getPharmaciesAsJSON();
		client.close();
		return obj.toString();
	}
	
	@Path("GetWrongDoctors")
	@GET()
	@Produces(MediaType.TEXT_HTML)
	public String GetWrongDoctors(
			@QueryParam("time") String time,
			@QueryParam("back") String back,
			@QueryParam("from") String from,
			@QueryParam("to") String to,
			@QueryParam("pharmacy") String pharmacy,
			@QueryParam("call_center") String call_center,
			@QueryParam("agent") String agent,
			@QueryParam("database") String database) {
		String leadsTimeFrame = null;
		if(from==null || to==null) {
			if(back==null)
				back = "0";
			if(time==null)
				time = "Day";
			leadsTimeFrame = CreateTimeFrame(time,back);
		}
		if(from!=null && to!=null) {
			leadsTimeFrame =  "(`DATE_ADDED` >= '"+from+"' AND `DATE_ADDED` <= '"+to+"')";
		}
		ReportClient client = new ReportClient(database);
		String response = client.GetWrongDoctors(leadsTimeFrame, call_center);
		client.close();
		return response;
	}
	@Path("FaxDisposition")
	@GET()
	@Produces(MediaType.TEXT_HTML)
	public String GetAgentReport(@QueryParam("from") String from,
								@QueryParam("to") String to,
								@QueryParam("column") String column,
								@QueryParam("pharmacy") String pharmacy,
								@QueryParam("call_center") String call_center,
								@QueryParam("agent") String agent,
								@QueryParam("column_param") String column_param,
								@QueryParam("value") String value,
								@QueryParam("database") String database) {
		ReportClient client = new ReportClient(database);
		if(!client.validColumn(column,"Leads")) {
			client.close();
			return "INVALID COLUMN";
		}
		String params = CreateQueryParams(pharmacy,call_center,agent,column_param,value);
		HashMap<String, AgentReport> list = client.GetFaxDispositionTable(from,to,column,params);
		
		df.setMinimumFractionDigits(3);
		StringBuilder sb = new StringBuilder();
		sb.append("<table border='1'>");
		sb.append("<tr bgcolor='#d3d3d3'>");
		sb.append("<td>"+column+"</td>");
		sb.append("<td>Approved</td>");
		sb.append("<td>Topical Script Approval</td>");
		sb.append("<td>Oral Script Approval</td>");
		sb.append("<td>Denied</td>");
		sb.append("<td>Needs To Be Seen</td>");
		sb.append("<td>Need PCP</td>");
		sb.append("<td>Wrong Doctor</td>");
		sb.append("<td>Not Interested</td>");
		sb.append("<td>Escribe</td>");
		sb.append("<td>Deceased</td>");
		sb.append("<td>Total</td>");
		sb.append("<td>Quality %</td>");
		sb.append("</tr>");
		Iterator<Entry<String, AgentReport>> it = list.entrySet().iterator();
		AgentReport total = new AgentReport("Total");
		while (it.hasNext()) {
		    Map.Entry<String, AgentReport> pair = (Map.Entry<String, AgentReport>)it.next();
			String key = pair.getKey();
		    AgentReport report = list.get(key);
		    sb.append("<tr>");
		    sb.append("<td>"+report.getName()+"</td>");
		    sb.append("<td>"+report.getApproved()+"</td>");
		    total.setApproved(total.getApproved()+report.getApproved());
		    total.setTopicalScriptApproval(total.getTopicalScriptApproval()+report.getTopicalScriptApproval());
		    total.setOralScriptApproval(total.getOralScriptApproval()+report.getOralScriptApproval());
		    total.setDenied(total.getDenied()+report.getDenied());
		    total.setNtbs(total.getNtbs()+report.getNtbs());
		    total.setNeedPcp(total.getNeedPcp()+report.getNeedPcp());
		    total.setWrongDoctor(total.getWrongDoctor()+report.getWrongDoctor());
		    total.setNotInterested(total.getNotIntrested()+report.getNotIntrested());
		    total.setEscribe(total.getEscribe()+report.getEscribe());
		    total.setDeceased(total.getDeceased()+report.getDeceased());
		    total.setLeadCount(total.getLeadCount()+report.getLeadCount());
		    sb.append("<td>"+report.getTopicalScriptApproval()+"</td>");
		    sb.append("<td>"+report.getOralScriptApproval()+"</td>");
		    sb.append("<td>"+report.getDenied()+"</td>");
		    sb.append("<td>"+report.getNtbs()+"</td>");
		    sb.append("<td>"+report.getNeedPcp()+"</td>");  
		    sb.append("<td>"+report.getWrongDoctor()+"</td>");		    
		    sb.append("<td>"+report.getNotIntrested()+"</td>");	    
		    sb.append("<td>"+report.getEscribe()+"</td>");	    
		    sb.append("<td>"+report.getDeceased()+"</td>");
		    sb.append("<td>"+report.getLeadCount()+"</td>");
		    int approved = report.getApproved()+report.getTopicalScriptApproval();
		    int recieved = report.getLeadCount();
		    if(approved==0)
		    	sb.append("<td>"+df.format((double)0)+"</td>");
		    else
		    	sb.append("<td>"+df.format((double)approved/recieved)+"</td>");
		    sb.append("</tr>");
		    
	    }
		sb.append("<tr>");
	    sb.append("<td>"+total.getName()+"</td>");
	    sb.append("<td>"+total.getApproved()+"</td>");
	    sb.append("<td>"+total.getTopicalScriptApproval()+"</td>");
	    sb.append("<td>"+total.getOralScriptApproval()+"</td>");
	    sb.append("<td>"+total.getDenied()+"</td>");
	    sb.append("<td>"+total.getNtbs()+"</td>");
	    sb.append("<td>"+total.getNeedPcp()+"</td>");
	    sb.append("<td>"+total.getWrongDoctor()+"</td>");
	    sb.append("<td>"+total.getNotIntrested()+"</td>");
	    sb.append("<td>"+total.getEscribe()+"</td>");
	    sb.append("<td>"+total.getDeceased()+"</td>");
	    sb.append("<td>"+total.getLeadCount()+"</td>");
	    int approved = total.getApproved()+total.getTopicalScriptApproval();
	    int recieved = total.getLeadCount();
	    if(approved==0)
	    	sb.append("<td>"+df.format((double)0)+"</td>");
	    else
	    	sb.append("<td>"+df.format((double)approved/recieved)+"</td>");
	    sb.append("</tr>");
		sb.append("</table>");
		client.close();
		return sb.toString();
	}
	
	@Path("LeadCount")
	@GET()
	@Produces(MediaType.TEXT_HTML)
	public String GetLeadCount(@QueryParam("from") String from,
								@QueryParam("to") String to,
								@QueryParam("column") String column,
								@QueryParam("table") String table,
								@QueryParam("pharmacy") String pharmacy,
								@QueryParam("call_center") String call_center,
								@QueryParam("agent") String agent,
								@QueryParam("column_param") String column_param,
								@QueryParam("value") String value,
								@QueryParam("database") String database) {
		if(column==null)
			column = "TYPE";
		if(table==null)
			table = "Leads";
		ReportClient client = new ReportClient(database);
		if(!client.validColumn(column,table)) {
			client.close();
			return "INVALID COLUMN";
		}
		String params = CreateQueryParams(pharmacy,call_center,agent,column_param,value);
		Set<Entry<String, AgentReport>> list = client.GetLeadCount(from,to,column,table,params);
		int totalLeads = client.GetLeadCount(from,to,table,params);
		client.close();
		df.setMinimumFractionDigits(3);
		StringBuilder sb = new StringBuilder();
		sb.append("<table border='1'>");
		sb.append("<tr bgcolor='#d3d3d3'>");
		sb.append("<td>"+column+"</td>");
		sb.append("<td>Count</td>");
		sb.append("<td>Percent</td>");
		sb.append("</tr>");
		for(Entry<String, AgentReport> entry : list) {
		    AgentReport report = entry.getValue();
		    sb.append("<tr>");
		    sb.append("<td>"+report.getName()+"</td>");
		    sb.append("<td>"+report.getLeadCount()+"</td>");
		    sb.append("<td>"+df.format((double)report.getLeadCount()/totalLeads)+"</td>");
		    sb.append("</tr>");
		}
		sb.append("<tr>");
		sb.append("<td>Total</td>");
	    sb.append("<td>"+totalLeads+"</td>");
	    sb.append("<td>"+df.format((double)totalLeads/totalLeads)+"</td>");
		sb.append("</tr>");
		return sb.toString();
	}
	
	@Path("PivotTable")
	@GET()
	@Produces(MediaType.TEXT_HTML)
	public String TwoXTwo(@QueryParam("from") String to,
							@QueryParam("to") String from,
							@QueryParam("column1") String column1,
							@QueryParam("column2") String column2,
							@QueryParam("table") String table,
							@QueryParam("pharmacy") String pharmacy,
							@QueryParam("call_center") String call_center,
							@QueryParam("agent") String agent,
							@QueryParam("column_param") String column_param,
							@QueryParam("value") String value,
							@QueryParam("database") String database) {
		ReportClient client = new ReportClient(database);
		if(!client.validColumn(column1,table) || !client.validColumn(column2,table)) {
			client.close();
			return "INVALID COLUMN";
		}
		String params = CreateQueryParams(pharmacy,call_center,agent,column_param,value);
		PivotTable pivot = client.GetPivotTable(from,to,column1,column2,table,params);
		client.close();
		df.setMinimumFractionDigits(3);
		StringBuilder sb = new StringBuilder();
		sb.append("<table border='1'>");
		sb.append("<tr bgcolor='#d3d3d3'>");
		sb.append("<td>"+column1+"</td>");
		sb.append("<td>"+column2+"</td>");
		sb.append("<td>Count</td>");
		sb.append("<td>Percent</td>");
		sb.append("</tr>");
		for(PivotTable.Row row: pivot.rows) {
			AgentReport report = row.report;
		    sb.append("<tr>");
		    sb.append("<td>"+report.getName()+"</td>");
		    sb.append("<td></td>");
		    sb.append("<td>"+report.getLeadCount()+"</td>");
		    sb.append("<td>"+df.format((double)report.getLeadCount()/pivot.getLeadCount())+"</td>");
		    sb.append("</tr>");  
		    Iterator<Entry<String, AgentReport>> it = row.rows.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, AgentReport> pair = (Map.Entry<String, AgentReport>)it.next();
				AgentReport subRow = pair.getValue();
				sb.append("<tr>");
				sb.append("<td>     </td>");
				sb.append("<td>"+subRow.getName()+"</td>");
				sb.append("<td>"+subRow.getLeadCount()+"</td>");
				sb.append("<td>"+df.format((double)subRow.getLeadCount()/report.getLeadCount())+"</td>");
				sb.append("</tr>");   
			}
		}
		return sb.toString();
	}
	
	@Path("EPH")
	@GET()
	@Produces(MediaType.TEXT_HTML)
	public String GetEPH(@QueryParam("call_center")String callCenter,
						@QueryParam("database")String database) {
		InfoDatabase client = new InfoDatabase();
		String response = client.GetEPH(callCenter,database);
		client.close();
		return response;
	}
	
	@Path("GetConversionPercent")
	@GET()
	@Produces(MediaType.TEXT_HTML)
	public String GetConversionPercent(@QueryParam("call_center")String callCenter,
										@QueryParam("database")String database) {
		InfoDatabase client = new InfoDatabase();
		String response = client.GetDispositionPercent(callCenter,database);
		client.close();
		return response;
	}
	
	@Path("LoadFailedTransfers") 
	@GET
	@Produces(MediaType.TEXT_HTML) 
	public String GetFailedTransfers(@QueryParam("from") String from,
								@QueryParam("to") String to,
								@QueryParam("pharmacy") String pharmacy,
								@QueryParam("call_center") String call_center,
								@QueryParam("agent") String agent,
								@QueryParam("column_param") String column_param,
								@QueryParam("value") String value,
								@QueryParam("database") String database) {
		ReportClient client = new ReportClient(database);
		String params = CreateQueryParams(pharmacy,call_center,agent,column_param,value);
		String response = client.GetFailed(from,to,params);
		client.close();
		return response;
	}
	
	
	@Path("LoadPendingTransfers") 
	@GET
	@Produces(MediaType.TEXT_HTML) 
	public String GetPendingTransfers(@QueryParam("from") String from,
								@QueryParam("to") String to,
								@QueryParam("pharmacy") String pharmacy,
								@QueryParam("call_center") String call_center,
								@QueryParam("agent") String agent,
								@QueryParam("column_param") String column_param,
								@QueryParam("value") String value,
								@QueryParam("database") String database) {
		ReportClient client = new ReportClient(database);
		String params = CreateQueryParams(pharmacy,call_center,agent,column_param,value);
		String response = client.GetPending(from,to,params);
		client.close();
		return response;
	}
	
	@Path("GetDrChaseCount")
	@GET()
	@Produces(MediaType.TEXT_HTML)
	public String DrChaseCount(@QueryParam("database") String database,
			@QueryParam("roadmap") String roadmap) {
		DatabaseClient client = new DatabaseClient(database);
		int live = client.GetLiveLeadCount("Leads", roadmap,null);
		int no_answer = client.NoAnswerCount("Leads", roadmap,null);
		int refax = client.GetReFaxCount("Leads",roadmap,null);
		int total = live+refax+no_answer;
		client.close();
		StringBuilder sb = new StringBuilder();
		sb.append("<table border='1'>");
		sb.append("<tr bgcolor='#d3d3d3'>");
		sb.append("<td>Record Type</td>");
		sb.append("<td>Count</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td>Live Leads</td>");
		sb.append("<td>"+live+"</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td>No Answer Leads</td>");
		sb.append("<td>"+no_answer+"</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td>Refax Leads</td>");
		sb.append("<td>"+refax+"</td>");
		sb.append("</tr>");
		sb.append("<td>Total Leads</td>");
		sb.append("<td>"+total+"</td>");
		sb.append("</tr>");
		sb.append("</table>");
		return sb.toString();
	}
	
	@Path("GetDrChaseCountByPharmacy")
	@GET()
	@Produces(MediaType.TEXT_HTML)
	public String DrChaseCountByPharmacy(@QueryParam("database") String database,
			@QueryParam("roadmap") String roadmap) {
		DatabaseClient client = new DatabaseClient(database);
		RoadMapClient map = new RoadMapClient(roadmap);
		String[] pharmacies = map.getPharmacyForDrChase();
		StringBuilder sb = new StringBuilder();
		sb.append("<table border='1'>");
		sb.append("<tr bgcolor='#d3d3d3'>");
		sb.append("<td>Pharmacy</td>");
		sb.append("<td>Live</td>");
		sb.append("<td>No Answer</td>");
		sb.append("<td>Re Fax</td>");
		sb.append("<td>Total</td>");
		sb.append("</tr>");
		int total_live = 0, total_no_answer = 0,total_refax = 0, total_all = 0;
		for(String pharmacy: pharmacies) {
			int live = client.GetLiveLeadCount("Leads", roadmap,pharmacy);
			int no_answer = client.NoAnswerCount("Leads", roadmap,pharmacy);
			int refax = client.GetReFaxCount("Leads",roadmap,pharmacy);
			int total = live+refax+no_answer;
			total_live += live;
			total_no_answer += no_answer;
			total_refax += refax;
			total_all += total;
			sb.append("<tr>");
			sb.append("<td>"+pharmacy+"</td>");
			sb.append("<td>"+live+"</td>");
			sb.append("<td>"+no_answer+"</td>");
			sb.append("<td>"+refax+"</td>");
			sb.append("<td>"+total+"</td>");
			sb.append("</tr>");
		}
		sb.append("<tr>");
		sb.append("<td>ALL</td>");
		sb.append("<td>"+total_live+"</td>");
		sb.append("<td>"+total_no_answer+"</td>");
		sb.append("<td>"+total_refax+"</td>");
		sb.append("<td>"+total_all+"</td>");
		sb.append("</tr>");
		sb.append("</table>");
		map.close();
		client.close();
		return sb.toString();
	}
	
	@Path("LoadSuccessfulTransfers") 
	@GET
	@Produces(MediaType.TEXT_HTML) 
	public String GetSuccessfulTransfers(@QueryParam("from") String from,
								@QueryParam("to") String to,
								@QueryParam("pharmacy") String pharmacy,
								@QueryParam("call_center") String call_center,
								@QueryParam("agent") String agent,
								@QueryParam("column_param") String column_param,
								@QueryParam("value") String value,
								@QueryParam("database") String database) {
		ReportClient client = new ReportClient(database);
		String params = CreateQueryParams(pharmacy,call_center,agent,column_param,value);
		String response = client.GetSuccessfulTransfers(from,to,params);
		client.close();
		return response;
	}

	@Path("GetFaxesSent")
	@GET
	@Produces(MediaType.TEXT_HTML) 
	public String GetFaxesSent(@QueryParam("from") String from,
								@QueryParam("to") String to,
								@QueryParam("database") String database) {
		ReportClient client = new ReportClient(database);
		HashMap<String, AgentReport> agents = client.GetFaxesSent(from,to);
		StringBuilder sb = new StringBuilder();
		sb.append("<table border='1'>");
		sb.append("<tr bgcolor='#d3d3d3'>");
		sb.append("<td>Agent Name</td>");
		sb.append("<td>Faxes Sent</td>");
		sb.append("</tr>");
		Iterator<Entry<String, AgentReport>> it = agents.entrySet().iterator();
		int total = 0;
		while (it.hasNext()) {
			Map.Entry<String, AgentReport> pair = (Entry<String, AgentReport>)it.next();
		    String key = pair.getKey();
		    AgentReport agent = agents.get(key);
		    sb.append("<tr>");
		    sb.append("<td>"+agent.getName()+"</td>");
			sb.append("<td>"+agent.getLeadCount()+"</td>");
		    sb.append("</tr>");
		    total += agent.getLeadCount();
		}
		client.close();
		sb.append("<tr>");
	    sb.append("<td>Total</td>");
		sb.append("<td>"+total+"</td>");
	    sb.append("</tr>");
		sb.append("</table>");
		return sb.toString();
	}
	
	@Path("AgentReport") 
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String GetDailyReport(
								@QueryParam("time") String time,
								@QueryParam("back") String back,
								@QueryParam("from") String from,
								@QueryParam("to") String to,
								@QueryParam("call_center") String call_center,
								@QueryParam("database") String database) {
		String leadsTimeFrame = null;
		String telmedTimeFrame = null;
		if(from==null || to==null) {
			if(back==null)
				back = "0";
			if(time==null)
				time = "Day";
			leadsTimeFrame = CreateTimeFrame(time,back);
			telmedTimeFrame = CreateTelmedTimeFrame(time,back);
		}
		if(from!=null && to!=null) {
			leadsTimeFrame =  "(`DATE_ADDED` >= '"+from+"' AND `DATE_ADDED` <= '"+to+"')";
			telmedTimeFrame =  "(`DATE_MODIFIED` >= '"+from+"' AND `DATE_MODIFIED` <= '"+to+"')";
		}
		if(call_center==null)
			return "MUST SUPPLY CALL CENTER";
		InfoDatabase info = new InfoDatabase();	
		HashMap<String, AgentReport> agents = info.GetAgentsFromCallCenter(call_center,database);
		info.close();
		ReportClient client = new ReportClient(database);
		Iterator<Entry<String, AgentReport>> it = agents.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, AgentReport> pair = (Entry<String, AgentReport>)it.next();
		    String key = pair.getKey();
		    AgentReport agent = agents.get(key);
		    agent.setLeadCount(client.GetLeadsByCountByAgent(agent.getName(), leadsTimeFrame));
		    agent.setTelmed(client.GetSuccesfulTelmedByAgent(agent.getName(), telmedTimeFrame));
		    agent.setPendingTelmed(client.GetPendingTelmedByAgent(agent.getName(), telmedTimeFrame));
		    agent.setFailedTelmed(client.GetFailedTelmedByAgent(agent.getName(), telmedTimeFrame));
		    agent.setWrongDoctor(client.GetLeadsByFaxDisposition(agent.getName(), leadsTimeFrame, Fax.FaxStatus.WRONG_DOCTOR));
		    agent.setDMETelmed(client.GetDMETelmedByAgent(agent.getName(), telmedTimeFrame));
		}
		int totalLeads = client.GetLeadsByCountByCallCenter(call_center, leadsTimeFrame);
		int totalTelmed = client.GetSuccesfulTelmedByCallCenter(call_center, telmedTimeFrame);
		int totalPendingTelmed = client.GetPendingTelmedByCallCenter(call_center, telmedTimeFrame); 
		int totalFailedTelmed = client.GetFailedTelmedByCallCenter(call_center, telmedTimeFrame); 
		int totalDME = client.GetDMETelmeds(call_center, telmedTimeFrame); 
		client.close();
		return PrintDataLeadCount(agents,call_center,totalLeads,totalTelmed,totalPendingTelmed,totalFailedTelmed,totalDME);
	}

	/*
	 * Print Report to HTML/TXT
	 */
	private String PrintDataLeadCount(HashMap<String, AgentReport> agents,String call_center,int totalLeads,int totalTelmed,int totalPendingTelmed,int totalFailedTelmed,int totalDME) {	
		StringBuilder builder = new StringBuilder();
		builder.append("<table border='1'>");
		builder.append("<tr bgcolor='#d3d3d3'>");
		builder.append("<td>Agent Name</td>");
		builder.append("<td>Lead Count</td>");
		builder.append("</tr>");
		/*
		 * LEAD GENERATION
		 */
		Iterator<Entry<String, AgentReport>> it = agents.entrySet().iterator();
		int leads = 0;
		while (it.hasNext()) {
			Map.Entry<String, AgentReport> pair = (Entry<String, AgentReport>)it.next();
		    String key = pair.getKey();
		    AgentReport agent = agents.get(key);
		    builder.append("<tr>");
		    builder.append("<td>"+agent.getName()+"</td>");
		    builder.append("<td>"+agent.getLeadCount()+"</td>");
		    leads += agent.getLeadCount();
		    builder.append("</tr>");
		}
		builder.append("<tr>");
		builder.append("<td>Other</td>");
	    builder.append("<td>"+(totalLeads-leads)+"</td>");
		builder.append("</tr>");
		builder.append("<tr>");
		builder.append("<td>Total Enrollments</td>");
	    builder.append("<td>"+totalLeads+"</td>");
		builder.append("</tr>");
		
		
		
		/*
		 * SUCCESFUL TELMED
		 */
		builder.append("<tr bgcolor='#d3d3d3'>");
		builder.append("<td>Agent Name</td>");
		builder.append("<td>Successful Telmed</td>");
		builder.append("</tr>");
		int telmed = 0;
		it = agents.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, AgentReport> pair = (Entry<String, AgentReport>)it.next();
		    String key = pair.getKey();
		    AgentReport agent = agents.get(key);	    
		    if(agent.getTelmed()>0) {
		    	builder.append("<tr>");
			    builder.append("<td>"+agent.getName()+"</td>");
			    builder.append("<td>"+agent.getTelmed()+"</td>");
			    telmed += agent.getTelmed();
			    builder.append("</tr>");   
		    }
		}
		builder.append("<tr>");
		builder.append("<td>Other</td>");
	    builder.append("<td>"+(totalTelmed-telmed)+"</td>");
		builder.append("</tr>");
		builder.append("<tr>");
		builder.append("<td>Total Telmed</td>");
	    builder.append("<td>"+totalTelmed+"</td>");
		builder.append("</tr>");
		
		/*
		* PENDING TELMED
		*/
		builder.append("<tr bgcolor='#d3d3d3'>");
		builder.append("<td>Agent Name</td>");
		builder.append("<td>Pending Telmed</td>");
		builder.append("</tr>");
		int pendingTelmed = 0;
		it = agents.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, AgentReport> pair = (Entry<String, AgentReport>)it.next();
		    String key = pair.getKey();
		    AgentReport agent = agents.get(key);
		    if(agent.getPendingTelmed()>0) {
		    	builder.append("<tr>");
			    builder.append("<td>"+agent.getName()+"</td>");
			    builder.append("<td>"+agent.getPendingTelmed()+"</td>");
			    pendingTelmed += agent.getPendingTelmed();
			    builder.append("</tr>");
		    }
		}
		builder.append("<tr>");
		builder.append("<td>Other</td>");
	    builder.append("<td>"+(totalPendingTelmed-pendingTelmed)+"</td>");
		builder.append("</tr>");
		builder.append("<tr>");
		builder.append("<td>Total Pending Telmed</td>");
	    builder.append("<td>"+pendingTelmed+"</td>");
		builder.append("</tr>");	
		/*
		* FAILED TELMED
		*/
		builder.append("<tr bgcolor='#d3d3d3'>");
		builder.append("<td>Agent Name</td>");
		builder.append("<td>Failed Telmed</td>");
		builder.append("</tr>");
		int failedTelmed = 0;
		it = agents.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, AgentReport> pair = (Entry<String, AgentReport>)it.next();
		    String key = pair.getKey();
		    AgentReport agent = agents.get(key);
		    if(agent.getFailedTelmed()>0) {
		    	builder.append("<tr>");
			    builder.append("<td>"+agent.getName()+"</td>");
			    builder.append("<td>"+agent.getFailedTelmed()+"</td>");
			    failedTelmed += agent.getFailedTelmed();
			    builder.append("</tr>");	    
		    }
		}
		builder.append("<tr>");
		builder.append("<td>Other</td>");
	    builder.append("<td>"+(totalFailedTelmed-failedTelmed)+"</td>");
		builder.append("</tr>");
		builder.append("<tr>");
		builder.append("<td>Total Failed Telmed</td>");
	    builder.append("<td>"+failedTelmed+"</td>");
		builder.append("</tr>");
		/*
		* DME TELMED
		*/
		builder.append("<tr bgcolor='#d3d3d3'>");
		builder.append("<td>Agent Name</td>");
		builder.append("<td>DME Telmed</td>");
		builder.append("</tr>");
		int dmeTelmed = 0;
		it = agents.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, AgentReport> pair = (Entry<String, AgentReport>)it.next();
		    String key = pair.getKey();
		    AgentReport agent = agents.get(key);
		    if(agent.getDMETelmed()>0) {
		    	builder.append("<tr>");
			    builder.append("<td>"+agent.getName()+"</td>");
			    builder.append("<td>"+agent.getDMETelmed()+"</td>");
			    dmeTelmed += agent.getDMETelmed();
			    builder.append("</tr>");	    
		    }
		}
		builder.append("<tr>");
		builder.append("<td>Other</td>");
	    builder.append("<td>"+(totalDME-dmeTelmed)+"</td>");
		builder.append("</tr>");
		builder.append("<tr>");
		builder.append("<td>Total DME Telmed</td>");
	    builder.append("<td>"+totalDME+"</td>");
		builder.append("</tr>");
		/*
		* POINTS
		*/
		builder.append("<tr bgcolor='#d3d3d3'>");
		builder.append("<td>Agent Name</td>");
		builder.append("<td>Points</td>");
		builder.append("</tr>");
		it = agents.entrySet().iterator();
		int totalPoints = 0;
		while (it.hasNext()) {
			Map.Entry<String, AgentReport> pair = (Entry<String, AgentReport>)it.next();
		    String key = pair.getKey();
		    AgentReport agent = agents.get(key);
		    int points = (agent.getLeadCount()+(agent.getTelmed()*2)+(agent.getDMETelmed()*2))-agent.getWrongDoctor();
		    builder.append("<tr>");
			builder.append("<td>"+agent.getName()+"</td>");
			builder.append("<td>"+points+"</td>");
			builder.append("</tr>");
			totalPoints += points;
		}
		builder.append("<tr>");
		builder.append("<td>Total</td>");
	    builder.append("<td>"+totalPoints+"</td>");
		builder.append("</tr>");
		builder.append("<tr>");
		return builder.toString();
	}
	
	/*
	 * Get Time Frame Queries
	 */
	private int ReturnTimeBack(String timeBack) {
		int back = 0;
		if(timeBack==null)
			back = 0;
		else {
			try {
				back = Integer.parseInt(timeBack);
			} catch(NumberFormatException ne) {
				JOptionPane.showMessageDialog(null, "Not a valid number");
				return 0;
			}
		}
		return back;
	}
	private String CreateTimeFrame(String time,String timeBack) {
		String timeFrame = null;
		int back = ReturnTimeBack(timeBack);
		switch(time) {
		case "Day":
			timeFrame = GetDailyQuery(back);
			break;
		case "Week":
			timeFrame = GetWeeklyQuery(back);
			break;
		case "Month":
			timeFrame = GetMonthlyQuery(back);
			break;
		case "Year":
			timeFrame = GetYearlyQuery(back);
			break;
		default:
			timeFrame = GetDailyQuery(back);
			break;
		}
		return timeFrame;
	}
	private String CreateTelmedTimeFrame(String time,String timeBack) {
		String timeFrame = null;
		int back = ReturnTimeBack(timeBack);
		switch(time) {
		case "Day":
			timeFrame = GetDailyQueryTelmed(back);
			break;
		case "Week":
			timeFrame = GetWeeklyQueryTelmed(back);
			break;
		case "Month":
			timeFrame = GetMonthlyQueryTelmed(back);
			break;
		case "Year":
			timeFrame = GetYearlyQueryTelmed(back);
			break;
		default:
			timeFrame = GetDailyQueryTelmed(back);
			break;
		}
		return timeFrame;
	}
	private String CreateQueryParams(String pharmacy,String callCenter,String agent,String column, String value) {
		StringBuilder query = new StringBuilder();
		if(!pharmacy.equalsIgnoreCase("ALL"))
			query.append("`PHARMACY` = '"+pharmacy+"' AND ");
		if(!callCenter.equalsIgnoreCase("ALL"))
			query.append("`CALL_CENTER` = '"+callCenter+"' AND ");
		if(!column.equalsIgnoreCase("ALL"))
			query.append("`"+column+"` = '"+value+"' AND ");
		if(!agent.equalsIgnoreCase("ALL"))
			query.append("`agent` = '"+agent+"'");
		if(query.toString().endsWith("AND "))
			query = new StringBuilder(query.substring(0, query.length()-4));		
		if(query.toString().length()>0) {
			query.insert(0, "(");
			query.append(") AND ");
		}
		return query.toString();
	}
	/*
	 * LEAD GENERATION TIME QUERIES
	 */
	private String GetDailyQuery(int back) {
		return "`DATE_ADDED` = DATE_ADD(CURDATE(), INTERVAL -"+back+" DAY)";
	}
	private String GetWeeklyQuery(int back) {
		return "YEARWEEK(`DATE_ADDED`) = YEARWEEK(DATE_ADD(CURRENT_DATE(),INTERVAL -"+back+" WEEK))";
	}
	private String GetMonthlyQuery(int back) {
		return "MONTH(`DATE_ADDED`) = MONTH(DATE_ADD(CURRENT_DATE(),INTERVAL -"+back+" MONTH)) AND YEAR(`DATE_ADDED`) = YEAR(DATE_ADD(CURRENT_DATE(),INTERVAL -"+back+" MONTH))";
	}
	private String GetYearlyQuery(int back) {
		return "YEAR(`DATE_ADDED`) = YEAR(DATE_ADD(CURRENT_DATE(),INTERVAL -"+back+" YEAR))";
	}
	/*
	 * TELMED TIME QUERIES
	 */
	private String GetDailyQueryTelmed(int back) {
		return "`DATE_MODIFIED` = DATE_ADD(CURDATE(), INTERVAL -"+back+" DAY)";
	}
	private String GetWeeklyQueryTelmed(int back) {
		return "YEARWEEK(`DATE_MODIFIED`) = YEARWEEK(DATE_ADD(CURRENT_DATE(),INTERVAL -"+back+" WEEK))";
	}
	private String GetMonthlyQueryTelmed(int back) {
		return "MONTH(`DATE_MODIFIED`) = MONTH(DATE_ADD(CURRENT_DATE(),INTERVAL -"+back+" MONTH)) AND YEAR(`DATE_MODIFIED`) = YEAR(DATE_ADD(CURRENT_DATE(),INTERVAL -"+back+" MONTH))";
	}
	private String GetYearlyQueryTelmed(int back) {
		return "YEAR(`DATE_MODIFIED`) = YEAR(DATE_ADD(CURRENT_DATE(),INTERVAL -"+back+" YEAR))";
	}
	private String GetMonthlyFaxDispositionQuery(int back) {
		return "MONTH(`FAX_DISPOSITION_DATE`) = MONTH(CURRENT_DATE())-"+back+" AND YEAR(`FAX_DISPOSITION_DATE`) = YEAR(CURRENT_DATE())";
	}
	
}
