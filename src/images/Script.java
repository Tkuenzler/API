package images;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDCheckBox;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;

import Fax.Drug;
import client.Record;

public class Script {
	public static final String LIVE_SCRIPT = "images/Live Script.pdf";
	public static final String DR_CHASE_MIN = "images/DR_CHASE - MIN.pdf";
	public static final String DR_CHASE_MAX = "images/DR_CHASE - MAX.pdf";
	public static final String HUMANA = "images/Humana.pdf";
	public static final String CAREMARK = "images/Caremark.pdf";
	public static final String AETNA = "images/Aetna.pdf";
	public static final String OPTUM_RX = "images/OptumRx.pdf";
	public static final String INGENIO_RX = "images/IngenioRx.pdf";
	public static final String MEDIMPACT = "images/Medimpact.pdf";
	public static final String CIGNA = "images/Cigna.pdf";
	public static final String ESI = "images/ESI.pdf";
	
	URL src;
	PDDocument pdfDocument;
	Record record;
	PDDocumentCatalog docCatalog = null;
	PDAcroForm acroForm = null;
	Drug[] drugs;
	public Script(URL src) throws ScriptException, IOException, URISyntaxException {
		this.src = src;
		pdfDocument = PDDocument.load(new File(src.toURI()));
		System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");
		docCatalog = pdfDocument.getDocumentCatalog();
		acroForm = docCatalog.getAcroForm();
	}
	public void setDrugs(Drug[] drugs) {
		this.drugs = drugs;
	}
	public File PopulateScript(Record record,String login) throws ScriptException {
		try {
			String number = null;
			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
			Date date = new Date();
			//COVER PAGE
			List<PDField> fields = acroForm.getFields();
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
					if(drugs[0]!=null)
						acroForm.getField(field.getPartialName()).setValue(drugs[0].getTherapy()); 
					break;
				case "Drug 1": 
					if(drugs[0]!=null)
						acroForm.getField(field.getPartialName()).setValue("Medication: "+drugs[0].getName()); 
					break;
				case "Drug Qty 1":
					if(drugs[0]!=null)
						acroForm.getField(field.getPartialName()).setValue("Dispense: "+drugs[0].getQty()); 
					break;
				case "Drug Sig 1":
					System.out.println("DRUG SIG 1");
					if(drugs[0]!=null)
						acroForm.getField(field.getPartialName()).setValue("Sig:  "+drugs[0].getSig()); 
					break;
				case "Drug Therapy 2":
					if(drugs[1]!=null)
						acroForm.getField(field.getPartialName()).setValue(drugs[1].getTherapy()); 
					break;
				case "Drug 2": 
					if(drugs[1]!=null)
						acroForm.getField(field.getPartialName()).setValue("Medication: "+drugs[1].getName()); 
					break;
				case "Drug Qty 2":
					if(drugs[1]!=null)
						acroForm.getField(field.getPartialName()).setValue("Dispense: "+drugs[1].getQty()); 
					break;
				case "Drug Sig 2":
					System.out.println("DRUG SIG 2");
					if(drugs[1]!=null)
						acroForm.getField(field.getPartialName()).setValue("Sig: "+drugs[1].getSig()); 
					break;
				case "Drug Therapy 3":
					if(drugs[2]!=null)
						acroForm.getField(field.getPartialName()).setValue(drugs[2].getTherapy()); 
					break;
				case "Drug 3": 
					System.out.println("DRUG 3");
					if(drugs[2]!=null)
						acroForm.getField(field.getPartialName()).setValue("Medication: "+drugs[2].getName()); 
					break;
				case "Drug Qty 3":
					if(drugs[2]!=null)
						acroForm.getField(field.getPartialName()).setValue("Dispense: "+drugs[2].getQty()); 
					break;
				case "Drug Sig 3":
					if(drugs[2]!=null)
						acroForm.getField(field.getPartialName()).setValue("Sig:  "+drugs[2].getSig()); 
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
			File file = File.createTempFile("FAX", ".pdf");
			pdfDocument.save(file);
			return file;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return null;
		}  catch (IllegalArgumentException e2) {
			throw new ScriptException("Illegal Argument");
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