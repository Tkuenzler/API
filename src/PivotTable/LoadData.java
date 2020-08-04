package PivotTable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import Fax.Drug;
import client.Record;

public class LoadData {
	public static final String BACH_LIST = "PivotTable/Bach.txt";
	public static final String LAKE_IDA_LIST = "PivotTable/Lake Ida.txt";
	public JSONObject data;
	public LoadData() {
		
	}
	public void GetData(String list) {
		BufferedReader br = null;
		File file = new File(LoadData.class.getClassLoader().getResource(list).getFile().replace("%20", " "));
		StringBuilder sb = new StringBuilder();
		try {
			br = new BufferedReader(new FileReader(file));
			String line = null;
			while((line=br.readLine())!=null) {
				sb.append(line);
			}
			data = new JSONObject(sb.toString());
		} catch(IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	private JSONObject LoadProductList(Record record) {
		if(!data.has(record.getBin()))
			return null;
		try {
			JSONObject bin = data.getJSONObject(record.getBin());
			if(!bin.getJSONObject("PCNs").has(translatePCN(record))) 
				return bin;
			JSONObject pcn = bin.getJSONObject("PCNs").getJSONObject(translatePCN(record));
			if(!pcn.getJSONObject("GRPs").has(translateGRP(record))) 
				return pcn;	
			JSONObject grp = pcn.getJSONObject("GRPs").getJSONObject(translateGRP(record));
			return grp;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(NumberFormatException e) {
			e.printStackTrace();
		}
		return null;
	}
	private String translatePCN(Record record) throws NumberFormatException {
		if(record.getBin().equalsIgnoreCase("610014") && record.getPcn().equalsIgnoreCase("No Data Returned"))
			return "PEU";
		else if(record.getPcn().equalsIgnoreCase("No Data Returned"))
			return "";
		else if(record.getBin().equalsIgnoreCase("017010") && !record.getPcn().equalsIgnoreCase("CIMCARE"))
			return ""+Integer.parseInt(record.getPcn());
		else if(record.getBin().equalsIgnoreCase("610127"))
			return ""+Integer.parseInt(record.getPcn());
		else if(record.getBin().equalsIgnoreCase("015581"))
			return ""+Integer.parseInt(record.getPcn());
		else if(record.getBin().equalsIgnoreCase("610502") && !record.getPcn().equalsIgnoreCase("MEDDAET"))
			return ""+Integer.parseInt(record.getPcn());
		else
			return record.getPcn();
		
	}
	private String translateGRP(Record record) throws NumberFormatException {
		if(record.getGrp().equalsIgnoreCase("No Data Returned"))
			return "";
		else if(record.getPcn().equalsIgnoreCase("BCTX"))
			return ""+Integer.parseInt(record.getGrp());
		
		else if(record.getBin().equalsIgnoreCase("610127"))
			return ""+Integer.parseInt(record.getGrp());
		else
			return record.getGrp();
	}
	public Drug[] GetDrugs(Record record) {
		try {
			JSONObject list = LoadProductList(record);
			if(list==null)
				return null;
			JSONObject data = list.getJSONObject("Data");
			JSONObject products = list.getJSONObject("Products");
			double totalCount = data.getInt("Count");
			Iterator keys = products.keys();
			TreeMap<String, Double> treemap = new TreeMap<String, Double>();
			while (keys.hasNext()) {
			    String key = (String)keys.next();
			    if(key.equalsIgnoreCase("Data") || key.equalsIgnoreCase("UNKNOWN"))
			    	continue;
			    JSONObject product = products.getJSONObject(key);
			    JSONObject productData = product.getJSONObject("Data");
			    double count = productData.getInt("Count");
			    double value = (((count/totalCount)*100)*productData.getDouble("AverageProfit"))/100;
			    treemap.put((String)key, value);
			}
			String[] therapies = GetTherapies(treemap);
			String[] names = new String[3];
			Drug[] drugs = new Drug[3];
			for(int i = 0;i<therapies.length;i++) {
				if(therapies[i]==null)
					continue;
				String name = GetTopDrug(products,therapies[i],totalCount);
				System.out.println(name);
				if(name!=null)
					names[i] = name;
			}
			for(int i = 0;i<names.length;i++) {
				if(names[i]==null)
					continue;
				else 
					drugs[i] = Drug.GetDrug(names[i]);
			}
			return drugs;
		} catch(JSONException e) {
			e.printStackTrace();
		}
		System.out.println("RETURNING NULL");
		return null;
	}
	private void AddEntry(JSONObject obj, Entry entry) throws JSONException {
		if(entry.profit<0)
			return;
		AddBinProductData(obj,entry);
	}
	private void AddGrpProductData(JSONObject obj,Entry entry) throws JSONException {
		if(!obj.has("GRPs"))
			obj.put("GRPs", new JSONObject());
		JSONObject grps = obj.getJSONObject("GRPs");
		if(!grps.has(entry.getGrp()))
			grps.put(entry.getGrp(), new JSONObject());
		JSONObject grp = grps.getJSONObject(entry.getGrp());
		addData(grp,entry);	
		if(!grp.has("Products"))
			grp.put("Products", new JSONObject());
		JSONObject products = grp.getJSONObject("Products");
		if(!products.has(entry.getDrugGroup()))
			products.put(entry.getDrugGroup(), new JSONObject());
		JSONObject drugGroup = products.getJSONObject(entry.getDrugGroup());
		addData(drugGroup,entry);	
		if(!drugGroup.has(entry.getDrugName()))
			drugGroup.put(entry.getDrugName(), new JSONObject());
		JSONObject drug = drugGroup.getJSONObject(entry.getDrugName());
		addData(drug,entry);
	}
	private void AddPcnProductData(JSONObject obj,Entry entry) throws JSONException {
		if(!obj.has("PCNs"))
			obj.put("PCNs", new JSONObject());
		JSONObject pcns = obj.getJSONObject("PCNs");
		if(!pcns.has(entry.getPcn()))
			pcns.put(entry.getPcn(), new JSONObject());
		JSONObject pcn = pcns.getJSONObject(entry.getPcn());
		addData(pcn,entry);	
		if(!pcn.has("Products"))
			pcn.put("Products", new JSONObject());
		JSONObject products = pcn.getJSONObject("Products");
		if(!products.has(entry.getDrugGroup()))
			products.put(entry.getDrugGroup(), new JSONObject());
		JSONObject drugGroup = products.getJSONObject(entry.getDrugGroup());
		addData(drugGroup,entry);	
		if(!drugGroup.has(entry.getDrugName()))
			drugGroup.put(entry.getDrugName(), new JSONObject());
		JSONObject drug = drugGroup.getJSONObject(entry.getDrugName());
		addData(drug,entry);
		AddGrpProductData(pcn,entry);
	}
	private void AddBinProductData(JSONObject obj,Entry entry) throws JSONException {
		if(!obj.has(entry.getBin()))
			obj.put(entry.getBin(), new JSONObject());
		JSONObject bins = obj.getJSONObject(entry.getBin());
		addData(bins,entry);	
		if(!bins.has("Products"))
			bins.put("Products", new JSONObject());
		JSONObject products = bins.getJSONObject("Products");
		if(!products.has(entry.getDrugGroup()))
			products.put(entry.getDrugGroup(), new JSONObject());
		JSONObject drugGroup = products.getJSONObject(entry.getDrugGroup());
		addData(drugGroup,entry);	
		if(!drugGroup.has(entry.getDrugName()))
			drugGroup.put(entry.getDrugName(), new JSONObject());
		JSONObject drug = drugGroup.getJSONObject(entry.getDrugName());
		addData(drug,entry);
		AddPcnProductData(bins,entry);
	}
	
	private void addData(JSONObject obj,Entry entry) throws JSONException {
		JSONObject data = null;
		if(!obj.has("Data"))
			data = new JSONObject();
		else
			data = obj.getJSONObject("Data");
		if(!data.has("SumOfProfit"))	
			data.put("SumOfProfit", 0);
		if(!data.has("Count"))
			data.put("Count", 0);
		if(!data.has("AverageProfit"))
			data.put("AverageProfit", 0);
		double profit = data.getDouble("SumOfProfit");
		profit += entry.getProfit();
		data.put("SumOfProfit", profit);
		double count = data.getDouble("Count");
		count++;
		data.put("Count", count);	
		data.put("AverageProfit", profit/count);
		obj.put("Data", data);
	}
	private String GetTopDrug(JSONObject products,String category,double totalCount) throws JSONException {
		JSONObject obj = products.getJSONObject(category);
		Iterator keys = obj.keys();
		TreeMap<String, Double> treemap = new TreeMap<String, Double>();
		while (keys.hasNext()) {
		    String key = (String)keys.next();
		    if(key.equalsIgnoreCase("Data"))
		    	continue;
		    JSONObject productData = obj.getJSONObject(key).getJSONObject("Data");
		    double count = productData.getInt("Count");
		    double value = (((count/totalCount)*100)*productData.getDouble("AverageProfit"))/100;
		    treemap.put((String)key, value);
		}
		return GetTop(treemap);
	}
	private String[] GetTherapies(TreeMap<String,Double> treemap) {
		String[] therapies = new String[3];
		Map<String, Double> sortedMap = sortByValues(treemap);
		Set<java.util.Map.Entry<String, Double>> set = sortedMap.entrySet();
		// Get iterator
		Iterator<java.util.Map.Entry<String, Double>> it = set.iterator();
		    // Show TreeMap elements
		    int count = 0;
		    while(it.hasNext()) {
		      Map.Entry pair = (Map.Entry)it.next();
		      therapies[count] = (String) pair.getKey();
		      count++;
		      if(count==3)
		    	  return therapies;
		    }
		 return therapies;
	}
	private String GetTop(TreeMap<String,Double> treemap) {
		Map<String, Double> sortedMap = sortByValues(treemap);
		Set<java.util.Map.Entry<String, Double>> set = sortedMap.entrySet();
		// Get iterator
		Iterator<java.util.Map.Entry<String, Double>> it = set.iterator();
		    // Show TreeMap elements
		    int count = 0;
		    while(it.hasNext()) {
		      Map.Entry pair = (Map.Entry)it.next();
		      return (String)pair.getKey();
		    }
		return null;
	}
	public static <K, V extends Comparable<V>> Map<K, V> 
	  sortByValues(final Map<K, V> map) {
	    Comparator<K> valueComparator = 
	             new Comparator<K>() {
	      public int compare(K k1, K k2) {
	        int compare = 
	              map.get(k2).compareTo(map.get(k1));
	        if (compare == 0) 
	          return 1;
	        else 
	          return compare;
	      }
	    };
	 
	    Map<K, V> sortedByValues = 
	      new TreeMap<K, V>(valueComparator);
	    sortedByValues.putAll(map);
	    return sortedByValues;
	    }
}
