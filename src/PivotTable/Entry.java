package PivotTable;

import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import Fax.Drug;

public class Entry {
	private class BachColumns {
		public static final int DRUG = 0;
		public static final int NDC = 2;
		public static final int PAYER = 3;
		public static final int INSURANCE_PAY = 7;
		public static final int COST = 8;
		public static final int GRP = 5;
		public static final int PCN = 6;
		public static final int BIN = 4;
	}
	private class LakeIdaColumns {
		public static final int DRUG = 14;
		public static final int NDC = 15;
		public static final int PAYER = 43;
		public static final int PROFIT = 40;
		public static final int GRP = 42;
		public static final int PCN = 39;
		public static final int BIN = 38;
	}
	String payer, bin, grp, pcn;
	String ndc,drugName,drugGroup;
	Drug drug;
	double pay,cost,profit;
	public Entry() {    
	}
	public void LoadLakeIda(String[] data) {
		setDrugName(data[LakeIdaColumns.DRUG]);
		setNdc(data[LakeIdaColumns.NDC].replaceAll("\"", "").replaceAll(",", ""));
		setDrug(data[LakeIdaColumns.NDC].replaceAll("\"", "").replaceAll(",", ""));
		setDrugGroup(data[LakeIdaColumns.NDC].replaceAll("\"", "").replaceAll(",", ""));
		setProfit(Double.parseDouble(data[LakeIdaColumns.PROFIT]));
		setBin(data[LakeIdaColumns.BIN]);
		setPcn(data[LakeIdaColumns.PCN]);
		setGrp(data[LakeIdaColumns.GRP]);
		setPayer(data[LakeIdaColumns.PAYER]);
	}
	public void LoadBachList(Row row) {
		 Iterator<Cell> cellIterator = row.cellIterator();
         while (cellIterator.hasNext()) {
        	 Cell cell = cellIterator.next();
			switch(cell.getColumnIndex()) {
		 	case BachColumns.DRUG:
		 		cell.setCellType(CellType.STRING);
		 		setDrugName(cell.getStringCellValue());
		 		break;
		 	case BachColumns.NDC:
		 		cell.setCellType(CellType.STRING);
		 		setDrugGroup(cell.getStringCellValue());
		 		setDrug(cell.getStringCellValue());
		 		setNdc(cell.getStringCellValue());
		 		break;
		 	case BachColumns.INSURANCE_PAY:
		 		setPay(cell.getNumericCellValue());
		 		break;
		 	case BachColumns.COST:
		 		setCost(cell.getNumericCellValue());
		 		break;
		 	case BachColumns.PAYER:
		 		cell.setCellType(CellType.STRING);
		 		setPayer(cell.getStringCellValue());
		 		break;
		 	case BachColumns.BIN:
		 		cell.setCellType(CellType.STRING);
		 			setBin(cell.getStringCellValue());
		 		break;
		 	case BachColumns.GRP:
		 		cell.setCellType(CellType.STRING);
		 		setGrp(cell.getStringCellValue());
		 		break;
		 	case BachColumns.PCN:
		 		cell.setCellType(CellType.STRING);
		 		setPcn(cell.getStringCellValue());
		 		break;
		 }
       }
       setProfit();
	}
	public String getPayer() {
		return payer;
	}

	public void setPayer(String payer) {
		this.payer = payer;
	}

	public String getBin() {
		return bin;
	}
	public void setBin(String bin) {
		this.bin = bin.replace(".0", "");
		switch(this.bin.length()) {
			case 5: this.bin = "0"+this.bin;
				break;
			case 4: this.bin = "00"+this.bin;
				break;
			case 3: this.bin = "000"+this.bin;
				break;
		}
	}
	
	public String getGrp() {
		return grp;
	}
	
	public void setGrp(String grp) {
		if(grp==null)
			this.grp = "";
		else
			this.grp = grp;
	}
	
	public String getPcn() {
		return pcn;
	}
	
	public void setPcn(String pcn) {
		if(pcn==null)
			this.pcn = " ";
		else
			this.pcn = pcn;
	}
	
	public String getNdc() {
		return ndc;
	}
	public void setNdc(String ndc) {
		this.ndc = ndc;
	}
	
	public String getDrugName() {
		if(drug==null)
			return this.drugName;
		else
			return drug.getName();
	}
	
	public void setDrugName(String drugName) {
		this.drugName = drugName;
	}
	public void setDrug(String ndc) {
		this.drug= Drug.GetDrugByNdc(ndc);
	}
	public Drug getDrug() {
		return this.drug;
	}
	public String getDrugGroup() {
		return drugGroup;
	}
	
	public void setDrugGroup(String ndc) {
		Drug drug = Drug.GetDrugByNdc(ndc);
		if(drug==null)
			this.drugGroup = "UNKNOWN";
		else
			this.drugGroup = drug.getTherapy();	
	}
	
	public double getPay() {
		return pay;
	}

	public void setPay(double pay) {
		this.pay = pay;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public double getProfit() {
		return profit;
	}
	public void setProfit() {
		this.profit = (this.pay-this.cost)*.65;
	}
	public void setProfit(double profit) {
		this.profit = profit;
	}
	
}
