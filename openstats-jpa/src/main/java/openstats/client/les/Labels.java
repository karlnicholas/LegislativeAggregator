package openstats.client.les;

import java.util.*;

public class Labels {
	public static final String LESGROUPNAME = new String("BILLPROGRESS");
	public static final String LESGROUPDESCR = new String("Legislative Effiveness Scores based on Bill Type and Progress. (Volden, C., & Wiseman, A. E. (2009))");
	public static ArrayList<String> DISTRICTSAGGREGATELABELS = new ArrayList<String>( Arrays.asList(new String[] {
		"ResInt", "ResAdopted", 
		"BillsInt", "BillsOC", "BillsPassed", "BillsChap" 
	}));
	public static ArrayList<String> DISTRICTSAGGREGATEDESC = new ArrayList<String>( Arrays.asList(new String[] {
			"Resolutions Introduced", "Resolutions Adopted", 
			"Bills Introduced", "Bills to Other Chamber", "Bills Passed", "Bills Chaptered"
		}));
	public static ArrayList<String> DISTRICTCOMPUTATIONLABELS = new ArrayList<String>( Arrays.asList(new String[] {"HS", "LS", "LES" }));
	public static ArrayList<String> DISTRICTCOMPUTATIONDESCS = new ArrayList<String>( Arrays.asList(new String[] { "Hit Score. (Mathews 1960)", "Legislator Score. (Ellickson 1992)", "Legislator Effectiveness Score. (Volden & Wiseman 2009)"}));
	public static final String ASSEMBLYCOMPUTATIONLABEL = "Skewness";
	public static final String ASSEMBLYCOMPUTATIONDESC = "Skewness of distribution of LES scores for all districts";
}
