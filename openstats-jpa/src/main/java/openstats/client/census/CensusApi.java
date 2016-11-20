package openstats.client.census;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


/**
 * <pre>
 * OpenStates API. This is a fully static class so that
 * it gets initialized once and then any API class 
 * will not have to initialize it again. This
 * class is not thread-safe and is generally not for public
 * consumption. Acceptable methods are
 * 
 * getCache()
 * setCache()
 * suspendCache()
 * 
 * See below.
 *</pre>
 *
 */
public class CensusApi {
	private static final Logger logger = Logger.getLogger(CensusApi.class.getName());
	public static final String apikeyKey = "apikey";
	public static final String apiServer = "api.census.gov/data/2012/acs5";
	private static String apiKey;
	public static SimpleDateFormat dateFormat;
//	public static TreeMap<String, TreeNode> pluses;
//	public static TreeMap<String, TreeNode> newFields;

    /**
     * Initialize the API. Called instead of a constructor.
     *
     * @param bundle the bundle
	public CensusApi(ResourceBundle bundle) throws Exception {
		// API not needed for testing
		if ( !bundle.containsKey(apikeyKey)) throw new RuntimeException( "No apikey found in openstates.properties");
		apiKey = bundle.getString(apikeyKey);
		if ( apiKey == null ) throw new RuntimeException("apikey not set in openstates.properties");
		dateFormat = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
	}
     */


	/**
	 * Handles the actual API calls and caching.
	 * 
	 *  This is part of a static class and therefore is not thread-safe. This method 
	 *  is not for public consumption.
	 *   
	 */
	public List<List<String>> query(Map<String, String> argMap) throws RuntimeException {
		BufferedReader reader = null;
		HttpURLConnection conn = null;
		String charSet = "utf-8";
		try {
			conn = CensusApi.getConnectionFromAPI(argMap);
/*			
			charSet = getCharset(conn);
		    // better check it first
			int rcode = conn.getResponseCode();
		    if ( rcode / 100 != 2) {
		    	String msg = conn.getResponseMessage();
		    	conn.disconnect();
		    	throw new RuntimeException(rcode+":"+msg);
		    }
			reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), charSet));
*/			
        	return readValue( reader);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if ( conn != null ) conn.disconnect();
			if ( reader != null ) {
				try {
					reader.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}
	
	private List<List<String>> readValue(BufferedReader reader) throws IOException {
		return new ArrayList<List<String>>();
/*		
		StringWriter writer = new StringWriter();
		String line;
		while ((line = reader.readLine())!=null) {
			writer.write(line);
			writer.write(Character.LINE_SEPARATOR);
		}
		writer.close();
		String contents = writer.toString().trim().replace("[", "").replace("]", "");
		StringReader sReader = new StringReader(contents);
		
		List<List<String>>results = new ArrayList<List<String>>();
		CSVParser parser = CSVFormat.RFC4180.parse(sReader);
		List<CSVRecord> records = parser.getRecords();
		for ( CSVRecord record: records ) {
			Iterator<String> cIt = record.iterator();
			ArrayList<String> row = new ArrayList<String>();
			while(cIt.hasNext()) row.add(cIt.next());
			results.add(row);
		}
		sReader.close();
		return results;
*/		
	}
	
	private static HttpURLConnection getConnectionFromAPI(Map<String, String> argMap) throws Exception {
	    HttpURLConnection con = null;
		// There is always a method
		StringBuilder terms = new StringBuilder();
		// Iterate through the keys and their values, both are important
		if ( argMap != null ) {
			for (String key: argMap.keySet() )
			{
				String value = argMap.get(key);
				if ( value == null ) continue;
				if ( terms.length() != 0 ) terms.append('&');
				terms.append( key );
				terms.append('=' );
				terms.append( value );	// URL encoding is done by the URI constructor
			}
		}
		terms.append("&key=587b0a7bd7ac44e7ec42b799f07e5858d9254134");
			
		// construct the URI ..
		URI uri = new URI(
			"http", 
			null,
			apiServer, 
			-1,
			null, 
			terms.toString(), 
			null
		);
		logger.info(uri.toString());
		return null;
/*		
		con = (HttpURLConnection) uri.toURL().openConnection();
	    con.setRequestMethod("GET");
	    con.setRequestProperty("Content-Type", "application/xml");
	    con.connect();
	    return con;
*/	    
	}
	
	private static String getCharset(HttpURLConnection con) throws IOException {
	    String contentType = con.getHeaderField("Content-Type");
	    String charset = null;
	    for (String param : contentType.replace(" ", "").split(";")) {
	        if (param.startsWith("charset=")) {
	            charset = param.split("=", 2)[1];
	            break;
	        }
	    }
	    if ( charset == null ) {
	    	logger.fine("Defaulting to utf-8 charset");
	    	charset = "utf-8";
	    }
	    return charset;
	}
}