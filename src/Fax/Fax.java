package Fax;

import java.io.IOException;
import java.net.URISyntaxException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import PBM.InsuranceFilter;
import PBM.InsuranceType;
import PivotTable.LoadData;
import client.Record;
import client.RingCentralClient;
import images.Script;
import images.Script.ScriptException;

public class Fax {
	
	public static synchronized JSONObject SendFax(Record record,RingCentralClient client,Script script) throws IOException, ScriptException, JSONException {
		JSONObject result = client.SendFax(record,script.PopulateScript(record,client.number));
		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			
		}
		return result;
	}
	public static Script GetScript(Record record,String database) throws ScriptException, IOException, URISyntaxException {
		if(database.equalsIgnoreCase("COMAN_MARKETING"))
			record.setTag("CM");
		if(record.getBin()==null)
			return new Script(Fax.class.getClassLoader().getResource(Script.DR_CHASE_MAX));
		if(isMedicare(record)) {
			switch(record.getBin()) {
				case "004336":
				case "610239":
				case "610591":
					return new Script(Fax.class.getClassLoader().getResource(Script.CAREMARK));
				case "610502":
					return new Script(Fax.class.getClassLoader().getResource(Script.AETNA));
				case "020115":
					return new Script(Fax.class.getClassLoader().getResource(Script.INGENIO_RX));
				case "015581":
				case "015599":
					return new Script(Fax.class.getClassLoader().getResource(Script.HUMANA));
				case "610097":
					return new Script(Fax.class.getClassLoader().getResource(Script.OPTUM_RX));
			}
			LoadData data = new LoadData();
			data.GetData(LoadData.LAKE_IDA_LIST);
			Drug[] drugs = data.GetDrugs(record);
			if(drugs!=null) {
				Script script = new Script(Fax.class.getClassLoader().getResource(Script.LIVE_SCRIPT));
				script.setDrugs(drugs);
				return script;
			}
			else
				return new Script(Fax.class.getClassLoader().getResource(Script.DR_CHASE_MAX));
		}
		else {
			return new Script(Fax.class.getClassLoader().getResource(Script.DR_CHASE_MAX));
		}
	}
	private static Drug[] SwapDrugs(Record record,Drug[] drugs) {
		switch(record.getBin()) {
			case "610502":
			case "004336":
			case "020115":
			case "610591":
			case "020099":
			case "610239":
				break;
			default:
				return drugs;
		}
		Drug[] newDrugs = new Drug[drugs.length];
		int count = 0;
		for(Drug drug: drugs) {
			if(drug!=null) {
				if(drug.IsSameDrug(Drug.Chlorzoxazone250)) {
					newDrugs[count] = Drug.Chlorzoxazone375;
					count++;
				}
				else {
					newDrugs[count] = drug;
					count++;
				}
	
			}
		}
		return newDrugs;
	}
	private static Drug[] SwapManufactorersRecommendation(Drug[] drugs) {
		Drug[] newDrugs = new Drug[drugs.length];
		int count = 0;
		for(Drug drug: drugs) {
			if(drug==null)
				continue;
			if(drug.IsSameDrug(Drug.Diflorasone360)) {
				newDrugs[count] = Drug.Diflorasone180;
				count++;
			}
			else if(drug.IsSameDrug(Drug.Desoximetasone360)) {
				newDrugs[count] = Drug.Desoximetasone120;
				count++;
			}
			else if(drug.IsSameDrug(Drug.Calcipotrene360)) {
				newDrugs[count] = Drug.Calcipotrene240;
				count++;
			}
			else if(drug.IsSameDrug(Drug.Lidocaine300)) {
				newDrugs[count] = Drug.Lidocaine250;
				count++;
			}
			else if(drug.IsSameDrug(Drug.Clobetasol360)) {
				newDrugs[count] = Drug.Clobetasol180;
				count++;
			}
			else if(drug.IsSameDrug(Drug.LidoPrilo360)) {
				newDrugs[count] = Drug.LidoPrilo240;
				count++;
			}
			else {
				newDrugs[count] = drug;
				count++;
			}
		}
		return newDrugs;
	}
	private static Drug[] SwapToHigher(Drug[] drugs) {
		Drug[] newDrugs = new Drug[drugs.length];
		int count = 0;
		for(Drug drug: drugs) {
			if(drug==null)
				continue;
			if(drug.IsSameDrug(Drug.Diflorasone360)) {
				newDrugs[count] = Drug.Diflorasone360;
				count++;
			}
			else if(drug.IsSameDrug(Drug.Desoximetasone360)) {
				newDrugs[count] = Drug.Desoximetasone360;
				count++;
			}
			else if(drug.IsSameDrug(Drug.Calcipotrene360)) {
				newDrugs[count] = Drug.Calcipotrene360;
				count++;
			}
			else if(drug.IsSameDrug(Drug.Lidocaine300)) {
				newDrugs[count] = Drug.Lidocaine300;
				count++;
			}
			else if(drug.IsSameDrug(Drug.Clobetasol360)) {
				newDrugs[count] = Drug.Clobetasol360;
				count++;
			}
			else if(drug.IsSameDrug(Drug.LidoPrilo360)) {
				newDrugs[count] = Drug.LidoPrilo360;
				count++;
			} else {
				newDrugs[count] = drug;
				count++;
			}
		}
		return newDrugs;
	}
	private static String GetMessageId(String json) {
		JSONObject obj;
		try {
			obj = new JSONObject(json);
			JSONArray to = obj.getJSONArray("to");
			return String.valueOf(obj.get("id"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return e.getMessage();
		}
		
	}
	private static boolean isMedicare(Record record) {
		String status = InsuranceFilter.Filter(record);
		if(status==null)
			return false;
		switch(status) {
			case InsuranceType.MAPD:
			case InsuranceType.MAPD_PPO:
			case InsuranceType.MAPD_HMO:
			case InsuranceType.MEDICAID_MEDICARE:
			case InsuranceType.MEDICARE_TELMED:
			case InsuranceType.PDP:
			case InsuranceType.MEDICARE_COMMERCIAL:
				return true;
			default:
				return false;
		}
	}
	public static RingCentralClient GetRingCentralClient(String pharmacy,String database) {
		switch(database) {
			case "MT_MARKETING":
				switch(pharmacy) {
					case "Sterling":
						return new RingCentralClient("13252676022","Winston4503","YHkGqwbYRCSrR12MLGGlNg","8FGD3qd_TLS_wIH_AmrOaQOvHnqKegS6K1TH-NEqo3qw");
					case "Hershey":
						return new RingCentralClient("19194468308","Kuenzler5726","5D_GKWGNTYKjpeqQxo8-Ww","wjmI17MhTD25GR17MT7QCwcMlZI_mKTWasKFwnfXCllQ");
					case "Millennium":
						return new RingCentralClient("16152589641","Winston4503","YHkGqwbYRCSrR12MLGGlNg","8FGD3qd_TLS_wIH_AmrOaQOvHnqKegS6K1TH-NEqo3qw");
					case "Jewel":
						return new RingCentralClient("17085713340","Winston4503","YHkGqwbYRCSrR12MLGGlNg","8FGD3qd_TLS_wIH_AmrOaQOvHnqKegS6K1TH-NEqo3qw");
					case "Rheem":
						return new RingCentralClient("19254345024","Winston4503","93y9kwlkSge8rfugvPXTbw","xoKao-qQT1iuGsF6yPk_5gnyR8VYLGT12JtZRwN2-Pzg");
					case "Fusion":
						return new RingCentralClient("15612706929","Winston4503","YHkGqwbYRCSrR12MLGGlNg","8FGD3qd_TLS_wIH_AmrOaQOvHnqKegS6K1TH-NEqo3qw");
					case "Eagle":
						return new RingCentralClient("17573514596","Kuenzler5726","CnE3L5LaQkipFr1-Yw88xg","jzL1Kk9jR0ypDfW5JxI6kQsxrMT3p0TEatW5UF3Pbbpw");
					default:
						return new RingCentralClient("15612660791","Winston4503","YHkGqwbYRCSrR12MLGGlNg","8FGD3qd_TLS_wIH_AmrOaQOvHnqKegS6K1TH-NEqo3qw");
				}		
			case "Coman_Marketing":
				switch(pharmacy) {
					case "Rheem":
						return new RingCentralClient("19254345024","Winston4503","93y9kwlkSge8rfugvPXTbw","xoKao-qQT1iuGsF6yPk_5gnyR8VYLGT12JtZRwN2-Pzg");
					default: 
						return new RingCentralClient("19546866708","Chino143","8fgCFRnxQFaZqlH2_3H08A","t87-C7YwRce73OIw54wtqAJFWUHagzQuKiitZm9aMrYQ");
				}
			default:
				return new RingCentralClient("15612660791","Winston4503","YHkGqwbYRCSrR12MLGGlNg","8FGD3qd_TLS_wIH_AmrOaQOvHnqKegS6K1TH-NEqo3qw");
		}
	}
}
