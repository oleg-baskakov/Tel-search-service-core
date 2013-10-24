package ru.mdmn.telsscore.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Store;

import ru.mdmn.telsscore.action.TxtMaster;
import ru.mdmn.telsscore.hibernate.SearchDBOperations;

@Entity
public class Txt {

	@Column
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	long id;

	@Column
	@Field(index = Index.TOKENIZED, store = Store.YES)
	@Analyzer(definition = "mainAnalyzer")
	private String txtRec;

	@Column
	private int type;
	@Column
	private String ver;

	@Column(name = "ordr")
	private int order;

	@Column
	String telId;
	
	@OneToMany
	@IndexedEmbedded
	private List<TxtPair> pairs;

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = "TelSite_Txt", joinColumns = @JoinColumn(name = "txtRecords_id"), inverseJoinColumns = @JoinColumn(name = "TelSite_id"))
	TelSite telsite;

	public Txt() {
		pairs = new ArrayList<TxtPair>(2);
		type = TxtType.PLAIN;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTxtRec() {
		return txtRec;
	}

	public void setTxtRec(String txtRec) {
		this.txtRec = txtRec;
	}

	public String getVer() {
		return ver;
	}

	public void setVer(String string) {
		this.ver = string;
	}

	public void addPair(TxtPair pair) {

		this.pairs.add(pair);
	}

	public void addPair(String key, String val) {
		TxtPair pair;
		val=TxtMaster.prepareStr(val);
		Collection<String> strs = TxtMaster.prepareTxt(val);
		
		for (String str : strs) {
			
			pair = new TxtPair(key,str);
			SearchDBOperations.saveObj(pair);
			pairs.add(pair);
			
		}
	}
	
	public List<TxtPair> getPairs() {
		return pairs;
	}

	public void setPairs(List<TxtPair> pairs) {
		this.pairs = pairs;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public TelSite getTelsite() {
		return telsite;
	}

	public void setTelsite(TelSite telsite) {
		this.telsite = telsite;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	// for plain text there is some hint - key in TxtPair is blank
	public void setPlainText(String[] txts) {
		List<TxtPair> pairs = new ArrayList<TxtPair>();
		TxtPair pair;
		for (int i = 0; i < txts.length; i++) {
			pair = new TxtPair("", txts[i]);
			SearchDBOperations.saveObj(pair);
			pairs.add(pair);
		}
		this.setPairs(pairs);
	}

	public String[] txt2StringArr(){
		String[] result=new String[1];
		
		ArrayList<String> data=new ArrayList<String>();
		if(getType()==TxtType.KEYWORD||getType()==TxtType.TKW)
			data.add(TxtType.REC_TKW);
		else if(getType()==TxtType.TSM)
			data.add(TxtType.REC_TSM);
		else if(getType()==TxtType.TLB)
			data.add(TxtType.REC_TLB);
		
		data.add(getVer());
		for (TxtPair pair : pairs) {
			
				data.add(pair.getKey());
				data.add(pair.getVal());
			
		}
		result=(String[]) data.toArray(result);
		return result;
	}
	
	public ArrayList<String> getText() {
		ArrayList<String> result = new ArrayList<String>();

		boolean firstRec = true;
		boolean plainText = false;

		for (TxtPair pair : pairs) {
			if (firstRec && pair.getKey().length() == 0) {
				plainText = true;
			}
			firstRec = false;
			if (plainText) {
				// Let's ignore every keys for plain text
				result.add(pair.getVal());
			} else {

				result.add(pair.getKey());
				result.add(pair.getVal());
			}
		}
		return result;
	}

	public static Txt createKeywordsTxt(String keywords, boolean save) {
		Txt txt = new Txt();
		txt.setType(TxtType.KEYWORD);
		txt.setVer("1");
		try {

			Collection<String> lines = TxtMaster.prepareTxt(keywords);
			List<TxtPair> pairs;
			TxtPair pair;
			pairs = new ArrayList<TxtPair>();
			
			for (String line : lines) {

				pair = new TxtPair("ft", line.trim());
				if (save)
					SearchDBOperations.saveObj(pair);

				pairs.add(pair);
			}
			txt.setPairs(pairs);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return txt;

	}

	public String getKeywords() {

		String result = "";
		if (getType() == TxtType.KEYWORD) {

			for (TxtPair pair : pairs) {
				result = pair.getVal();
			}
		}
		return result;
	}

	public String getTelId() {
		return telId;
	}

	public void setTelId(String telId) {
		this.telId = telId;
	}

}
