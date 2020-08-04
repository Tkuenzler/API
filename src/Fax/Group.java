package Fax;

import javax.servlet.http.HttpServletRequest;

public class Group {
	public static String GetGroup(HttpServletRequest request) {
		String ip = request.getRemoteAddr();
		if(ip.startsWith("186.77"))
			return "Nicaragua";
		else if(ip.equalsIgnoreCase("103.224.48.123"))
			return "PADDLEPOINT";
		else if(ip.equalsIgnoreCase("66.176.0.218"))
			return "SOS";
		else if(ip.equalsIgnoreCase("43.224.189.94"))
			return "SOS";
		else if(ip.equalsIgnoreCase("122.55.250.2"))
			return "SOS";
		else if(ip.startsWith("110.54.244.118"))
			return "Vanguard";
		return "Unknown";
	}
}
