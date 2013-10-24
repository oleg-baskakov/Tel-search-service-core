package ru.mdmn.dnstest;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;

import org.xbill.DNS.ARecord;
import org.xbill.DNS.Address;
import org.xbill.DNS.DClass;
import org.xbill.DNS.LOCRecord;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.MXRecord;
import org.xbill.DNS.NAPTRRecord;
import org.xbill.DNS.NSRecord;
import org.xbill.DNS.Record;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.SOARecord;
import org.xbill.DNS.TXTRecord;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

public class DnsTest {

	public DnsTest() {
		
	}

	public static String[] getSoapEP(String domain){
		
		String ep="_soap._nspapi.";
		String[] eps=null;
		try{
		Lookup l= new Lookup(ep+domain, Type.ANY);
		Record[] records;
		records = l.run();
		if(records==null)return null;
		eps=new String[records.length];
		System.out.println("rec len="+records.length);
		for (int i = 0; i < records.length; i++) {
			if(records[i].getType()==Type.A){
				ARecord mx = (ARecord) records[i];
				System.out.println("Host " + mx.getAddress() + " has preference "+ mx.getName());
			}else if(records[i].getType()==Type.NAPTR){
				NAPTRRecord txt = (NAPTRRecord) records[i];
				System.out.println(" txt"+txt);
				
//				System.out.println("	flags="+txt.getFlags());
//				System.out.println("	ord  ="+txt.getOrder());
//				System.out.println("	pref  ="+txt.getPreference());
//				System.out.println("	regexp  ="+txt.getRegexp());
//				System.out.println("	add  ="+txt.getAdditionalName());
//				System.out.println("	 srvc ="+txt.getService());
//				System.out.println("	 ttl ="+txt.getTTL());
//				System.out.println("	replacement  ="+txt.getReplacement());

			}
			else if(records[i].getType()==Type.NS){
				NSRecord mx = (NSRecord) records[i];
				System.out.println("Host " + mx.rdataToString() + " has preference "+ mx.getTarget());
			}
		}

		
		} catch (TextParseException e) {
			e.printStackTrace();
		}
		
		return eps;
		
	}

	public static void main(String[] args) {
		System.out.println(URLEncoder.encode("@sdsd"));
		main2();

	}
	
	public static void getReffersFromLog(){
		String fileName="c:\\Soft\\analog_60w32\\analog 6.0\\log200910.pl";
		try {
	
			BufferedReader br=new BufferedReader(new FileReader(fileName));
			String line;
			while((line=br.readLine())!=null){
				String ip = line.substring(0, line.indexOf("- -"));
				//System.out.println(ip);
				String temp = line.substring(line.indexOf("GET")+4);
				String[] vals = temp.split("\"");
				if(!"null".equalsIgnoreCase(vals[2])&&vals[2].indexOf("bigcity.tel")<0)
					System.out.println(vals[2]);
				
			}
		String data="72.30.142.225 - - [01/Oct/2009:07:35:23 +0200] \"GET /lookup.action?domain=amsterdam.europe.bigcity.tel HTTP/1.1\" 200 4830 \"null\" \"Mozilla/5.0 (compatible; Yahoo! Slurp/3.0; http://help.yahoo.com/help/us/ysearch/slurp)\"";
		
		String ip = data.substring(0, data.indexOf("- -"));
	//	System.out.println(ip);
		String temp = data.substring(data.indexOf("GET")+4);
		String[] vals = temp.split("\"");
		for (String val : vals) {
//			System.out.println(val);
		}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	/**
	 * @param args
	 */
	public static void main2() {
		// TODO Auto-generated method stub
		try {
			
			try {
				InetAddress addr = Address.getByName("moscow.europe.bigcity.tel");
				System.out.println("addr="+addr.getHostAddress());
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Record[] records;
			
			

		Lookup l= new Lookup("Ace-Cafe-London.londonclub.tel", Type.LOC);
		Resolver resolver;
	//	l.setSearchPath(new String[]{"d0.cth.dns.nic.tel"});
		
		
		records = l.run();
		System.out.println("err="+l.getErrorString());
		//System.out.println(l.getResult());
		;
		
		Record[] answers = l.getAnswers();
		if(answers!=null){
			System.out.println("answ="+answers.length);
			for (int i = 0; i < answers.length; i++) {
				System.out.println(answers[i].getType()+" - "+answers[i].getName());
				
				if(records[i].getType()==Type.NS){
					NSRecord mx = (NSRecord) records[i];
					System.out.println("Host " + mx.rdataToString() + " has preference "+ mx.getTarget());
				}else 				if(records[i].getType()==Type.SOA){
					SOARecord mx = (SOARecord) records[i];
					System.out.println("Host " + mx.rdataToString() + " has preference "+ mx.getName());
					System.out.println("    *" + mx.getExpire() );
					System.out.println("    *" + mx.getRefresh() );
					System.out.println("    *" + mx.getAdditionalName() );
					System.out.println("    *" + mx.getAdmin() );
				}

				
			}
			
		}

		if(records==null)return;
		System.out.println("rec len="+records.length);
		for (int i = 0; i < records.length; i++) {
			if(records[i].getType()==Type.A){
				ARecord mx = (ARecord) records[i];
				System.out.println("Host " + mx.getAddress() + " has preference "+ mx.getName());
			}else if(records[i].getType()==Type.TXT){
				TXTRecord txt = (TXTRecord) records[i];
				System.out.println("txt="+txt.getStrings());
				System.out.println(txt.getTTL()+"  "+txt );
			}else if(records[i].getType()==Type.LOC){
				LOCRecord txt = (LOCRecord) records[i];
				System.out.println("lat="+txt.getLatitude());
				System.out.println("long="+txt.getLongitude());
			}else if(records[i].getType()==Type.NAPTR){
				NAPTRRecord txt = (NAPTRRecord) records[i];
				System.out.println(" "+txt);
				System.out.println("	flags="+txt.getFlags());
				System.out.println("	ord  ="+txt.getOrder());
				System.out.println("	pref  ="+txt.getPreference());
				System.out.println("	regexp  ="+txt.getRegexp());
				System.out.println("	add  ="+txt.getAdditionalName());
				System.out.println("	 srvc ="+txt.getService());
				System.out.println("	 ttl ="+txt.getTTL());
				System.out.println("	replacement  ="+txt.getReplacement());
				System.out.println();
			}
			else if(records[i].getType()==Type.NS){
				NSRecord mx = (NSRecord) records[i];
				System.out.println("Host " + mx.rdataToString() + " has preference "+ mx.getTarget());
			}
		}

		
		} catch (TextParseException e) {
			e.printStackTrace();
		}
	}

}
