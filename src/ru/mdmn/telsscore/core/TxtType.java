/**
 * 
 */
package ru.mdmn.telsscore.core;

import java.util.HashMap;

/**
 * @author oleg Baskakov
 *
 */
public class TxtType {

	public static final int PLAIN = 0;
	public static final int TKW = 1;
	public static final int TSM = 2;
	public static final int TLB = 4;
	public static final int KEYWORD = 3;

	public static final String REC_TKW = ".tkw";
	public static final String REC_TSM = ".tsm";
	public static final String REC_TLB = ".tlb";

	private static HashMap keywords;
	
	static{
		keywords=new HashMap<String, String>();
		keywords.put("nl", "Name");
		keywords.put("s", "");
		keywords.put("fn", "");
		keywords.put("ln", "");
		keywords.put("", "");
		keywords.put("", "");
	}
	/*
	 *  
salutation  s  "Mr", "Mrs"
firstName fn "Adam"
 lastName  ln  "Smith"
 nickName nn  "Ade"
 
commonName
 cn
 "Addie"
 
 
 dateOfBirth 
 dob 
 "1968-02-19"
 
 
 gender 
 g
 "male", "female"
 
 
 maritalStatus 
 ms 
 "married", "single"
 
Address
 postalAddress 
 pa 
 Grouping keyword
 
addressLine[1..3]
 a[1...3]
 "8 Wilfried Street" Multiple lines/values possible
 
townCity 
 tc
  "London"
 
stateProvince 
 sp
 "Hampshire"
 
postalCode 
 pc
 "SW1E 6PL"
 
country 
 c
 "Scotland"
 
latitudeLongitude 
 ll
 Geographic latitude/longitude
 
Work
 directoryInformation
 di
 Grouping keyword
 
organization 
 o
 To be left blank for entries representing corporations
 
department 
 d
  "IT"
 
jobTitle 
 jt
 "chief executive officer"
 
 
 hobbiesInterests 
 hi
 "scuba diving"
 
 
 freeText 
 ft
 Free-text comments like "selfless, innovating, trustworthy, red hair"
 

	 * 
	 * */
	
	/**
	 * 
	 */
	public TxtType() {
		// TODO Auto-generated constructor stub
	}

}
