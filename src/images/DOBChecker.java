package images;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class DOBChecker {
	public static boolean olderThan(String dob,int age) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
		LocalDate birthDate = LocalDate.parse(dob,formatter);
		LocalDate currentDate = LocalDate.now();
		int currentAge = Period.between(birthDate, currentDate).getYears();
		if(currentAge>=age)
			return true;
		else
			return false;
	}
}
