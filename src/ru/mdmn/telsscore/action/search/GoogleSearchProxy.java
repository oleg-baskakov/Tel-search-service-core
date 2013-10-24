package ru.mdmn.telsscore.action.search;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class GoogleSearchProxy {

	public GoogleSearchProxy() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GoogleSearchProxy search = new GoogleSearchProxy();
		search.makeQuery("new york site:.tel",0);
		//search.makeQuery("info:http://frankmccown.blogspot.com/");
		//search.makeQuery("site:frankmccown.blogspot.com");

	}
	

	 // Put your website here
	 private final String HTTP_REFERER = "http://www.example.com/";


	 public String[] makeQuery(String query,int start) {

	  System.out.println(" Querying for " + query);
	  String[] result=null;
	  try
	  {
	   // Convert spaces to +, etc. to make a valid URL
	   query = URLEncoder.encode(query, "UTF-8");

	   URL url = new URL("http://ajax.googleapis.com/ajax/services/search/web?start="+start+"&rsz=large&v=1.0&q=" + query);
	   URLConnection connection = url.openConnection();
	   connection.addRequestProperty("Referer", HTTP_REFERER);

	   // Get the JSON response
	   String line;
	   StringBuilder builder = new StringBuilder();
	   //String builder = "";
	   BufferedReader reader = new BufferedReader(
	     new InputStreamReader(connection.getInputStream()));
	   while((line = reader.readLine()) != null) {
	    builder.append(line);
		   //builder+=line;
	   }

	   String response = builder.toString();
	   JSONObject json = JSONObject.fromObject(response);;
	   
	   System.out.println("Total results = " +
	     json.getJSONObject("responseData")
	     .getJSONObject("cursor")
	     .getString("estimatedResultCount"));

	   JSONArray ja = json.getJSONObject("responseData")
	   .getJSONArray("results");
	   
	   result=new String[ja.size()];
	   System.out.println(" Results:");
	   for (int i = 0; i < ja.size(); i++) {
	    System.out.print((i+1) + ". ");
	    JSONObject j = ja.getJSONObject(i);
	    System.out.println(j.getString("titleNoFormatting"));
	    System.out.println(j.getString("url"));
	    String domain = j.getString("url");
	    domain=domain.replaceFirst("http://", "");
	    
	    domain=domain.replaceFirst("www\\.", "");
		if(domain.endsWith("/")){
			domain=domain.substring(0, domain.length()-1);
		}
	    result[i]=domain;
	   }
	  }
	  catch (Exception e) {
	   System.err.println("Something went wrong...");
	   e.printStackTrace();
	  }
	  
	  return result;
	 }

	
	
}
