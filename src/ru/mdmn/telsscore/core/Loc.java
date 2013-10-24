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
import javax.persistence.OneToOne;

@Entity
public class Loc {

	@Column
	@Id
	@GeneratedValue (strategy=GenerationType.AUTO)
	private long id;

	@Column
	private double alt;
	
	@Column
	private double hPrecision;
	@Column
	private double lat;
	@Column
	private double lon;
	@Column
	private double vPrecision;
	@Column
	private double size;
	@Column
	String telId;

	@OneToOne (cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinTable(name="TelSite_Loc",joinColumns=@JoinColumn(name="loc_id"),
			inverseJoinColumns=@JoinColumn(name="TelSite_id"))
	TelSite telsite;

	
	public Loc() {
		// TODO Auto-generated constructor stub
	}
	public Loc(double lng, double lat) {
		lon=lng;
		this.lat=lat;
	}

	public double getAlt() {
		return alt;
	}


	public void setAlt(double alt) {
		this.alt = alt;
	}


	public double getHPrecision() {
		return hPrecision;
	}


	public void setHPrecision(double precision) {
		hPrecision = precision;
	}


	public double getLat() {
		return lat;
	}


	public void setLat(double lat) {
		this.lat = lat;
	}


	public double getLon() {
		return lon;
	}


	public void setLon(double lon) {
		this.lon = lon;
	}


	public double getVPrecision() {
		return vPrecision;
	}


	public void setVPrecision(double precision) {
		vPrecision = precision;
	}


	public double getSize() {
		return size;
	}


	public void setSize(double size) {
		this.size = size;
	}


	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
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
