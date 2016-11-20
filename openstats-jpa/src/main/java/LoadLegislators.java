
import java.util.*;

import org.openstates.bulkdata.LoadBulkData;
import org.openstates.data.Legislator;

public class LoadLegislators {
	
	public static void main(String... args) throws Exception {
		new LoadLegislators().run();
	}
	
	private void run() throws Exception {
		new LoadBulkData().loadCurrentTerm( "2014-12-01-ga-json.zip", "2013", TimeZone.getTimeZone("GMT-06:00") );
		Map<String, Legislator> legislatorMap = new TreeMap<String, Legislator>();

	}

}
