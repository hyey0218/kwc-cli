package konantech.kwc.cli.common;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class CommonUtils {
	
	
	public static LocalDateTime stringToLocalDateTime(String str, String pattern, boolean timeContains) {
		LocalDateTime dateTime;
		if(timeContains)
			dateTime = stringToLocalDateTime(str,pattern);
		else
			dateTime = stringToLocalDay(str,pattern);
		return dateTime;
	}
	public static String getStackTrace(Exception e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		
		return sw.toString();
	}
	
	
	public static LocalDateTime stringToLocalDateTime(String str, String pattern) {
		pattern = StringUtils.defaultIfEmpty(pattern, "yyyy-MM-dd HH:mm:ss");
        LocalDateTime timeToMins = LocalDateTime.parse(str, DateTimeFormatter.ofPattern(pattern).withLocale(Locale.KOREA));
        return timeToMins;
    }
	public static LocalDateTime stringToLocalDay(String str, String pattern) {
		pattern = StringUtils.defaultIfEmpty(pattern, "yyyy-MM-dd");
        LocalDateTime timeToMins = LocalDate.parse(str, DateTimeFormatter.ofPattern(pattern).withLocale(Locale.KOREA)).atStartOfDay();
        return timeToMins;
    }
	
	public static String getCurrentTimeStr(String pattern) {
		pattern = StringUtils.defaultIfEmpty(pattern, "yyyy-MM-dd HH:mm:ss");
		String timeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern).withLocale(Locale.KOREA));
		return timeStr;
	}
	
	public static String getUriParamValue(String uri, String paramName) {
		UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(uri);
		MultiValueMap<String, String> map = uriComponentsBuilder.build().getQueryParams();
		return StringUtils.defaultIfEmpty(map.getFirst(paramName), "");
	}
	//path 마지막 값
	public static String getUriLastPath(String uri) {
		UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(uri);
		List<String> list = uriComponentsBuilder.build().getPathSegments();
		return list.get(list.size()-1);
	}
	//param 제거
	public static String getUriParamRemove(String uri, String paramName) {
		String[] params = paramName.split(",");
		
		UriComponents uriComp = UriComponentsBuilder.fromUriString(uri).build();
		MultiValueMap<String, String> map = uriComp.getQueryParams();

		UriComponentsBuilder tmp = UriComponentsBuilder.newInstance()
				.scheme(uriComp.getScheme())
				.host(uriComp.getHost())
				.path(uriComp.getPath());
		
		for (Map.Entry<String, List<String>> entry : map.entrySet()) {
			boolean bool = false;
			for(String str : params) {
				if(entry.getKey().equals(str))
					bool = true;
			}
			if(!bool)
				tmp.queryParam(entry.getKey(),entry.getValue());
				
		}
		return tmp.toUriString();
	}
	public static String nowDateTimeToStr(String pattern) {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern));
	}
	public static String DateTimeToStr(LocalDateTime time, String pattern) {
		return time.format(DateTimeFormatter.ofPattern(pattern));
	}
	
	/**
	 * @param text
	 * @return MD5 인코딩
	 */
	public static String getEncMd5(String text) {
		
		MessageDigest md;
		String encText = "";
		try {
			md = MessageDigest.getInstance("MD5");
	        byte[] bytes = text.getBytes(Charset.forName("UTF-8"));
	        md.update(bytes);
	        encText = Base64.getEncoder().encodeToString(md.digest());
		} catch (NoSuchAlgorithmException e) {
			encText = text;
			e.printStackTrace();
		}
		return encText;
	}
	
	public static String removeRegexPattern(String old , String pattern) {
		Pattern p = Pattern.compile(pattern, Pattern.MULTILINE);
		Matcher m = p.matcher(old);
		String retVal = new String(old);
//		while(m.find()) {
//			String match = m.group();
//			retVal = StringUtils.remove(retVal, match);
//		}
		retVal = m.replaceAll("");
		return retVal;
	}
	public static void clipCopy(String text) {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	    StringSelection selection = new StringSelection(text);
	    clipboard.setContents(selection, selection);
	}
	
	public static String getClipCopyData() {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable t = clipboard.getContents(null);
		String str="";
		try {
			if( t != null) 
				str = (String) t.getTransferData(DataFlavor.stringFlavor);
		} catch (UnsupportedFlavorException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return str;
	}
	
}
