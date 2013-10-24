package ru.mdmn.telsscore.action;

import org.apache.log4j.Logger;

public class SiteViewCreator {

	private static SiteViewCreator instance;
	
	Logger log = Logger.getLogger(SiteViewCreator.class);
	static{
		instance=new SiteViewCreator();
	}

	
	public static SiteViewCreator getInstance(){
		return instance;
		
	}
	/*
	public void atWork(TelSite site){
		
	}
	*/
	private SiteViewCreator() {
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
