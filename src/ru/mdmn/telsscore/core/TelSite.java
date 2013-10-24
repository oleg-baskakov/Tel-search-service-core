package ru.mdmn.telsscore.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.solr.analysis.LowerCaseFilterFactory;
import org.apache.solr.analysis.SnowballPorterFilterFactory;
import org.apache.solr.analysis.StandardFilterFactory;
import org.apache.solr.analysis.StandardTokenizerFactory;
import org.apache.solr.analysis.WordDelimiterFilterFactory;
import org.hibernate.annotations.FilterDefs;

import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.AnalyzerDef;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Parameter;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.annotations.TokenFilterDef;
import org.hibernate.search.annotations.TokenizerDef;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import ru.mdmn.telsscore.action.TxtMaster;
import ru.mdmn.telsscore.hibernate.SearchDBOperations;

@Entity
@Indexed
@AnalyzerDef(name = "urlAnalyzer", tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class), filters = {
		@TokenFilterDef(factory = WordDelimiterFilterFactory.class, params = @Parameter(name = "generateWordParts", value = "1")),

		@TokenFilterDef(factory = LowerCaseFilterFactory.class),
		@TokenFilterDef(factory = StandardFilterFactory.class),
		@TokenFilterDef(factory = SnowballPorterFilterFactory.class, params = @Parameter(name = "language", value = "English")) })
@Root
public class TelSite implements Serializable {
	// @Parameter( name="language", value="English")

	@Column
	@Id
	@DocumentId
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@OneToMany
	@IndexedEmbedded
	private List<Naptr> naptrRecords;

	@OneToMany
	@IndexedEmbedded
	private List<Txt> txtRecords;

	@Column
	private String url;

	@Column
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date lastUpdate;

	@Column
	@Field(index = Index.TOKENIZED, store = Store.YES)
	@Analyzer(definition = "urlAnalyzer")
	@Attribute(name = "domainName")
	private String name;

	@Column
	private String ip;

	@OneToOne
	private Loc loc;

	private int flag;

	public TelSite() {
		naptrRecords=new ArrayList<Naptr>();
		txtRecords=new ArrayList<Txt>();
	}

	public HashMap<String, String> getTxtMap() {

		HashMap<String, String> map = new HashMap<String, String>();
		List<TxtPair> pair;
		String val;
		for (Txt txt : txtRecords) {
			if (txt.getType() == TxtType.TKW) {
				pair = txt.getPairs();
				for (TxtPair txtPair : pair) {
					if ("ft".equals(txtPair.getKey())) {
						val = map.get("ft");
						if (val != null) {
							val += txtPair.getVal();
						} else
							map.put(txtPair.getKey(), txtPair.getVal());
					} else
						map.put(txtPair.getKey(), txtPair.getVal());
				}
			}
		}

		return map;
	}

	public String getTxtIntro() {

		for (Txt txt : txtRecords) {
			if (txt.getType() == TxtType.PLAIN) {
				return txt.getTxtRec();
			}
		}

		return null;
	}

	public List<String> getFreeTxt() {

		List<String> result = null;

		Txt txt;
		boolean first = true;
		if (txtRecords != null && txtRecords.size() > 1) {
			result = new ArrayList<String>();
			for (int i = 0; i < txtRecords.size(); i++) {

				txt = txtRecords.get(i);
				if (txt.getType() == TxtType.PLAIN) {
					if (first) {
						first = false;
					} else
						result.add(txt.getTxtRec());
				}
			}
		}

		return result;
	}

	public List<Naptr> getNaptrRecords() {
		return naptrRecords;
	}

	public List<Naptr> getTelLinks() {

		List<Naptr> telLinks = new ArrayList<Naptr>();
		Naptr naptr;
		int posDomainName;
		for (int i = 0; i < naptrRecords.size(); i++) {
			naptr = naptrRecords.get(i);
			if (naptr.additional.length() > 1) {

				if ((posDomainName = naptr.additional.indexOf(name)) > 0) {
					naptr.data = naptr.additional.substring(0, posDomainName - 1);
					naptr.data = naptr.data.replaceAll("-", " ");
				}
				telLinks.add(naptr);
			}
		}
		return telLinks;
	}

	public void setNaptrRecords(List<Naptr> naptrRecords) {
		this.naptrRecords = naptrRecords;
	}

	public void addNaptrRecord(Naptr naptr) {
		if (naptrRecords == null)
			naptrRecords = new ArrayList<Naptr>();
		naptrRecords.add(naptr);
	}

	public void addTxtRecord(Txt txt) {
		if (txtRecords== null)
			txtRecords= new ArrayList<Txt>();
		txtRecords.add(txt);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public int getFlag() {
		return flag;
	}

	public List<Txt> getTxtRecords() {
		return txtRecords;
	}

	public void setTxtRecords(List<Txt> txtRecords) {
		this.txtRecords = txtRecords;
	}

	public Loc getLoc() {
		return loc;
	}

	public void setLoc(Loc loc) {
		this.loc = loc;
	}

	
	public void addTitle(String title){
		Txt txt = TxtMaster.createLabel(title);
		SearchDBOperations.saveObj(txt);
		txtRecords.add(txt);
		
	} 

	
	
	public void addAvatar(String url){
		Naptr naptr = NaptrType.createAvatar(url);
		SearchDBOperations.saveObj(naptr);		
		addNaptrRecord(naptr);
	}

	public void addTelLinkWithLabel(String link, String label,	int order, int pref){
		 
		Naptr naptr = NaptrType.createTelLink(link, order, pref);
		addNaptrRecord(naptr);
		Txt txt = TxtMaster.createLabelForRec(label, order, pref);
		addTxtRecord(txt);
	}
	
	
	public String getIds() {

		String ids = "";
		if (txtRecords != null) {
			for (Txt txt : txtRecords) {
				ids += "t" + txt.getId() + " ";
			}
		}
		if (naptrRecords != null) {

			for (Naptr naptr : naptrRecords) {
				ids += "n" + naptr.getId() + " ";
			}
		}
		if (loc != null)
			ids += loc.getId();

		return ids;
	}

}
