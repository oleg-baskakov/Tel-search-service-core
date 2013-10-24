package ru.mdmn.telsscore.core;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;

import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Store;

import ru.mdmn.telsscore.action.TxtMaster;


@Entity
public class TxtPair {

	
	@Column
	@Id
	@GeneratedValue (strategy=GenerationType.AUTO)
	long id;

	@Column (name="skey")
	private String key;
	@Column
	@Field(index=Index.TOKENIZED, store=Store.YES)
	@Analyzer(definition="mainAnalyzer")
	private String val;

	@ManyToOne (cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinTable(name="Txt_TxtPair",joinColumns=@JoinColumn(name="pairs_id"),
			inverseJoinColumns=@JoinColumn(name="Txt_id"))
	private Txt txt;
	/**
	 * @param args
	 */
	public TxtPair(String k, String v) {
		key=k;
		val=TxtMaster.prepareStr(v);

	}

	public TxtPair() {

	}

	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getVal() {
		return val;
	}

	public void setVal(String val) {
		this.val = val;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Txt getTxt() {
		return txt;
	}

	public void setTxt(Txt txt) {
		this.txt = txt;
	}

}
