package Fax;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.json.JSONException;
import org.json.JSONObject;
import PBM.InsuranceFilter;
import PBM.InsuranceType;
import client.Record;
import client.RingCentralClient;
import images.Script;
import images.Script.ScriptException;
import images.ScriptNew;

public class Fax {
	
	public static synchronized JSONObject SendFax(Record record,RingCentralClient client,File file) throws IOException, ScriptException, JSONException {
		JSONObject result = client.SendFax(record,file);
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
		if(record.getPharmacy().equalsIgnoreCase("All_Pharmacy")) {
			switch(record.getBin()) {
				case "015581":
				case "015599":
				case "610014":
				case "003858":
				case "017010":
					return new Script(Drug.Diflorasone180);
				case "610097":
					return new Script(Drug.Ketoprofen240);
				default:
					return new Script(Fax.class.getClassLoader().getResource(Script.DR_CHASE_MAX));
			}
			
		}
		if(isMedicare(record)) {
			switch(record.getBin()) {
				case "004336":
				{
					if(record.getGrp().equalsIgnoreCase("RX6270"))
						return new Script(Fax.class.getClassLoader().getResource(Script.RX_6270));
					else if(record.getGrp().equalsIgnoreCase("RX8120"))
						return new Script(Fax.class.getClassLoader().getResource(Script.RX_8120));
					else if(record.getGrp().equalsIgnoreCase("RXCVSD"))
						return new Script(Fax.class.getClassLoader().getResource(Script.SILVER_SCRIPTS));
					else
						return new Script(Fax.class.getClassLoader().getResource(Script.CAREMARK));
				}
				case "610591":
					return new Script(Fax.class.getClassLoader().getResource(Script.CAREMARK));
				case "610239":
					return new Script(Fax.class.getClassLoader().getResource(Script.DR_CHASE_MAX));
				case "610502":
					return new Script(Fax.class.getClassLoader().getResource(Script.AETNA));
				case "020115":
					return new Script(Fax.class.getClassLoader().getResource(Script.INGENIO_RX));
				case "015581":
				case "015599":
					return new Script(Fax.class.getClassLoader().getResource(Script.HUMANA));
				case "610097":
					if(record.getGrp().equalsIgnoreCase("SHCA"))
						return new Script(Fax.class.getClassLoader().getResource(Script.OPTUM_RX_SHCA));
					else
						return new Script(Fax.class.getClassLoader().getResource(Script.OPTUM_RX));
				case "610014":
				case "400023":
					return new Script(Fax.class.getClassLoader().getResource(Script.ESI));
				case "017010":
					return new Script(Fax.class.getClassLoader().getResource(Script.CIGNA));
				case "015574":
					return new Script(Fax.class.getClassLoader().getResource(Script.MEDIMPACT));
				default: return new Script(Fax.class.getClassLoader().getResource(Script.DR_CHASE_MAX));	
			}
		}
		else 
			return new Script(Fax.class.getClassLoader().getResource(Script.DR_CHASE_MAX));
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
	public static RingCentralClient GetRingCentralClient(Record record,String database) {
		if(record==null)
			return new RingCentralClient("15612660791","Winston4503","YHkGqwbYRCSrR12MLGGlNg","8FGD3qd_TLS_wIH_AmrOaQOvHnqKegS6K1TH-NEqo3qw");
		switch(database) {
			case "MT_MARKETING":
				switch(record.getPharmacy()) {
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
					case "All_Pharmacy":
						return new RingCentralClient("15612088766","Winston4503","2GLlQwihSBWTtWOjqU7fGg","2pWwHGALTqKjOM3J8CQa-wSmLoqPNBSq-UL1jppP_n3g");
					case "MedRx":
						if(record.getState().equalsIgnoreCase("LA"))
							return new RingCentralClient("13182556193","Kuenzler5726","ia661QoQRh2Nqr6wD3HdYg","PAXMi-8HSdGgcujvdrVbpwrVZ30H61TcOMVx-kRcysJw");
						else
							return new RingCentralClient("18324581961","Kuenzler5726","ia661QoQRh2Nqr6wD3HdYg","PAXMi-8HSdGgcujvdrVbpwrVZ30H61TcOMVx-kRcysJw");
					default:
						return new RingCentralClient("15612660791","Winston4503","YHkGqwbYRCSrR12MLGGlNg","8FGD3qd_TLS_wIH_AmrOaQOvHnqKegS6K1TH-NEqo3qw");
				}		
			case "Coman_Marketing":
				switch(record.getPharmacy()) {
					case "Rheem":
						return new RingCentralClient("19254345024","Winston4503","93y9kwlkSge8rfugvPXTbw","xoKao-qQT1iuGsF6yPk_5gnyR8VYLGT12JtZRwN2-Pzg");
					default: 
						return new RingCentralClient("19546866708","Chino143","8fgCFRnxQFaZqlH2_3H08A","t87-C7YwRce73OIw54wtqAJFWUHagzQuKiitZm9aMrYQ");
				}
			default:
				return new RingCentralClient("15612660791","Winston4503","YHkGqwbYRCSrR12MLGGlNg","8FGD3qd_TLS_wIH_AmrOaQOvHnqKegS6K1TH-NEqo3qw");
		}
	}
	public static ScriptNew GetScriptNew(Record record,String login) throws InvalidPasswordException, IOException, URISyntaxException {
		if(record.getBin().equalsIgnoreCase(""))
			return new ScriptNew(record,Fax.class.getClassLoader().getResource(Script.DR_CHASE_MAX),login);
		if(record.getPharmacy().equalsIgnoreCase("All_Pharmacy")) 
			return new ScriptNew(record,Fax.class.getClassLoader().getResource(Script.CUSTOM_SCRIPT_WITH_COVER),login);
		if(isMedicare(record)) {
			switch(record.getBin()) {
				case "004336":
				{
					if(record.getGrp().equalsIgnoreCase("RX6270"))
						return new ScriptNew(record,Fax.class.getClassLoader().getResource(Script.RX_6270),login);
					else if(record.getGrp().equalsIgnoreCase("RX8120"))
						return new ScriptNew(record,Fax.class.getClassLoader().getResource(Script.RX_8120),login);
					else if(record.getGrp().equalsIgnoreCase("RXCVSD"))
						return new ScriptNew(record,Fax.class.getClassLoader().getResource(Script.SILVER_SCRIPTS),login);
					else
						return new ScriptNew(record,Fax.class.getClassLoader().getResource(Script.CAREMARK),login);
				}
				case "610591":
					return new ScriptNew(record,Fax.class.getClassLoader().getResource(Script.CAREMARK),login);
				case "610239":
					return new ScriptNew(record,Fax.class.getClassLoader().getResource(Script.DR_CHASE_MAX),login);
				case "610502":
					return new ScriptNew(record,Fax.class.getClassLoader().getResource(Script.AETNA),login);
				case "020115":
					return new ScriptNew(record,Fax.class.getClassLoader().getResource(Script.INGENIO_RX),login);
				case "015581":
				case "015599":
					return new ScriptNew(record,Fax.class.getClassLoader().getResource(Script.HUMANA),login);
				case "610097":
					if(record.getGrp().equalsIgnoreCase("SHCA"))
						return new ScriptNew(record,Fax.class.getClassLoader().getResource(Script.OPTUM_RX_SHCA),login);
					else
						return new ScriptNew(record,Fax.class.getClassLoader().getResource(Script.OPTUM_RX),login);
				case "610014":
				case "400023":
					return new ScriptNew(record,Fax.class.getClassLoader().getResource(Script.ESI),login);
				case "017010":
					return new ScriptNew(record,Fax.class.getClassLoader().getResource(Script.CIGNA),login);
				case "015574":
					return new ScriptNew(record,Fax.class.getClassLoader().getResource(Script.MEDIMPACT),login);
				default: return new ScriptNew(record,Fax.class.getClassLoader().getResource(Script.DR_CHASE_MAX),login);
			}
		}
		else 
		 return new ScriptNew(record,Fax.class.getClassLoader().getResource(Script.DR_CHASE_MAX),login);
	}
}
