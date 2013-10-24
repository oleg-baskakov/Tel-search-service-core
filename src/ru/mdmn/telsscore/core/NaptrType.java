package ru.mdmn.telsscore.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

//import antlr.Utils;

import ru.mdmn.telsscore.action.TxtMaster;

public class NaptrType {

	public static final String REGEXP_PREF = "!^.*$!";
	public static String SIP = "sip";
	public static String H323 = "h323";
	public static String VOICETEL = "voice:tel";
	public static String SMSTEL = "sms:tel";
	public static String EMSTEL = "ems:tel";
	public static String MMSTEL = "mms:tel";
	public static String SMSMAILTO = "sms:mailto";
	public static String EMSMAILTO = "ems:mailto";
	public static String MMSMAILTO = "mms:mailto";
	public static String EMAILMAILTO = "email:mailto";
	public static String PHOTO= "x-photo:http";
	public static String NOTE= "x-note:data";
	public static String WEBHHTP = "web:http";
	public static String WEBHHTPS = "web:https";
	public static String FTFTP = "ft:ftp";
	public static String FAXTEL = "fax:tel";

	public static String XVOICE = "x-voice:";
	public static String XIM = "x-im:";
	public static String XCRIPTO = "x-cripto:data:";

	public static String LABEL_XVOICE = "Voice";
	public static String LABEL_XIM = "IM";

	public static String MS_AIM = "aim";
	public static String MS_SKYPE = "skype";
	public static String MS_GTALK = "gtalk";
	public static String MS_ICQ = "icq";
	public static String MS_YMSGR = "ymsgr";
	public static String MS_MSNIM = "msnim";
	public static String MS_XMPP = "xmpp";

	public static String XMOBILE = "x-mobile";
	public static String XHOME = "x-home";
	public static String XWORK = "x-work";
	public static String XMAIN = "x-main";
	public static String XTRANSIT = "x-transit";
	public static String XPRS = "x-prs";

	public static String XLBL = "x-lbl";

	private static HashMap<String, String> transports;
	private static HashMap<String, String> services;
	private static HashMap<String, String> actions;

	static {
		transports = new HashMap<String, String>();
		transports.put(SIP, "SIP VoIP call");
		transports.put(H323, "H.323 VoIP call");
		transports.put(VOICETEL, "Voice Call");
		transports.put(SMSTEL, "SMS Message");
		transports.put(EMSTEL, "EMS Message");
		transports.put(MMSTEL, "MMS Message");
		transports.put(SMSMAILTO, "SMS Message");
		transports.put(EMSMAILTO, "EMS Message");
		transports.put(MMSMAILTO, "MMS Message");
		transports.put(EMAILMAILTO, "Email");
		transports.put(WEBHHTP, "Web Link");
		transports.put(WEBHHTPS, "Web Link");
		transports.put(FTFTP, "FTP File Server Link");
		transports.put(FAXTEL, "Fax");

		services = new HashMap<String, String>();
		services.put(MS_AIM, "AIM");
		services.put(MS_SKYPE, "Skype");
		services.put(MS_GTALK, "Googletalk");
		services.put(MS_ICQ, "ICQ");
		services.put(MS_YMSGR, "Yahoo! Messenger");
		services.put(MS_MSNIM, "MSN");
		services.put(MS_XMPP, "XMPP");

		actions = new HashMap<String, String>();
		actions.put(XMOBILE, "Mobile");
		actions.put(XHOME, "Home");
		actions.put(XWORK, "Work");
		actions.put(XMAIN, "Main");
		actions.put(XTRANSIT, "In Transit");
		actions.put(XPRS, "Premium Rate");

	}

	public synchronized static String getLabel(String typeData) {
		String result = "";
		String[] types = typeData.split("\\+");
		String transport = "";
		String action = "";
		String label = "";
		String tmp;
		if (types != null && types.length > 0) {
			String type;
			for (int i = 0; i < types.length; i++) {
				type = types[i];
				if (transports.containsKey(type)) {
					transport = transports.get(type);
				} else if (actions.containsKey(type)) {
					if (action.length() > 0) {
						action += " And ";
					}
					action += " " + actions.get(type);
				} else if (type.startsWith(XVOICE)) {
					tmp = type.substring(XVOICE.length() - 1);
					if (transport.length() > 0)
						transport += " & " + LABEL_XVOICE;
					else {
						if (services.containsKey(tmp)) {
							transport = services.get(type) + " "+ LABEL_XVOICE;;
						} else
							transport = type;
					}
				} else if (type.startsWith(XIM)) {
					tmp = type.substring(XIM.length() - 1);
					if (transport.length() > 0)
						transport += " & " + LABEL_XIM;
					else {
						if (services.containsKey(tmp)) {
							transport = services.get(type) + " "+ LABEL_XIM;;
						} else
							transport = type;
					}

				}else if (type.startsWith(XLBL)){
					if(label.length()==0)
						label="(";

					tmp = type.substring(XLBL.length() );
					String[] labels = tmp.split(":");
					for (int j = 0; j < labels.length; j++) {
						label+=" "+labels[j];
					}
				}

			}
			if(label.length()>0)
				label+=")";
			result=action+" "+transport+" "+label;
		}
		return TxtMaster.prepareStr(result);
	}

	public synchronized static String getNaptrStr(String src) {
		
		if(src==null)return "";
		String[] data = src.split("\\:");
		String label = "";
		String tmp;
		if (data == null)return "";
		if( data.length == 1) {
			return src;
			
		}else if( data.length > 1) {
			if(data[0].equalsIgnoreCase("http"))return data[1].substring(2);
			return data[1];
		}
 
		
		return "";
		
	}
	
	public synchronized static Naptr createXLBL(String txt, int order, int pref){
		Naptr naptr=new Naptr();
		naptr.setPreference(pref);
		naptr.setRecOrder(order);
		String regexpData=REGEXP_PREF+"!";
		naptr.setRegexpData(regexpData);
		
		String type=Naptr.PREFIX+XLBL+":"+txt;
		naptr.setType(type);
		return naptr;
	}
	
	public synchronized static Naptr createPhone(String txt, String label, int order, int pref){
		Naptr naptr=new Naptr();
		naptr.setPreference(pref);
		naptr.setFlags("u");
		naptr.setRecOrder(order);
		String regexpData=REGEXP_PREF+txt+"!";
		naptr.setRegexpData(regexpData);
		String type=Naptr.PREFIX+"+"+VOICETEL;
		if(label!=null)
			type+="+"+XLBL+":"+label;
		
		naptr.setType(type);
		return naptr;
	}

	public synchronized static Naptr createAvatar(String url) {
		Naptr naptr=new Naptr();
	
		naptr.setPreference(33);
		naptr.setFlags("u");
		naptr.setRecOrder(10);
		String regexpData=NaptrType.REGEXP_PREF+url+"!";
		naptr.setRegexpData(regexpData);
		naptr.setType(Naptr.PREFIX+NaptrType.PHOTO);
		return naptr;
	}
	
	public synchronized static Naptr createNote(String text, int order, int pref) {
		Naptr naptr=new Naptr();
	
		naptr.setPreference(pref);
		naptr.setFlags("u");
		naptr.setRecOrder(order);
		if(text.length()>238)
			text=text.substring(0,238);
		String regexpData=NaptrType.REGEXP_PREF+"data:,"+text+"!";
		naptr.setRegexpData(regexpData);
		naptr.setType(Naptr.PREFIX+NaptrType.NOTE);
		return naptr;
	}
	
	
	
	public synchronized static Naptr createEmail(String email, String label, int order, int pref){
		Naptr naptr=new Naptr();
		naptr.setPreference(pref);
		naptr.setFlags("u");
		naptr.setRecOrder(order);
		String regexpData=REGEXP_PREF+email+"!";
		naptr.setRegexpData(regexpData);
		String type=Naptr.PREFIX+"+"+EMAILMAILTO;
		if(label!=null&&!"".equals(label))
			type+="+"+XLBL+":"+label;
		
		naptr.setType(type);
		return naptr;
	}
	
	public synchronized static Naptr createLink(String link, String label, int order, int pref){
		Naptr naptr=new Naptr();
		naptr.setPreference(pref);
		naptr.setFlags("u");
		naptr.setRecOrder(order);
		String regexpData=REGEXP_PREF+link+"!";
		naptr.setRegexpData(regexpData);
		String type=Naptr.PREFIX+WEBHHTP;
		//Collection<String> result;
		if(label!=null&&!"".equals(label)){
			//result=getStrs(label);
			//for (String str : result) {
				type+="+"+XLBL+":"+label;
		//	}
		}
		naptr.setType(type);
		return naptr;
	}
	
	public synchronized static Naptr createTelLink(String link, int order, int pref){
		Naptr naptr=new Naptr();
		naptr.setPreference(pref);
		naptr.setFlags("");
		naptr.setRecOrder(order);
		naptr.setRegexpData("");
		naptr.setAdditional(link+".");
		naptr.setType("");
		return naptr;
	}
	
	public static synchronized Collection<String> getStrs(String info) {
		// TODO Auto-generated method stub
		List<String> infoList = new ArrayList<String>();
		if (info == null)
			return infoList;

		int size = info.length() / 20;
		// size+=this.getInfo().length()%255>0?1:0;
		int offset;
		for (int i = 0; i < size; i++) {
			offset = i * 20;
			infoList.add(info.substring(offset, offset + 20));

		}
		offset = size * 20;
		infoList.add(info.substring(offset, info.length()));

		return infoList;
	}
	
	
}
