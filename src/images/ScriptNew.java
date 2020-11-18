package images;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDCheckBox;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;

import Fax.Drug;
import client.Record;

public class ScriptNew {
	PDDocument pdfDocument;
	PDDocumentCatalog docCatalog = null;
	PDAcroForm acroForm = null;
	PDFMergerUtility pdfMerger;
	File file = null;
	Record record = null;
	String login = null;
	String attention = null;
	public ScriptNew(Record record,URL source,String login) throws InvalidPasswordException, IOException, URISyntaxException {
		this.record = record;
		this.login = login;
		pdfDocument = PDDocument.load(new File(source.toURI()));
		System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");
		docCatalog = pdfDocument.getDocumentCatalog();
		acroForm = docCatalog.getAcroForm();
	}
	public void CheckDME() throws IOException {
		String[] braceList = record.getBraceList().split(",");
		StringBuilder sb = new StringBuilder();
		for(String brace: braceList) {
			System.out.println("BRACE FOUND: "+brace);
			switch(brace.trim()) {
			case "Back":
				((PDCheckBox) acroForm.getField("Back")).check();
				break;
			//Ankle
			case "Ankle":
				((PDCheckBox) acroForm.getField("Ankle")).check();
				break;
			case "Right Ankle":
				((PDCheckBox) acroForm.getField("Ankle")).check();
				sb.append(acroForm.getField("Ankle Modifier").getValueAsString()+" RT");
				acroForm.getField("Ankle Modifier").setValue(sb.toString());
				break;
			case "Left Ankle":
				((PDCheckBox) acroForm.getField("Ankle")).check();
				sb.append(acroForm.getField("Ankle Modifier").getValueAsString()+" LT");
				acroForm.getField("Ankle Modifier").setValue(sb.toString());
				break;
			//Wrist
			case "Wrist":
				((PDCheckBox) acroForm.getField("Wrist")).check();
				break;
			case "Right Wrist":
				((PDCheckBox) acroForm.getField("Wrist")).check();
				sb.append(acroForm.getField("Wrist Modifier").getValueAsString()+" RT");
				acroForm.getField("Wrist Modifier").setValue(sb.toString());
				break;
			case "Left Wrist":
				((PDCheckBox) acroForm.getField("Wrist")).check();
				sb.append(acroForm.getField("Wrist Modifier").getValueAsString()+" LT");
				acroForm.getField("Wrist Modifier").setValue(sb.toString());
				break;
			//Knees
			case "Knee":
			case "Knees":
				((PDCheckBox) acroForm.getField("Knee")).check();
				break;
			case "Right Knee":
				((PDCheckBox) acroForm.getField("Knee")).check();
				sb.append(acroForm.getField("Knee Modifier").getValueAsString()+" RT");
				acroForm.getField("Knee Modifier").setValue(sb.toString());
				break;
			case "Left Knee":
				((PDCheckBox) acroForm.getField("Knee")).check();
				sb.append(acroForm.getField("Knee Modifier").getValueAsString()+" LT");
				acroForm.getField("Knee Modifier").setValue(sb.toString());
				break;
			//SHOULDER
			case "Shoulder":
				((PDCheckBox) acroForm.getField("Shoulder")).check();
				break;
			case "Right Shoulder":
				((PDCheckBox) acroForm.getField("Shoulder")).check();
				sb.append(acroForm.getField("Shoulder Modifier").getValueAsString()+" RT");
				acroForm.getField("Shoulder Modifier").setValue(sb.toString());
				break;
			case "Left Shoulder":
				((PDCheckBox) acroForm.getField("Shoulder")).check();
				sb.append(acroForm.getField("Shoulder Modifier").getValueAsString()+" LT");
				acroForm.getField("Shoulder Modifier").setValue(sb.toString());
				break;
			
			//ELBOW
			case "Elbow":
				((PDCheckBox) acroForm.getField("Elbow")).check();
				break;
			case "Right Elbow":
				((PDCheckBox) acroForm.getField("Elbow")).check();
				sb.append(acroForm.getField("Elbow").getValueAsString()+" RT");
				acroForm.getField("Elbow Modifier").setValue(sb.toString());
				break;
			case "Left Elbow":
				((PDCheckBox) acroForm.getField("Elbow")).check();
				sb.append(acroForm.getField("Elbow").getValueAsString()+" LT");
				acroForm.getField("Elbow Modifier").setValue(sb.toString());
				break;
			//HIP
			case "Hip":
			case "Hips":
				((PDCheckBox) acroForm.getField("Hip")).check();
				break;
		}
		}
	}
	public void AddScript(Drug drug1,Drug drug2,String notes) throws InvalidPasswordException, IOException, URISyntaxException, ScriptNewException {
		pdfMerger = new PDFMergerUtility();
		pdfDocument = PDDocument.load(new File(ScriptNew.class.getClassLoader().getResource(Script.CUSTOM_SCRIPT).toURI()));
		docCatalog = pdfDocument.getDocumentCatalog();
		acroForm = docCatalog.getAcroForm();
		PopulateScript(login,drug1,drug2,notes);
		File file2 = File.createTempFile("FAX2", ".pdf");
        pdfDocument.save(file2);
        pdfMerger.setDestinationFileName(file.getAbsolutePath());
        pdfMerger.addSource(file);
        pdfMerger.addSource(file2);
        pdfMerger.mergeDocuments(null);
        file2.delete();
	}
	public void setAttention(String attention) {
		this.attention = attention;
	}
	public void PopulateDMEScript(String notes,String attention,String agent) throws ScriptNewException, IOException {
		file = File.createTempFile("FAX", ".pdf");
		PopulateScript(notes,agent);
		CheckDME();
		pdfDocument.save(file); 
	}
	public void PopulateRecord(Drug drug1,Drug drug2,String notes) throws IOException, ScriptNewException {
		//COVER PAGE
		PopulateScript(login,drug1,drug2,notes);
		file = File.createTempFile("FAX", ".pdf");
		pdfDocument.save(file); 
	}
	public void reducePDFSize() throws InvalidPasswordException, IOException {
		PDDocument doc = PDDocument.load(file);
	    Map<String, COSBase> fontFileCache = new HashMap<>();
	    for (int pageNumber = 0; pageNumber < doc.getNumberOfPages(); pageNumber++) {
	        final PDPage page = doc.getPage(pageNumber);
	        COSDictionary pageDictionary = (COSDictionary) page.getResources().getCOSObject().getDictionaryObject(COSName.FONT);
	        for (COSName currentFont : pageDictionary.keySet()) {
	            COSDictionary fontDictionary = (COSDictionary) pageDictionary.getDictionaryObject(currentFont);
	            for (COSName actualFont : fontDictionary.keySet()) {
	                COSBase actualFontDictionaryObject = fontDictionary.getDictionaryObject(actualFont);
	                if (actualFontDictionaryObject instanceof COSDictionary) {
	                    COSDictionary fontFile = (COSDictionary) actualFontDictionaryObject;
	                    if (fontFile.getItem(COSName.FONT_NAME) instanceof COSName) {
	                        COSName fontName = (COSName) fontFile.getItem(COSName.FONT_NAME);
	                        fontFileCache.computeIfAbsent(fontName.getName(), key -> fontFile.getItem(COSName.FONT_FILE2));
	                        fontFile.setItem(COSName.FONT_FILE2, fontFileCache.get(fontName.getName()));
	                    }
	                }
	            }
	        }
	    }

	    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    doc.save(baos);
	    baos.writeTo(new FileOutputStream(file));
	    
	}
	private void PopulateScript(String login,Drug drug1,Drug drug2,String notes) throws ScriptNewException {
		List<PDField> fields = acroForm.getFields();
		String fieldValue = null;
		try {
			String number = null;
			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
			Date date = new Date();
			
			for(PDField field: fields) {
				fieldValue = field.getPartialName(); 
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
						acroForm.getField("Notes").setValue(notes); 
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
						if(drug1!=null)
							acroForm.getField(field.getPartialName()).setValue(drug1.getTherapy()); 
						break;
					case "Drug 1": 
						if(drug1!=null)
							acroForm.getField(field.getPartialName()).setValue("Medication: "+drug1.getName()); 
						break;
					case "Drug Qty 1":
						if(drug1!=null)
							acroForm.getField(field.getPartialName()).setValue("Dispense: "+drug1.getQty()); 
						break;
					case "Drug Sig 1":
						if(drug1!=null)
							acroForm.getField(field.getPartialName()).setValue("Sig:  "+drug1.getSig()); 
						break;
					case "Drug Therapy 2":
						if(drug2!=null)
							acroForm.getField(field.getPartialName()).setValue(drug2.getTherapy()); 
						break;
					case "Drug 2": 
						if(drug2!=null)
							acroForm.getField(field.getPartialName()).setValue("Medication: "+drug2.getName()); 
						break;
					case "Drug Qty 2":
						if(drug2!=null)
							acroForm.getField(field.getPartialName()).setValue("Dispense: "+drug2.getQty()); 
						break;
					case "Drug Sig 2":
						if(drug2!=null)
							acroForm.getField(field.getPartialName()).setValue("Sig:  "+drug2.getSig()); 
						break;
					case "ATTENTION":
						acroForm.getField(field.getPartialName()).setValue("ATTENTION: "+attention); 
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
		} catch(IOException ex) {
			throw new ScriptNewException(ex.getMessage()+" "+fieldValue);
		}
	}
	private void PopulateScript(String notes,String agent) throws ScriptNewException {
		List<PDField> fields = acroForm.getFields();
		String fieldValue = null;
		try {
			String number = null;
			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
			Date date = new Date();
			for(PDField field: fields) {
				fieldValue = field.getPartialName(); 
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
						acroForm.getField("Notes").setValue(notes); 
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
					case "ATTENTION":
						acroForm.getField(field.getPartialName()).setValue(attention); 
						break;
					case "Dr Chase Agent":
						acroForm.getField(field.getPartialName()).setValue(agent); 
						break;
						
				}
			}
		} catch(IOException ex) {
			throw new ScriptNewException(ex.getMessage()+" "+fieldValue);
		}
	}
	public File getFile() {
		return this.file;
	}
	public class  ScriptNewException extends Exception {
		public ScriptNewException(String message){
			super(message);
		}
	}
}
