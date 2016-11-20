package openstats.client.census;

import java.util.ArrayList;
import java.util.List;

public class CensusTable {
	public String tableId;
	public String tableDescr;
	
	public List<StringPair> cells = new ArrayList<StringPair>();
	
	public CensusTable(String tableId, String tableDescr) {
		this.tableId = tableId;
		this.tableDescr = tableDescr;
	}

	public static class StringPair {
		public String label;
		public String descr;
		public StringPair(String label, String descr) {
			this.label = label;
			this.descr = descr;
		}		
	}

}
