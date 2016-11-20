package openstats.client.openstates;

import java.util.TreeSet;

public interface OpenState {
	// The unkown and other need to be on the end so that 
	// the regular types can be used as array indexes
	public enum BILLTYPE {RESOLUTION, BILL, TOPICALBILL, UNKNOWN };
	public enum BILLACTION {INTRODUCED, OTHERCHAMBER, TOGOVERNOR, ENACTED, OTHER };
//
	public String getState();
	public String getStateName();
	public String getSession();
	public BILLTYPE getBillType(org.openstates.data.Bill bill, TreeSet<String> currentTopics);
	public BILLACTION getBillAction(String chamber, String act, BILLTYPE billType);
//
	public void loadBulkData() throws Exception;
//
	public String getCensusCode();
}
	
