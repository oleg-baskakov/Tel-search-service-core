package ru.mdmn.telsscore.core;

import java.util.ArrayList;


import ru.mdmn.telsscore.action.TxtMaster;

public class AdRecord {
	
	private String title;
	private String label;
	private String uri;
	private int order;
	private String desc;
	private String position;
	
	
	private static final String AD_TAG=".tad";
	private static final String VER="1";

	public static final String POSITION_TOP="1";
	public static final String POSITION_RIGHT="2";
	public static final String POSITION_FOOT="3";
	public static final String AD_OWNER = "_ad";
	
	public AdRecord(String title, String label, String uri, String position, int order, String desc) {
		super();
		this.title = TxtMaster.prepareStr(title,true);
		this.label = TxtMaster.prepareStr(label,true);
		this.uri = uri;
		this.position= position;
		this.order = order;
		this.desc = desc;
	}
	
	public String[] getAdRecordData(){
		
		ArrayList<String> adData=new ArrayList<String>();
		adData.add(AD_TAG);
		adData.add(VER);
		adData.add(position);
		adData.add(""+order);
		adData.add(title);
		adData.add(label);
		adData.addAll(TxtMaster.getStrs("uri", uri));
		adData.addAll(TxtMaster.getStrs("desc", desc));

		String[] data;
		data = (String[]) adData.toArray(new String[adData.size()]);

		return data;
		
	}

}
