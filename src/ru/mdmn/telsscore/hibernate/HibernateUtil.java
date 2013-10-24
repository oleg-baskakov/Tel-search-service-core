package ru.mdmn.telsscore.hibernate;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;

import ru.mdmn.telsscore.action.SiteXupypr;
import ru.mdmn.telsscore.action.search.SearchProcessor;
import ru.mdmn.telsscore.core.Loc;
import ru.mdmn.telsscore.core.Naptr;
import ru.mdmn.telsscore.core.TelSite;
import ru.mdmn.telsscore.core.Txt;
import ru.mdmn.telsscore.core.TxtPair;

public class HibernateUtil {

	Logger log = Logger.getRootLogger();

	  private static  SessionFactory sessionFactory ;//=new AnnotationConfiguration().configure().buildSessionFactory();;
	 // private static  Ejb3Configuration ejb3Configuration;
	static{
	    try{
	      AnnotationConfiguration aconf = new AnnotationConfiguration()
	      //.addPackage("ru.mdmn.telsscore.core")
	      .addAnnotatedClass(TelSite.class)
	      .addAnnotatedClass(Naptr.class)
	      .addAnnotatedClass(Loc.class)
	      .addAnnotatedClass(Txt.class)
	      .addAnnotatedClass(TxtPair.class);
	      Configuration conf = aconf.configure("/hibernate-telss.cfg.xml");
	    //  conf.configure("/hibernate.cfg.xml");
	      sessionFactory = aconf.buildSessionFactory();
//	      ejb3Configuration=new Ejb3Configuration()
//	      .addAnnotatedClass(Element.class)
//	      .addAnnotatedClass(Container.class)
//	      .configure("/hibernate.cfg.xml");
	    }catch(Exception e){
	    	e.printStackTrace();

	    }
	}



	public static SessionFactory getSessionFactory() {
			return sessionFactory;
	}

	public static Session getSession() {
		
		return sessionFactory.getCurrentSession().isOpen()?sessionFactory.getCurrentSession()
				:sessionFactory.openSession();
	}

	public void hiberSample() {
		Session ses = getSessionFactory().getCurrentSession();


		//EntityManager em=ejb3Configuration.buildEntityManagerFactory().createEntityManager();
		ses.beginTransaction();
		/*
		List els = 	ses.createQuery("from Container").list();
		List elements;
		for (Iterator iterator = els.iterator(); iterator.hasNext();) {
			container = (Container) iterator.next();
			System.out.println(container.getName());
			System.out.println(container.getItems());
			elements=container.getItems();
			for (Iterator iterator2 = elements.iterator(); iterator2.hasNext();) {
				Element el = (Element) iterator2.next();
				System.out.println("\t"+el.getName());
			}

		}
		System.out.println(els.toArray().length);
		ses.getTransaction().commit();
		if(ses.isOpen())ses.close();*/
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {

		SearchIndexer();
		//searchTest();
//	test();	
//		
//	TelSite site = SearchProcessor.getSite("henri.tel");
//	List<Naptr> naptrs = site.getNaptrRecords();
//	System.out.println(naptrs);
//	System.out.println(site.getTxtRecords());
//	System.out.println(site.getIp());
	}

	private static void test() {
		Session ses = getSessionFactory().getCurrentSession();
		ses.beginTransaction();
		//List<TelSite> site = SearchProcessor.searchSites("coordinator.tel");
		
		//System.out.println(site.get(0).getIp());
		
		SiteXupypr xupypr=SiteXupypr.getInstance();
//		TelSite site = xupypr.atWork("coordinator.tel");
		TelSite site = xupypr.atWork("henri.tel");
		System.out.println(site.getNaptrRecords().size());
		//ses.beginTransaction();
		SearchDBOperations.putSite(site);
/*
		List<Naptr> naptrs = site.getNaptrRecords();
		ses.beginTransaction();
		ses.save(site);
		for (Naptr naptr : naptrs) {
			ses.save(naptr);
			
			
		}
		
//		ses.beginTransaction();
		ses.flush();
		ses.getTransaction().commit();
		ses=getSessionFactory().getCurrentSession();
		ses.beginTransaction();
		List<TelSite> res=ses.createQuery("from TelSite").list();
		for (TelSite naptr : res) {
			System.out.println(naptr.getNaptrRecords().size());
			
		}
		System.out.println(res);

		
		List<Naptr> res2=ses.createQuery("from Naptr").list();
		for (Naptr naptr2 : res2) {
			System.out.println(naptr2.getTelsite());
			
		}	
			
		System.out.println(res);
		
*/
		if(ses.isOpen())ses.close();
		
		//getSessionFactory().close();	
	}
	
	public static void SearchIndexer(){
		Session session = getSessionFactory().openSession();
		FullTextSession fullTextSession =
			Search.getFullTextSession(session);
			Transaction tx = fullTextSession.beginTransaction();
			List<TelSite> sites = session.createQuery("from TelSite as site").list();
			for (TelSite site : sites) {
				System.out.println("Indexing "+site.getName());
				fullTextSession.index(site);
				
			}
			tx.commit(); //index is written at commit time
			session.close();
	}
	
	
	public static void searchTest(){
		Session session = getSessionFactory().openSession();
		FullTextSession fullTextSession =
			Search.getFullTextSession(session);
			Transaction tx = fullTextSession.beginTransaction();
			// create native Lucene query
			String[] fields = new String[]{"name", 
					"naptrRecords.additional", "naptrRecords.type","naptrRecords.data",
					"txtRecords.txtRec",
					"txtRecords.txtRec.pairs.val"
			};
			MultiFieldQueryParser parser = new MultiFieldQueryParser(fields, new
			StandardAnalyzer());
			org.apache.lucene.search.Query query;
			try {
				query = parser.parse( "contact"	);
			// wrap Lucene query in a org.hibernate.Query
			org.hibernate.Query hibQuery =
			fullTextSession.createFullTextQuery(query, TelSite.class);
			// execute search
			List<TelSite> result = hibQuery.list();
			
			for (TelSite telSite : result) {
				System.out.println(telSite.getName());
			}
			
			tx.commit();
			session.close();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	
}
