package ru.mdmn.telsscore.hibernate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;


import ru.mdmn.telsscore.action.SiteXupypr;
import ru.mdmn.telsscore.core.Loc;
import ru.mdmn.telsscore.core.Naptr;
import ru.mdmn.telsscore.core.TelSite;
import ru.mdmn.telsscore.core.Txt;
import ru.mdmn.telsscore.core.TxtPair;

public class SearchDBOperations {

	static Logger log = Logger.getRootLogger();// getLogger(SearchDBOperations.class);

	public SearchDBOperations() {
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static void updateObj(Object obj) {

		Session ses = HibernateUtil.getSessionFactory().openSession();
		try {

			synchronized (obj) {
				ses.beginTransaction();
				ses.update(obj);
				ses.flush();
				ses.getTransaction().commit();

			}
		} catch (Exception e) {
			if(ses.getTransaction().isActive())
				ses.getTransaction().rollback();
			e.printStackTrace();
		} finally {
			ses.close();
		}

	}

	public static void saveObj(Object obj) {

		Session ses = HibernateUtil.getSessionFactory().openSession();
		try {

			synchronized (obj) {
				ses.beginTransaction();
				ses.save(obj);
				ses.flush();
				ses.getTransaction().commit();

			}
		} catch (Exception e) {
			ses.getTransaction().rollback();
			e.printStackTrace();
		} finally {
			ses.close();
		}

	}
	
	public static void putSite(TelSite site) {
		Session ses = null;
		try {

			ses = HibernateUtil.getSessionFactory().openSession();
			List<Naptr> naptrs = site.getNaptrRecords();
			ses.beginTransaction();
			if(naptrs!=null){
				for (Naptr naptr : naptrs) {
					ses.save(naptr);
				}
			}

			Loc loc = site.getLoc();
			if (loc != null)
				ses.save(loc);

			// updateObj(site);
			ses.getTransaction().commit();

			List<Txt> txts = site.getTxtRecords();
			if (txts != null) {
				List<TxtPair> txtPairs;
				for (Txt txt : txts) {

					try {
						txtPairs = txt.getPairs();
						for (TxtPair txtPair : txtPairs) {
							try {
							ses = HibernateUtil.getSessionFactory().openSession();
							ses.beginTransaction();

							ses.save(txtPair);
							ses.getTransaction().commit();
						} catch (Exception e) {
							log.error(e, e);
							ses.getTransaction().rollback();
						}
						}
						ses = HibernateUtil.getSessionFactory().openSession();
						ses.beginTransaction();
						ses.save(txt);
						ses.getTransaction().commit();
					} catch (Exception e) {
						log.error(e, e);
						ses.getTransaction().rollback();
					}finally{
						if (ses.isOpen())
							ses.close();
						
					}
				}
			}

			ses = HibernateUtil.getSessionFactory().openSession();
			ses.beginTransaction();
			ses.save(site);
			// ses.flush();
			ses.getTransaction().commit();
		} catch (Exception e) {
			log.error(e, e);
			ses.getTransaction().rollback();
		} finally {
			if (ses.isOpen())
				ses.close();
			// HibernateUtil.getSessionFactory().close();
		}

	}

	public List<TelSite> getSites() {

		List<TelSite> sites = new ArrayList<TelSite>();

		Session ses = null;
		try {

			ses = HibernateUtil.getSession();
			ses.beginTransaction();

			sites = ses.createQuery("from TelSite").setMaxResults(50).list();
		} catch (Exception e) {
			log.error(e, e);
		} finally {
			// if (ses.isOpen())
			// ses.close();
			// HibernateUtil.getSessionFactory().close();
		}
		return sites;
	}

	public TelSite getSiteByName(String domain) {

		TelSite site = null;
		domain=domain.trim().replace(' ', '-');

		Session ses = null;
		List sites;
		try {

			ses = HibernateUtil.getSessionFactory().getCurrentSession();
			ses.beginTransaction();
			sites = ses.createQuery("from TelSite as site where lower(site.name)=?")
					.setString(0, domain).setMaxResults(1).list();
			if (sites != null && sites.size() > 0) {
				site = (TelSite) sites.get(0);
				// System.out.println(site.getNaptrRecords());
			}
		} catch (Exception e) {
			log.error(e, e);
		} finally {

			/*
			 * if (ses != null && ses.isOpen()) ses.close();
			 */// HibernateUtil.getSessionFactory().close();
		}
		return site;
	}

	
	public static List<TelSite> searchLucene(String queryStr){
	//	HibernateUtil.SearchIndexer();
		Session session = HibernateUtil.getSessionFactory().openSession();
		FullTextSession fullTextSession =
			Search.getFullTextSession(session);
		//fullTextSession.beginTransaction();
			Transaction tx = fullTextSession.beginTransaction();
			// create native Lucene query
			String[] fields = new String[]{ "name",
					"naptrRecords.additional", "naptrRecords.type","naptrRecords.data",
					"txtRecords.txtRec",
					"txtRecords.txtRec.pairs.val"
			};
//			String[] fields = new String[]{"name" 
//			};
			Analyzer analyzer=fullTextSession.getSearchFactory().getAnalyzer("urlAnalyzer");
			Analyzer mainAnalyzer=fullTextSession.getSearchFactory().getAnalyzer("mainAnalyzer");
			PerFieldAnalyzerWrapper pfaw = new PerFieldAnalyzerWrapper(   mainAnalyzer);
			pfaw.addAnalyzer("name", analyzer);
			
			MultiFieldQueryParser parser = new MultiFieldQueryParser(fields, pfaw);
			
			org.apache.lucene.search.Query query;
			try {
				query = parser.parse( queryStr	);
			// wrap Lucene query in a org.hibernate.Query
			org.hibernate.Query hibQuery =
			fullTextSession.createFullTextQuery(query, TelSite.class);
			// execute search
			ArrayList<TelSite> result = (ArrayList<TelSite>) hibQuery.list();
			boolean delete;
			tx.commit();
			TelSite telSite; 
			for(int i=0; i<result.size();i++){
				
				System.out.println(result.get(i));
				telSite=result.get(i);
				delete=false;
//				if(telSite.getNaptrRecords()==null||telSite.getNaptrRecords().size()==0)delete=true;
//				if(telSite.getTxtMap()==null||telSite.getTxtMap().size()==0)delete=true;
//				if(telSite.getTelLinks()==null||telSite.getTelLinks().size()==0)delete=true;
				
				if((telSite.getNaptrRecords()==null||telSite.getNaptrRecords().size()==0)&&
				(telSite.getTxtMap()==null||telSite.getTxtMap().size()==0)&&
				(telSite.getTelLinks()==null||telSite.getTelLinks().size()==0))
					delete=true;

				if(delete)result.remove(telSite);
				
			}
//			for (TelSite telSite : result) {
//				System.out.println(telSite.getName());
//			}
//			
		//	tx.commit();
			//session.close();
			return result;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
	}
	
	
	
	public List<TelSite> search(String query, int start, int size) {

		query = "%" + query + "%";

		Session ses = null;
		List<TelSite> sites = new ArrayList<TelSite>();
		HashSet<TelSite> set = new HashSet<TelSite>();
		List<TelSite> results;
		try {
			results = searchInDomainName(query, start, size);
			if (results != null) {
				set.addAll(results);
				// sites.addAll(results);
			}
			int newSize = size - set.size();
			if (newSize <= 0)
				return sites;

			results = searchInNaptrs(query, start, newSize);
			if (results != null) {
				set.addAll(results);
				// sites.addAll(results);
			}
			newSize = size - set.size();
			if (newSize <= 0)
				return sites;

			results = searchInTxt(query, start, size);
			if (results != null) {
				set.addAll(results);
				// sites.addAll(results);
			}
		} catch (Exception e) {
			log.error(e, e);
		} finally {

			if (ses != null && ses.isOpen())
				ses.close();
			// HibernateUtil.getSessionFactory().close();
		}
		// return sites;
		return new ArrayList<TelSite>(set);// .toArray();
	}

	private List<TelSite> searchInNaptrs(String query, int start, int size) {

		Session ses = null;
		query=query.trim().replace(' ', '-');
		List<TelSite> sites = new ArrayList<TelSite>();
		try {

			ses = HibernateUtil.getSessionFactory().getCurrentSession();
			ses.beginTransaction();
			List<Naptr> naptrs = ses
					.createQuery(
							"from Naptr as rec where (lower(rec.data) like :search or lower(rec.type) like :search)")
					.setString("search", query).setMaxResults(size)
					.setFirstResult(start).list();
			if (naptrs == null)
				return sites;
			for (Naptr naptr : naptrs) {
				TelSite site = naptr.getTelsite();
				System.out.println(naptr.getTelsite());
				sites.add(naptr.getTelsite());
			}
		} catch (Exception e) {
			log.error(e, e);
		} finally {

			// if(ses!=null&&ses.isOpen())ses.close();
			// HibernateUtil.getSessionFactory().close();
		}
		return sites;
	}

	private List<TelSite> searchInTxtPair(String query, int start, int size) {

		Session ses = null;
		List<TelSite> sites = new ArrayList<TelSite>();
		try {

			ses = HibernateUtil.getSessionFactory().openSession();
			ses.beginTransaction();
			List<TxtPair> txtPairs = ses.createQuery(
					"select distinct pair from TxtPair pair where lower(pair.val) like ?").setString(0,
					query).setMaxResults(size).setFirstResult(start).list();

			for (TxtPair pair : txtPairs) {
				sites.add(pair.getTxt().getTelsite());
			}
			ses.getTransaction().commit();
		} catch (Exception e) {
			log.error(e, e);
			ses.getTransaction().rollback();
		} finally {
			
			// if(ses!=null&&ses.isOpen())ses.close();
			// HibernateUtil.getSessionFactory().close();
		}
		return sites;
	}

	private List<TelSite> searchInTxt(String query, int start, int size) {

		Session ses = null;
		List<TelSite> sites = new ArrayList<TelSite>();
		try {

			ses = HibernateUtil.getSessionFactory().openSession();
			ses.beginTransaction();
			List<Txt> txts = ses.createQuery(
					"select distinct txt from Txt txt where lower(txt.txtRec) like ?").setString(0,
					query).setMaxResults(size).setFirstResult(start).list();

			for (Txt txt : txts) {
				if(txt.getTelsite()!=null)
					sites.add(txt.getTelsite());
			}
		} catch (Exception e) {
			log.error(e, e);
		} finally {
			ses.getTransaction().commit();
			// if(ses!=null&&ses.isOpen())ses.close();
			// HibernateUtil.getSessionFactory().close();
		}
		return sites;
	}
	
	private List<TelSite> searchInDomainName(String query, int start, int size) {

		Session ses = null;
		query=query.trim().replace(' ', '-');

		List<TelSite> sites = null;
		try {

			ses = HibernateUtil.getSessionFactory().getCurrentSession();
			ses.beginTransaction();
			sites = ses.createQuery(
					"from TelSite as site where site.name like ?").setString(0,
					query).setMaxResults(size).setFirstResult(start).list();
		} catch (Exception e) {
			log.error(e, e);
		} finally {

			// if(ses!=null&&ses.isOpen())ses.close();
			// HibernateUtil.getSessionFactory().close();
		}
		return sites;
	}

}
