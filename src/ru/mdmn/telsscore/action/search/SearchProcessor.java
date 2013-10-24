package ru.mdmn.telsscore.action.search;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;

import ru.mdmn.telsscore.action.SiteXupypr;
import ru.mdmn.telsscore.core.TelSite;
import ru.mdmn.telsscore.hibernate.SearchDBOperations;

public class SearchProcessor {

	public SearchProcessor() {
		// TODO Auto-generated constructor stub
	}
	
	public static synchronized List<TelSite> searchSites(String query, int start, int size){
		List<TelSite> sites=new ArrayList<TelSite>();
		GoogleSearchProxy search = new GoogleSearchProxy();
		String[] domainsNames;
		try {
			
		domainsNames=search.makeQuery(query+" site:.tel",start);
		String domain;
		TelSite site;
		for (int i = 0; i < domainsNames.length; i++) {
			domain = domainsNames[i];
			site = getSite(domain);
			if(site==null){
				SiteXupypr.getInstance().onOperation(domain);
			}
			
		}
		SearchDBOperations db = new SearchDBOperations();
		//sites = db.search(query, start, size);
		sites = db.searchLucene(query);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return sites;
		
	}
	
	
	
	
	public static synchronized TelSite getSite(String query){
		SearchDBOperations db = new SearchDBOperations();
		TelSite site = db.getSiteByName(query);
		
		
		return site;
		
	}
	public static synchronized List<TelSite> getSites(String query){
		if (query==null) {
			return new ArrayList<TelSite>();
		}
		SearchDBOperations db = new SearchDBOperations();
		
		List<TelSite> sites=db.getSites();

		return sites;
		
	}
}
