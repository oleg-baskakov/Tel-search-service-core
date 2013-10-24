/**
 * 
 */
package ru.mdmn.telsscore.action;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import de.knipp.telnic.util.dns.UnicodeConverter;


import ru.mdmn.telsscore.core.Txt;
import ru.mdmn.telsscore.core.TxtPair;
import ru.mdmn.telsscore.core.TxtType;
import ru.mdmn.telsscore.hibernate.SearchDBOperations;

/**
 * @author oleg Baskakov
 *
 */
public class TxtMaster {

	public static Txt createTxt(List<String> txts) {
		
		Txt txt = new Txt();
		
		if(txts.size()<=0)
			return null;
		String first=txts.get(0);

		if(first!=null){
			if(TxtType.REC_TKW.equalsIgnoreCase(first))
				parseTKW(txts,txt);
			else if(TxtType.REC_TSM.equalsIgnoreCase(first))
				parseTSM(txts,txt);
			else 
				parsePlain(txts,txt);
		}
		return txt;

	}

	private static void parsePlain(List<String> txts, Txt txt) {
		
		String data="";
		txt.setType(TxtType.PLAIN);
		for (String string : txts) {
			data+=string+" ";
		}
		//txt.addPair(new TxtPair("",data));
		txt.setTxtRec(prepareStr(data));
		SearchDBOperations.saveObj(txt);
		
	}

	public synchronized  static String prepareStr(String data) {
		data=data.trim();
		data=data.replaceAll("\\\\010", "\n").replaceAll("\\\\\\W", "\n");
		data=decodeUTF(data);
		
		return data.trim();
	}

	public  synchronized static String prepareStr(String data, boolean checkLen) {
		
		data=data.trim();
		if(data.length()>255)
			data=data.substring(0,255);
		data=data.replaceAll("\\\\010", "\n").replaceAll("\\\\\\W", "\n");
		data=decodeUTF(data);
		
		return data.trim();
	}
	
	
	
	public synchronized static String decodeUTF(String f) {

		int i=0;
		int pos;
		String tmp="";
		int offset;
		while(i<f.length()){
			pos=f.indexOf("\\", i);
			offset = pos+8<f.length()?pos+8:f.length();
			if(pos>=0){
				if(pos!=i)
					tmp+=f.substring(i,pos);
			//	i=pos;
				
				String utfpair = f.substring(pos+1, offset);
				String[] decs = utfpair.split("\\\\");
				if(decs!=null&&decs.length==2){
					try {
						int a=Integer.parseInt(decs[0]);
						int b=Integer.parseInt(decs[1]);
						String enc="%"+Integer.toHexString(a)+"%"+Integer.toHexString(b);
						tmp+=URLDecoder.decode(enc);
					} catch (Exception e) {
						tmp+=f.substring(pos,offset);
						e.printStackTrace();
					}
				}else{
					tmp+=f.substring(pos,offset);
				}
				i=offset;
				
			}else{
				tmp+=f.substring(i);
				i=f.length();
			}
			
		}
		return tmp;
	}

	private static void parseTSM(List<String> txts, Txt txt) {

		txt.setType(TxtType.TSM);

		if(txts.size()<2)
			return;
		txt.setVer(txts.get(1));
		if(txts.size()<=2)
			return;
		try {
			
			TxtPair txtPair;
		for (int i = 2; i < txts.size(); i+=2) {
			txtPair = new TxtPair(txts.get(i), txts.get(i+1));
			SearchDBOperations.saveObj(txtPair);

			txt.addPair(txtPair );
			
		}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void parseTKW(List<String> txts, Txt txt) {
		txt.setType(TxtType.TKW);

		if(txts.size()<2)
			return;
		txt.setVer(txts.get(1));
		if(txts.size()<=2)
			return;
		try {
			
			TxtPair txtPair;
		for (int i = 2; i < txts.size(); i+=2) {
			txtPair = new TxtPair(txts.get(i), txts.get(i+1));
			SearchDBOperations.saveObj(txtPair);

			txt.addPair(txtPair );
			
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public synchronized static String getFullNameStr(HashMap<String,String> recs){
		String result=null;
		if(recs.get("nl")!=null){
			result=getVal(recs,"","s",". ")+getVal(recs,"","fn"," ")
					+getVal(recs," '","cn","' ")+getVal(recs,"","ln"," ");
			
		}
		
		return result;
		
	}

	public synchronized static String getAddressStr(HashMap<String,String> recs){
		String result=null;
		if(recs.get("pa")!=null){
			result=getVal(recs,"","a1",", ")+getVal(recs,"","a2",", ")+getVal(recs,"","a3",", ")
					+getVal(recs,"","tc",", ")+getVal(recs,"","sp",", ")
					+getVal(recs,"","pc",", ")+getVal(recs,"","c","");
			
		}
		
		return result;
	}

	public synchronized static String getBusinessInfoStr(HashMap<String,String> recs){
		String result="";
		if(recs.get("bi")!=null){
			result=getVal(recs,"","bar",".\n")+getVal(recs,"","bsa",".\n")+getVal(recs,"","sa",". ");
			
		}
		
		return result;
	}

	
	public static  boolean hasBusinessInfo(HashMap<String,String> recs) {
		
		return recs.containsKey("bpa")||recs.containsKey("bi");
				

	}
	
	public static  boolean hasCustomInfo(HashMap<String,String> recs) {
		
		return recs.containsKey("hi")||recs.containsKey("ft");
				

	}
	public synchronized static String getVal(HashMap<String, String> recs, String prefix,
			String key,	String postix) {
		String val=recs.get(key);
		return val!=null?prefix+val+postix:"";
	}
	
	//split string to txt pairs: "tag" "info1" "tag" "info2" .. info<255 
	public static synchronized Collection<String> getStrs(String tag,String info) {

		List<String> infoList = new ArrayList<String>();
		if (info == null)
			return infoList;

		int size = info.length() / 255;
		// size+=this.getInfo().length()%255>0?1:0;
		int offset;
		for (int i = 0; i < size; i++) {
			offset = i * 255;
			infoList.add(tag);
			infoList.add(info.substring(offset, offset + 255));

		}
		offset = size * 255;
		if(offset<info.length()){
			infoList.add(tag);
			infoList.add(info.substring(offset, info.length()));
		}

		return infoList;
	}
	
	public synchronized static Txt createAddressTxt(String bName, String addr1,String addr2, String city, String code,String country) {
		Txt txt=new Txt();
		txt.setType(TxtType.TKW);
		txt.setVer("1");
		if(!empty(bName)){
			txt.addPair("bpa", bName);
		}
		if(!empty(addr1)){
			txt.addPair("a1", addr1);
		}
		if(!empty(addr2)){
			txt.addPair("a2", addr2);
		}
		if(!empty(city)){
			txt.addPair("tc", city);
		}
		if(!empty(code)){
			txt.addPair("pc", code);
		}
		if(!empty(country)){
			txt.addPair("c", country);
		}
		return txt;
			
	}
	
	
	public static boolean empty(String txtData) {
		return txtData == null || txtData.trim().length() == 0;
	}
	
	
	public synchronized static Collection<String> prepareTxt(String txt) {
		List<String> infoList=new ArrayList<String>();
		if(txt==null)return infoList;
		txt=prepareStr(txt);
		
		//int utflen=UnicodeConverter.getUTF8Length(txt);
		int size=txt.length()/220;
		//size+=this.getInfo().length()%255>0?1:0;
		int offset;
		for (int i = 0; i < size; i++) {
			offset=i*220;
			infoList.add(txt.substring(offset,offset+220));
			
		}
		offset= size*220;
		infoList.add(txt.substring(offset,txt.length()));
		
		return infoList;
	}
	
	public synchronized static Txt createLabel(String title) {
		Txt txt=new Txt();
		txt.setType(TxtType.TSM);
		txt.setVer("11");
		txt.addPair("dds",title);
		return txt;
	}	 

	public synchronized static Txt createLabelForRec(String label, int order, int pref) {
		Txt txt=new Txt();
		ArrayList<String> cols = (ArrayList<String>) TxtMaster.prepareTxt(label);
		cols.add(0, "" + pref);
		cols.add(0, "" + order);
		txt.setType(TxtType.TLB);
		txt.setVer("1");
		txt.addPair(""+order,""+pref);
		for (int i=0;i<cols.size();i+=2) {
			txt.addPair(""+cols.get(i),""+cols.get(i+1));
			
		}
		return txt;
	}	 

	
}
