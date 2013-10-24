package ru.mdmn.telsscore.core;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;

import org.apache.solr.analysis.LowerCaseFilterFactory;
import org.apache.solr.analysis.SnowballPorterFilterFactory;
import org.apache.solr.analysis.StandardFilterFactory;
import org.apache.solr.analysis.StandardTokenizerFactory;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.AnalyzerDef;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Parameter;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.annotations.TokenFilterDef;
import org.hibernate.search.annotations.TokenizerDef;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import ru.mdmn.telsscore.action.TxtMaster;

@Root
@Entity
@AnalyzerDef(name="mainAnalyzer",
		tokenizer=@TokenizerDef(factory =StandardTokenizerFactory.class	),
		filters={
			@TokenFilterDef(factory=LowerCaseFilterFactory.class),
			@TokenFilterDef(factory=StandardFilterFactory.class),
			@TokenFilterDef(factory=SnowballPorterFilterFactory.class, 
					params=@Parameter(name="language", value="English")
			)
		}
	)

public class Naptr implements Serializable{

	public static String  PREFIX="E2U+";
	
	@Column
	@Id
	@GeneratedValue (strategy=GenerationType.AUTO)
	@Attribute
	long id;
	
	@Column
	@Element
	String flags;
	
	@Element(name="class")
	String classField;

	@Element
	String owner;

	@Element(name="order")
	@Column
	int recOrder;
	
	@Column
	@Element
	int preference;
	
	@Column
	@Element(name="regexp")
	String regexpData;
	
	@Column
	@Field(index=Index.TOKENIZED, store=Store.YES)
	@Analyzer(definition="mainAnalyzer")

	String additional;
	
	@Column
	@Element(name="services")
	String typeData;
	
	@Column
	long ttl;
	
	@Column
	@Field(index=Index.TOKENIZED, store=Store.YES)
	@Analyzer(definition="mainAnalyzer")
	String type;
	
	@Column
	@Field(index=Index.TOKENIZED, store=Store.YES)
	@Analyzer(definition="mainAnalyzer")
	String data;
	
	@Column
	String telId;

	@ManyToOne (cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinTable(name="TelSite_Naptr",joinColumns=@JoinColumn(name="naptrRecords_id"),
			inverseJoinColumns=@JoinColumn(name="TelSite_id"))
	TelSite telsite;
	
	public Naptr() {
		// TODO Auto-generated constructor stub

	}


public static String getPureData(String sData){
		
		String pureData="";
		String[] datas = sData.split("!");
		if(datas!=null&&datas.length>1)
			pureData=datas[datas.length-1];
		
		return TxtMaster.prepareStr(pureData);
		
	}
	
	public String getFlags() {
		return flags;
	}

	public void setFlags(String flags) {
		this.flags = flags;
	}

	public int getRecOrder() {
		return recOrder;
	}

	public void setRecOrder(int order) {
		this.recOrder = order;
	}

	public int getPreference() {
		return preference;
	}

	public void setPreference(int preference) {
		this.preference = preference;
	}


	public String getAdditional() {
		return additional;
	}

	public void setAdditional(String additional) {
		this.additional = additional;
	}


	public long getTtl() {
		return ttl;
	}

	public void setTtl(long l) {
		this.ttl = l;
	}

	public String getRegexpData() {
		return regexpData;
	}

	public void setRegexpData(String regexpData) {
		this.regexpData = regexpData;
	}

	public String getTypeData() {
		return typeData;
	}

	public void setTypeData(String typeData) {
		this.typeData = typeData;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}


	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


	public String getData() {
		return data;
	}


	public void setData(String data) {
		this.data = data;
	}


	public TelSite getTelsite() {
		return telsite;
	}


	public void setTelsite(TelSite telsite) {
		this.telsite = telsite;
	}


	public String getTelId() {
		return telId;
	}


	public void setTelId(String telId) {
		this.telId = telId;
	}

}
