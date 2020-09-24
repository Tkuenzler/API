package images;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDCheckBox;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;

import Fax.Drug;
import Fax.Fax;
import PBM.InsuranceFilter;
import PBM.InsuranceType;
import client.Record;
import images.Script.ScriptException;

public class Script {
	public static final String LIVE_SCRIPT = "images/Live Script.pdf";
	public static final String DR_CHASE_MIN = "images/DR_CHASE - MIN.pdf";
	public static final String DR_CHASE_MAX = "images/DR_CHASE - MAX.pdf";
	public static final String HUMANA = "images/Humana.pdf";
	public static final String CAREMARK = "images/Caremark.pdf";
	public static final String SILVER_SCRIPTS = "images/Silverscripts.pdf";
	public static final String RX_8120 = "images/RX8120.pdf";
	public static final String RX_6270 = "images/RX6270.pdf";
	public static final String AETNA = "images/Aetna.pdf";
	public static final String OPTUM_RX = "images/OptumRx.pdf";
	public static final String OPTUM_RX_SHCA = "images/OptumRx - SHCA.pdf";
	public static final String OPTUM_RX_ALL_FAMILY = "images/Optum Rx - FL.pdf";
	public static final String INGENIO_RX = "images/IngenioRx.pdf";
	public static final String MEDIMPACT = "images/Medimpact.pdf";
	public static final String CIGNA = "images/Cigna.pdf";
	public static final String ESI = "images/ESI.pdf";
	public static final String CUSTOM_SCRIPT = "images/Custom Script.pdf";
	public static final String CUSTOM_SCRIPT_WITH_COVER = "images/Custom With Cover.pdf";
	
	URL src;
	PDDocument pdfDocument;
	Record record;
	PDDocumentCatalog docCatalog = null;
	PDAcroForm acroForm = null;
	PDFMergerUtility pdfMerger;
	File file = null;
	Drug[] drugs;
	Drug drug;
	public Script(URL src) throws ScriptException, IOException, URISyntaxException {
		this.src = src;
		pdfDocument = PDDocument.load(new File(src.toURI()));
		System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");
		docCatalog = pdfDocument.getDocumentCatalog();
		acroForm = docCatalog.getAcroForm();
		
	}
	public Script(Drug drug) {
		try {
			pdfDocument = PDDocument.load(new File(Fax.class.getClassLoader().getResource(Script.CUSTOM_SCRIPT).toURI()));
			docCatalog = pdfDocument.getDocumentCatalog();
			acroForm = docCatalog.getAcroForm();
			this.drug = drug;
		} catch (IOException | URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void setDrugs(Drug[] drugs) {
		this.drugs = drugs;
	}
	public File getFile() {
		return this.file;
	}
 	public void CreateAndPopulate(Record record,String number) {
 		try {
			//COVER PAGE
			List<PDField> fields = acroForm.getFields();
			PopulateScript(fields,record,number);
			file = File.createTempFile("FAX", ".pdf");
			pdfDocument.save(file);
			AddScripts(record,number);
			if(record.getPharmacy().equalsIgnoreCase("All_Pharmacy")) {
				AddScriptsAllFamilyPharmacy(record,number);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 	}
 	public void AddScripts(Record record,String number) throws InvalidPasswordException, IOException, ScriptException, URISyntaxException {
 		boolean fungal = false;
		if(record.getProducts()!=null)
		for(String product: record.getProducts()) {
			switch(product.trim()) {
				case "Migraines":
					AddScript(record, number, Drug.GetMigraineScript(record), null);
					break;
				case "Anti-Fungal":
					if(!fungal) {
						AddScript(record, number, Drug.GetAntiFungal(record), null);
						fungal = true;
					}
					break;
				case "Podiatry":
					if(!fungal) {
						AddScript(record, number, Drug.GetFootSoak(record), Drug.GetAntiFungal(record));
						fungal = true;
					}
					break;
			}
		}
 	}
	public void PopulateScript(List<PDField> fields,Record record,String login) throws IOException {
		String number = null;
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		Date date = new Date();
		for(PDField field: fields) {
			switch(field.getPartialName()) {
				case "TAG":
					acroForm.getField("TAG").setValue(record.getTag());
					break;
				case "Fax":
					number = login.substring(1, login.length());
					if(number.length()==10)
						number = "("+number.substring(0,3)+") "+number.substring(3,6)+"-"+number.substring(6,10);
					acroForm.getField("Fax").setValue(number); 
					break;
				case "Date":
					acroForm.getField("Date").setValue(dateFormat.format(date).toUpperCase()); 
					break;
				case "Notes":
					acroForm.getField("Notes").setValue("Patient is experiencing pain due to "+record.getPainCause()+" on "+record.getPainLocation()); 
					break;
				case "Patient Name":
					acroForm.getField("Patient Name").setValue(record.getFirstName().toUpperCase()+" "+record.getLastName().toUpperCase()); 
					break;
				case "Patient Phone":
					number = record.getPhone();
					if(number.length()==10)
						number = "("+number.substring(0,3)+") "+number.substring(3,6)+"-"+number.substring(6,10);
					else
						number = record.getPhone();
					acroForm.getField("Patient Phone").setValue(number); 
					break;
				case "DOB":
					acroForm.getField("DOB").setValue(record.getDob().toUpperCase()); 
					break;
				case "Patient Address":
					acroForm.getField("Patient Address").setValue(record.getAddress().toUpperCase()); 
					break;
				case "City/State/Zip":
					acroForm.getField("City/State/Zip").setValue(record.getCity().toUpperCase()+"/"+record.getState().toUpperCase()+"/"+record.getZip().toUpperCase()); break;
				case "Carrier":
					acroForm.getField("Carrier").setValue(record.getCarrier()); 
					break;
				case "Policy Id":
					acroForm.getField("Policy Id").setValue(record.getPolicyId()); 
					break;
				case "BIN":
					acroForm.getField("BIN").setValue(record.getBin()); 
					break;
				case "GROUP":
					acroForm.getField("GROUP").setValue(record.getGrp()); 
					break;
				case "PCN":
					acroForm.getField("PCN").setValue(record.getPcn()); 
					break;
				case "Doctor City/State/Zip":
					acroForm.getField("Doctor City/State/Zip").setValue(record.getDrCity().toUpperCase()+"/"+record.getDrState().toUpperCase()+"/"+record.getDrZip().toUpperCase()); 
					break;
				case "Doctor Name":
					acroForm.getField("Doctor Name").setValue(record.getDrFirst().toUpperCase()+" "+record.getDrLast().toUpperCase()); 
					break;
				case "Doctor Phone":
					number = record.getDrPhone();
					if(number.length()==10)
						number = "("+number.substring(0,3)+") "+number.substring(3,6)+"-"+number.substring(6,10);
					else
						number = record.getDrPhone();
					acroForm.getField("Doctor Phone").setValue(number); 
					break;
				case "Doctor Fax":
					number = record.getDrFax();
					if(number.length()==10)
						number = "("+number.substring(0,3)+") "+number.substring(3,6)+"-"+number.substring(6,10);
					else
						number = record.getDrFax();
					acroForm.getField("Doctor Fax").setValue(number); 
					break;
				case "NPI":
					acroForm.getField("NPI").setValue(record.getNpi().toUpperCase()); 
					break;
				case "Doctor Address":
					acroForm.getField("Doctor Address").setValue(record.getDrAddress().toUpperCase());
					break;
				case "Drug Therapy 1":
					acroForm.getField(field.getPartialName()).setValue(drug.getTherapy()); 
					break;
				case "Drug 1": 
					acroForm.getField(field.getPartialName()).setValue("Medication: "+drug.getName()); 
					break;
				case "Drug Qty 1":
					acroForm.getField(field.getPartialName()).setValue("Dispense: "+drug.getQty()); 
					break;
				case "Drug Sig 1":
					acroForm.getField(field.getPartialName()).setValue("Sig:  "+drug.getSig()); 
					break;
			}
		}
		PDCheckBox pain = (PDCheckBox) acroForm.getField("Pain");
		PDCheckBox derm = (PDCheckBox) acroForm.getField("Dermatitis");
		PDCheckBox vitamins = (PDCheckBox) acroForm.getField("Vitamins");
		PDCheckBox acid = (PDCheckBox) acroForm.getField("Acid");
		if(pain!=null)
			pain.check();
		if(derm!=null)
			derm.check();
		if(vitamins!=null)
			vitamins.check();
		if(acid!=null)
			acid.check();
	}
	public void PopulateDrugs(List<PDField> fields,Drug drug1,Drug drug2) throws IOException {
		for(PDField field: fields) {
			switch(field.getPartialName()) {
			case "Drug Therapy 1":
				if(drug1!=null)
					acroForm.getField("Drug Therapy 1").setValue(drug1.getTherapy()); 
				break;
			case "Drug 1":
				if(drug1!=null)
					acroForm.getField("Drug 1").setValue("Medication:  "+drug1.getName()); 
				break;
			case "Drug Qty 1": 
				if(drug1!=null)
					acroForm.getField("Drug Qty 1").setValue("Dispense:  "+drug1.getQty()); 
				break;
			case "Drug Sig 1":
				if(drug1!=null)
					acroForm.getField("Drug Sig 1").setValue("Sig:  "+drug1.getSig()); 
				break;
			case "Drug Therapy 2":
				if(drug2!=null)
					acroForm.getField("Drug Therapy 2").setValue(drug2.getTherapy()); 
				break;
			case "Drug 2":
				if(drug2!=null)
					acroForm.getField("Drug 2").setValue("Medication:  "+drug2.getName()); 
				break;
			case "Drug Qty 2": 
				if(drug2!=null)
					acroForm.getField("Drug Qty 2").setValue("Dispense:  "+drug2.getQty()); 
				break;
			case "Drug Sig 2":
				if(drug2!=null)
					acroForm.getField("Drug Sig 2").setValue("Sig:  "+drug2.getSig()); 
				break;
			}
		}
	}
	public void AddScript(Record record,String login,Drug drug1,Drug drug2) throws InvalidPasswordException, IOException, ScriptException, URISyntaxException {
		pdfMerger = new PDFMergerUtility();
		pdfDocument = PDDocument.load(new File(Script.class.getClassLoader().getResource(Script.CUSTOM_SCRIPT).toURI()));
		docCatalog = pdfDocument.getDocumentCatalog();
		acroForm = docCatalog.getAcroForm();
		try {
			//COVER PAGE
			List<PDField> fields = acroForm.getFields();
			PopulateScript(fields,record,login);
			PopulateScript(fields,record,login);
			if(drug1!=null || drug2!=null)
				PopulateDrugs(fields,drug1,drug2);
	        File file2 = File.createTempFile("FAX2", ".pdf");
	        pdfDocument.save(file2);
	        pdfMerger.setDestinationFileName(file.getAbsolutePath());
	        pdfMerger.addSource(file);
	        pdfMerger.addSource(file2);
	        pdfMerger.mergeDocuments(null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//JOptionPane.showMessageDialog(new JFrame(), e.getMessage());
		}  catch (IllegalArgumentException e2) {
			System.out.println(e2.getMessage());
			e2.printStackTrace();
			throw new ScriptException("Illegal Argument");
		}  finally {
			
		}
	}
	public void AddScriptsAllFamilyPharmacy(Record record,String number) throws InvalidPasswordException, IOException, ScriptException, URISyntaxException {
		int type = InsuranceFilter.GetInsuranceType(record);
		switch(record.getBin()) {
			case "015581":
			case "015589":
			{
				AddScript(record,number,Drug.LidoPrilo240,null);
				if(record.getCurrentAge()>=60)
					AddScript(record,number,Drug.Methocarbamol750,null);
				else
					AddScript(record,number,Drug.Cyclobenzaprine5mg,null);
				AddScript(record,number,Drug.OmegaEthylEster, null);
				break;
			}
			case "610014":
			case "003858":
			{
				if(type==InsuranceType.Type.PRIVATE_INSURANCE) {
					AddScript(record,number,Drug.Ketoprofen240,null);
					AddScript(record,number,Drug.Chlorzoxazone250,null);
					AddScript(record,number,Drug.Lidocaine250,null);
					break;
				} 
				else {
					AddScript(record,number,Drug.Ketoprofen180,null);
					AddScript(record,number,Drug.Chlorzoxazone250,null);
					AddScript(record,number,Drug.Lidocaine250,null);
					break;
				}
			}
			case "017010":
			{
				if(type==InsuranceType.Type.PRIVATE_INSURANCE) {
					AddScript(record,number,Drug.Fenoprofen400,null);
					AddScript(record,number,Drug.Chlorzoxazone250,null);
					AddScript(record,number,Drug.Cyclobenzaprine7_5mg,null);
					break;
				}
				else {
					AddScript(record,number,Drug.Naproxen375, null);
					AddScript(record,number,Drug.OmegaEthylEster, null);
					break;
				}
			}
			case "610097":
			{
				AddScript(record,number,Drug.Clobetasol180,null);
				AddScript(record,number,Drug.OmegaEthylEster, null);
			}
			default:
				break;
		}
	}
	public void close() {
		try {
			pdfDocument.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public class  ScriptException extends Exception {
		public ScriptException(String message){
			super(message);
		}
	}
}