package ru.mdmn.telsscore.action;

import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.Address;
import org.xbill.DNS.LOCRecord;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.NAPTRRecord;
import org.xbill.DNS.NSRecord;
import org.xbill.DNS.Record;
import org.xbill.DNS.TXTRecord;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

import de.knipp.telnic.util.net.IDN;

import ru.mdmn.telsscore.core.Loc;
import ru.mdmn.telsscore.core.Naptr;
import ru.mdmn.telsscore.core.NaptrType;
import ru.mdmn.telsscore.core.TelSite;
import ru.mdmn.telsscore.core.Txt;
import ru.mdmn.telsscore.hibernate.SearchDBOperations;

public class SiteXupypr {

	private static SiteXupypr instance;

	Logger log = Logger.getRootLogger();
	static {
		instance = new SiteXupypr();
	}

	private SiteXupypr() {

	}

	public static SiteXupypr getInstance() {

		return instance;

	}

	public TelSite atWork(String url) {
		TelSite site = null;
		try {
			log.info("search for addr=" + url);

			InetAddress addr = Address.getByName(url);
			Record[] records;
			log.info("Site is found. Xupypr begin to preparation "
					+ addr.getHostName());
			site = new TelSite();
			site.setName(addr.getHostName());
			site.setIp(addr.getHostAddress());
			site.setLastUpdate(new Date());
			SearchDBOperations.saveObj(site);
			Lookup l = new Lookup(addr.getHostName(), Type.NAPTR);
			records = l.run();
			if (records != null) {
				log.info("rec len=" + records.length);

				NAPTRRecord rec;
				String service;
				for (int i = 0; i < records.length; i++) {
					if (records[i].getType() == Type.NAPTR) {
						rec = (NAPTRRecord) records[i];
						Naptr naptr = new Naptr();
						naptr.setAdditional(rec.getAdditionalName().toString());
						service = rec.getService();
						if (service.startsWith(Naptr.PREFIX)) {
							service = service.substring(Naptr.PREFIX.length());
						}
						naptr.setTypeData(service);
						naptr.setFlags(rec.getFlags());
						naptr.setRecOrder(rec.getOrder());
						naptr.setPreference(rec.getPreference());
						naptr.setRegexpData(rec.getRegexp());
						naptr.setTtl(rec.getTTL());
						naptr.setData(Naptr.getPureData(rec.getRegexp()));
						naptr.setType(NaptrType.getLabel(naptr.getTypeData()));
						SearchDBOperations.saveObj(naptr);
						
						site.addNaptrRecord(naptr);
						log.info("	* add rec:" + rec);
					}
				}
				SearchDBOperations.updateObj(site);
			}

			l = new Lookup(addr.getHostName(), Type.TXT);
			records = l.run();
			if (records != null) {

				ArrayList<Txt> txtRecs = getTxtRecords(records);
				site.setTxtRecords(txtRecs);
				SearchDBOperations.updateObj(site);
			}
			
			l = new Lookup(addr.getHostName(), Type.LOC);
			records = l.run();
			if (records != null) {

				Loc loc = getLocRecord((LOCRecord)records[0]);
				if(loc!=null){
					SearchDBOperations.saveObj(loc);
					site.setLoc(loc);
				}
				SearchDBOperations.updateObj(site);
			}
			

		} catch (UnknownHostException e) {
			log.error(e, e);

		} catch (TextParseException e) {
			log.error(e, e);
		}finally{

		}

		return site;
	}

	private Loc getLocRecord(LOCRecord record) {
		Loc loc=new Loc();
		loc.setAlt(record.getAltitude());
		loc.setHPrecision(record.getHPrecision());
		loc.setLat(record.getLatitude());
		loc.setLon(record.getLongitude());
		loc.setSize(record.getSize());
		loc.setVPrecision(record.getVPrecision());
		return loc;
	}

	private ArrayList<Txt> getTxtRecords(Record[] records) {

		ArrayList<Txt> txtRecs = new ArrayList<Txt>();
		TXTRecord rec;

		Txt txt;
		for (int i = 0; i < records.length; i++) {
			try {

				if (records[i].getType() == Type.TXT) {
					rec = (TXTRecord) records[i];

					txt = TxtMaster.createTxt(rec.getStrings());
					txt.setOrder(i);
					SearchDBOperations.saveObj(txt);

					txtRecs.add(txt);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return txtRecs;
	}

	public void onOperation(String url) {
		try {

			TelSite site = atWork(url);
			if (site != null) {
				SearchDBOperations db = new SearchDBOperations();
				//db.putSite(site);
				log.info("Site:" + url + " put in db successfully");
			}
		} catch (Exception e) {
			log.error(e, e);
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String f="\\208\\162\\209\\131 \\209 ука128\\208\\184\\208\\183\\208\\188 \\208\\184 \\208\\190\\209\\130\\208\\180\\209\\139\\209\\133\\208\\179\\208\\190" +
				"\\209\\128";
		
		int i=0;
		int pos;
		String tmp="";
		while(i<f.length()){
			pos=f.indexOf("\\", i);
			if(pos>=0){
				if(pos!=i)
					tmp+=f.substring(i,pos);
			//	i=pos;
				String utfpair = f.substring(pos+1, pos+8<f.length()?pos+8:f.length());
				String[] decs = utfpair.split("\\\\");
				if(decs!=null&&decs.length==2){
					try {
						int a=Integer.parseInt(decs[0]);
						int b=Integer.parseInt(decs[1]);
						String enc="%"+Integer.toHexString(a)+"%"+Integer.toHexString(b);
						tmp+=URLDecoder.decode(enc);
					} catch (Exception e) {
						tmp+=f.substring(pos,pos+8);
						e.printStackTrace();
					}
				}else{
					tmp+=f.substring(pos,pos+8);
				}
				i=pos+8;
				
			}else{
				tmp+=f.substring(i);
				i=f.length();
			}
			
		}
		
		String f2 = f.replaceAll("\\\\", "%");
		System.out.println(tmp);

	}

}
