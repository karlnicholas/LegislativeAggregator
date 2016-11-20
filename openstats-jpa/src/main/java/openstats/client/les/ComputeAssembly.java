package openstats.client.les;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;

import openstats.client.openstates.*;
import openstats.client.openstates.OpenState.BILLACTION;
import openstats.client.openstates.OpenState.BILLTYPE;
import openstats.client.util.Statistics;
import openstats.dbmodel.Result;
import openstats.model.*;
import openstats.model.District.CHAMBER;

public class ComputeAssembly {

	public class AuthorStats {

		public AuthorStats() {
			billData = new long[3][];
			for ( int i=0; i<3; ++i ) {
				billData[i] = new long[4];
				for ( int j=0;j<4;++j) {
					billData[i][j] = 0;
				}
			}
		}
		
		public long billData[][];
		public int cmember = 0;
		public int cvchair = 0;
		public int cchair = 0;
		public int leader = 0;
		public int officeScore = -1;
		public double les = 0.0;
	}


	class BillAction implements Comparable<BillAction> {
		public org.openstates.data.Bill.Action action; 
		public BillAction(org.openstates.data.Bill.Action action) {
			this.action = action;
		}
		@Override
		public int compareTo(BillAction o) {
			return action.date.compareTo(o.action.date);
		}
		
	}

	private TreeSet<String> currentTopics;

	public void computeAssemblyLES(OpenState openState, Assembly assembly) throws Exception { 
		openState.loadBulkData();
		TreeMap<org.openstates.data.Legislator, AuthorStats> legislatorStats = readLegislators();
		buildcurrentTopics(openState);
		determineOfficeScores(legislatorStats);
		ArrayList<org.openstates.data.Bill.Sponsor> sponsors = new ArrayList<org.openstates.data.Bill.Sponsor>();
//		int determineCount = 0;
		Collection<org.openstates.data.Bill> bills = org.openstates.model.Bills.values();
		for ( org.openstates.data.Bill bill:  bills ) {
	//		System.out.println(bill.bill_id+"---------------------------------------");
			sponsors.clear();
			determinePrincipalSponsors(bill, sponsors);
			for ( org.openstates.data.Bill.Sponsor sponsor: sponsors ) {
				org.openstates.data.Legislator legislator = null;
				AuthorStats sponsorStats = null;
				if ( sponsor != null && sponsor.leg_id != null ) {
					legislator = org.openstates.model.Legislators.get(sponsor.leg_id);
					if ( legislator != null ) sponsorStats = legislatorStats.get(legislator);
				}
				if ( sponsorStats != null ) {
//					determineCount++;
					determineBillProgress(bill, sponsorStats, openState);
				}
	
			}
//			if ( sponsors.size() == 0 ) System.out.println("Principal Sponsor Not Found:" + bill.bill_id );
		}
//		System.out.println("Determine count = " + determineCount);
//		Group group = 
		assembly.setGroup(new Group(Labels.LESGROUPNAME, Labels.LESGROUPDESCR));
		
		List<InfoItem> infoItems = new ArrayList<InfoItem>();
		for ( int i=0, ie=Labels.DISTRICTSAGGREGATELABELS.size(); i<ie; ++i ) {
			infoItems.add( new InfoItem( Labels.DISTRICTSAGGREGATELABELS.get(i), Labels.DISTRICTSAGGREGATEDESC.get(i)) );
		}
		// skipping descriptions for the moment
		assembly.setInfoItems(infoItems);
		
		for ( org.openstates.data.Legislator legislator: legislatorStats.keySet() ) {
			AuthorStats sponsorStats = legislatorStats.get(legislator); 
			
			CHAMBER chamber;
			if ( legislator.chamber.equals("upper") ) chamber = CHAMBER.UPPER;
			else if ( legislator.chamber.equals("lower") ) chamber = CHAMBER.LOWER;
			else throw new RuntimeException("Chamber not found:" + legislator.chamber);
			String dNum = legislator.district;
			if ( dNum.length() != 3 ) {
				try {
					dNum = String.format("%03d", Integer.parseInt(dNum));
				} catch (Exception ignored ) {
					dNum = String.format("%3s", dNum);
				}
			}
			
			District district = assembly.findDistrict(chamber, dNum);
			if (district == null ) {
				district = new District(dNum, chamber);
				assembly.getDistrictList().add(district);
			} 
			
			if ( district.getResults().size() != 0 ) {
				List<Result> valueList = district.getResults();
				valueList.set(0, new Result(BigDecimal.valueOf(valueList.get(0).getValue().longValue() + sponsorStats.billData[0][0]), BigDecimal.valueOf(0)));
				valueList.set(1, new Result(BigDecimal.valueOf(valueList.get(1).getValue().longValue() + sponsorStats.billData[0][3]), BigDecimal.valueOf(0)));
				valueList.set(2, new Result(BigDecimal.valueOf(valueList.get(2).getValue().longValue() + sponsorStats.billData[1][0]), BigDecimal.valueOf(0)));
				valueList.set(3, new Result(BigDecimal.valueOf(valueList.get(3).getValue().longValue() + sponsorStats.billData[1][1]), BigDecimal.valueOf(0)));
				valueList.set(4, new Result(BigDecimal.valueOf(valueList.get(4).getValue().longValue() + sponsorStats.billData[1][2]), BigDecimal.valueOf(0)));
				valueList.set(5, new Result(BigDecimal.valueOf(valueList.get(5).getValue().longValue() + sponsorStats.billData[1][3]), BigDecimal.valueOf(0)));
				valueList.set(6, new Result(BigDecimal.valueOf(valueList.get(6).getValue().longValue() + sponsorStats.billData[2][0]), BigDecimal.valueOf(0)));
				valueList.set(7, new Result(BigDecimal.valueOf(valueList.get(7).getValue().longValue() + sponsorStats.billData[2][1]), BigDecimal.valueOf(0)));
				valueList.set(8, new Result(BigDecimal.valueOf(valueList.get(8).getValue().longValue() + sponsorStats.billData[2][2]), BigDecimal.valueOf(0)));
				valueList.set(9, new Result(BigDecimal.valueOf(valueList.get(9).getValue().longValue() + sponsorStats.billData[2][3]), BigDecimal.valueOf(0)));
			} else {
				List<Result> valueList = new ArrayList<Result>();
				valueList.add(new Result(BigDecimal.valueOf(sponsorStats.billData[0][0]), BigDecimal.valueOf(0)));
				valueList.add(new Result(BigDecimal.valueOf(sponsorStats.billData[0][3]), BigDecimal.valueOf(0)));
				valueList.add(new Result(BigDecimal.valueOf(sponsorStats.billData[1][0]), BigDecimal.valueOf(0)));
				valueList.add(new Result(BigDecimal.valueOf(sponsorStats.billData[1][1]), BigDecimal.valueOf(0)));
				valueList.add(new Result(BigDecimal.valueOf(sponsorStats.billData[1][2]), BigDecimal.valueOf(0)));
				valueList.add(new Result(BigDecimal.valueOf(sponsorStats.billData[1][3]), BigDecimal.valueOf(0)));
				valueList.add(new Result(BigDecimal.valueOf(sponsorStats.billData[2][0]), BigDecimal.valueOf(0)));
				valueList.add(new Result(BigDecimal.valueOf(sponsorStats.billData[2][1]), BigDecimal.valueOf(0)));
				valueList.add(new Result(BigDecimal.valueOf(sponsorStats.billData[2][2]), BigDecimal.valueOf(0)));
				valueList.add(new Result(BigDecimal.valueOf(sponsorStats.billData[2][3]), BigDecimal.valueOf(0)));
				district.addResults(valueList);
			}
		}
		computeLES(assembly);
		System.out.println(openState.getState()+":"+computeSkewness(assembly));
	}	

	
	public BigDecimal computeSkewness(Assembly assembly) {
		double[] stats = new double[assembly.getDistrictList().size()];
		int idx=0;
		for ( District district: assembly.getDistrictList() ) {
			List<Result> valueList = district.getResults();
			if ( valueList.size() == 0 ) continue;
			stats[idx++] = valueList.get(0).getValue().doubleValue();
		}
		Statistics statistics = new Statistics(stats);
		List<InfoItem> infoItems = new ArrayList<InfoItem>();
//		infoItems.add( new InfoItem( Labels.ASSEMBLYCOMPUTATIONLABEL, Labels.ASSEMBLYCOMPUTATIONDESC) );
		assembly.addInfoItems(infoItems);
		List<Result> valueList = new ArrayList<Result>();

		double mean = statistics.getMean();
		double variance = statistics.getVariance();
		
		double thirdmoment = 0;
		for ( double v: stats )
			thirdmoment += Math.pow((v-mean), 3.0);
		thirdmoment /= stats.length;

		// same a skew.p() in excel.
		double skewness = thirdmoment / Math.pow(variance, (3.0/2.0));

//		double skewness = (3.0*(statistics.getMean() - statistics.getMedian()))/statistics.getStdDev();
		if ( Double.isNaN(skewness)) return new BigDecimal(-1);
		BigDecimal bdSkewness = new BigDecimal(String.format("%.5f", skewness));
		valueList.add(new Result(bdSkewness, new BigDecimal(0.0))); 
//		assembly.addResults(valueList);
		return bdSkewness;
	}


	private void determinePrincipalSponsors(
		org.openstates.data.Bill bill, 
		ArrayList<org.openstates.data.Bill.Sponsor> sponsors
	) {
		for ( org.openstates.data.Bill.Sponsor sponsor: bill.sponsors ) {
// if ( !sponsor.type.toLowerCase().equals("primary") ) System.out.print(".");
			if ( sponsor.type.toLowerCase().equals("primary") ) sponsors.add(sponsor);
		}
	}
	
	private void determineBillProgress(
		org.openstates.data.Bill bill, 
		AuthorStats sponsorStats, OpenState openState
	) {
		BILLTYPE billType = openState.getBillType(bill, currentTopics);	// default resolution
		if ( billType == BILLTYPE.UNKNOWN ) throw new RuntimeException("Cannot determine bill type:" + bill.bill_id+":" + bill.title);
		
		List<BillAction> actions = new ArrayList<BillAction>();
		for ( org.openstates.data.Bill.Action action: bill.actions ) {
			actions.add(new BillAction(action));
		}
		Collections.sort(actions);

/*
if ( bill.chamber.toLowerCase().equals("upper") && billType == BILLTYPE.RESOLUTION) {
	for ( BillAction action: actions ) {
		System.out.println(bill.bill_id+":"+action.action.action);
	}
}
*/
		
		int progress = 0;
		for ( BillAction myAction: actions ) {
			String act = myAction.action.action.toLowerCase();
//			if ( bill.bill_id.contains("SR") ) System.out.println(bill.bill_id + ":" + bill.chamber+":"+act);
			BILLACTION billProgress = openState.getBillAction(bill.chamber, act, billType);
			if ( billProgress != BILLACTION.OTHER && billProgress.ordinal() > progress ) progress = billProgress.ordinal();
		}
		sponsorStats.billData[billType.ordinal()][progress]++;

	}

	private TreeMap<org.openstates.data.Legislator, AuthorStats> readLegislators() throws Exception {
		TreeMap<org.openstates.data.Legislator, AuthorStats> legislators = new TreeMap<org.openstates.data.Legislator, AuthorStats>();
		for ( org.openstates.data.Legislator legislator: org.openstates.model.Legislators.values()) {
			legislators.put(legislator, new AuthorStats());
		}
		return legislators;
	}
	
	/**
	 * 
	 * Legislative Influence: Toward Theory Development through Causal Analysis
	 * Author(s): Katherine Meyer
	 * Source: Legislative Studies Quarterly, Vol. 5, No. 4 (Nov., 1980), pp. 563-585
	 * Published
	 * 
	 * It assigned the following valueList to positions: Party Leader
	 * or Whip = 5; Committee Chair and Vice Chair simultaneously on different
	 * committees = 4; Committee Chair only = 3; two or more Committee Vice
	 * Chairs = 2; Committee Vice Chair only = 1; and Member only = 0.
	 * 
	 * Added -1 if no office held
	 * 
	 */
	private void determineOfficeScores(
		TreeMap<org.openstates.data.Legislator, AuthorStats> authorSuccess
	) {
		for ( org.openstates.data.Committee committee: org.openstates.model.Committees.values() ) {
			for ( org.openstates.data.Committee.Member member: committee.members ) {
				org.openstates.data.Legislator legislator = null;
				if ( member.leg_id != null ) legislator = org.openstates.model.Legislators.get(member.leg_id);
				if ( legislator != null ) {
					AuthorStats successStat = authorSuccess.get(legislator);
					String role = member.role.toLowerCase();
					if ( role.contains("member")) {
						successStat.cmember++;
					}
					else if ( role.contains("vice")) {
						successStat.cvchair++;
					}
					else if ( role.contains("chair") ) {
						successStat.cchair++;
//					} else { 
						// assume it's a leadership position?
//						System.out.println("Leader Role???:" + legislator + ":" + role);
//						successStat.leader++;
					}
				}
			}
		}
		// check 
		for (org.openstates.data.Legislator legislator: authorSuccess.keySet() ) {
			AuthorStats successStat = authorSuccess.get(legislator); 
			if ( successStat.cmember > 0 ) successStat.officeScore = 0;
			if ( successStat.cvchair == 1 ) successStat.officeScore = 1;
			if ( successStat.cvchair > 1 ) successStat.officeScore = 2;
			if ( successStat.cchair == 1 ) successStat.officeScore = 3;
			if ( successStat.cchair > 0 && successStat.cvchair > 0 ) successStat.officeScore = 4;
			if ( successStat.leader > 0 ) successStat.officeScore = 5;
/*
			for ( Legislator.Role role: legislator.roles ) {
				String type = role.type.toLowerCase();
				if ( !(type.contains("member") || type.contains("vice chair") || type.contains("chair")) ) {
					System.out.println("Presumed leadership?:" + role);
					successStat.officeScore = 5;
				}
			}
*/			
		}
	}
	
	public void computeLES(Assembly assembly) {
				
//		ArrayList<Long> lidsAll = makeRList();
		
		double LESMult = new Double(assembly.getDistrictList().size()/4.0);

		double[][] denomArray = new double[3][4];

		denomArray[0][0] = totalFrom(assembly, 0);
		denomArray[0][1] = 0.0;
		denomArray[0][2] = 0.0;
		denomArray[0][3] = totalFrom(assembly, 1); 
		
		denomArray[1][0] = totalFrom(assembly, 2);
		denomArray[1][1] = totalFrom(assembly, 3); 
		denomArray[1][2] = totalFrom(assembly, 4); 
		denomArray[1][3] = totalFrom(assembly, 5); 

		denomArray[2][0] = totalFrom(assembly, 6);
		denomArray[2][1] = totalFrom(assembly, 7); 
		denomArray[2][2] = totalFrom(assembly, 8); 
		denomArray[2][3] = totalFrom(assembly, 9);
		
		// make the array inverse cumulative across rows 
		for ( int j=0; j < 3; ++j ) {
			for ( int i=0; i < 4; ++i ) {
				double sum = 0.0;
				for ( int i2=i; i2 < 4; ++i2 ) {
					sum += denomArray[j][i2]; 
				}
				denomArray[j][i] = sum;
			}
		}

		double billsMult = 5.0;
		double topicMult = 10.0;
		
		
		double[] denom = new double[4];
		denom[0] = denomArray[0][0]
				+ (billsMult * denomArray[1][0])  
				+ (topicMult * denomArray[2][0]); 

		denom[1] = denomArray[0][1]
				+ (billsMult * denomArray[1][1])  
				+ (topicMult * denomArray[2][1]); 

		denom[2] = denomArray[0][2]
				+ (billsMult * denomArray[1][2])  
				+ (topicMult * denomArray[2][2]); 
	
		denom[3] = denomArray[0][3]
				+ (billsMult * denomArray[1][3])  
				+ (topicMult * denomArray[2][3]); 

		double[][] distArray = new double[3][4];

		for ( District dist: assembly.getDistrictList()) {

			List<Result> valueList = dist.getResults();
			if ( valueList.size() != 0 ) {

				distArray[0][0] = valueList.get(0).getValue().doubleValue();
				distArray[0][1] = 0.0;
				distArray[0][2] = 0.0;
				distArray[0][3] = valueList.get(1).getValue().doubleValue(); 
				
				distArray[1][0] = valueList.get(2).getValue().doubleValue();
				distArray[1][1] = valueList.get(3).getValue().doubleValue(); 
				distArray[1][2] = valueList.get(4).getValue().doubleValue(); 
				distArray[1][3] = valueList.get(5).getValue().doubleValue(); 
	
				distArray[2][0] = valueList.get(6).getValue().doubleValue();
				distArray[2][1] = valueList.get(7).getValue().doubleValue(); 
				distArray[2][2] = valueList.get(8).getValue().doubleValue(); 
				distArray[2][3] = valueList.get(9).getValue().doubleValue();
			}
				
			// make the array inverse cumulative across rows 
			for ( int j=0; j < 3; ++j ) {
				for ( int i=0; i < 4; ++i ) {
					double sum = 0.0;
					for ( int i2=i; i2 < 4; ++i2 ) {
						sum += distArray[j][i2]; 
					}
					distArray[j][i] = sum;
				}
			}
	
			double[] num = new double[4];
			num[0] = distArray[0][0]
					+ (billsMult * distArray[1][0])  
					+ (topicMult * distArray[2][0]); 

			num[1] = distArray[0][1]
					+ (billsMult * distArray[1][1])  
					+ (topicMult * distArray[2][1]); 

			num[2] = distArray[0][2]
					+ (billsMult * distArray[1][2])  
					+ (topicMult * distArray[2][2]); 

			num[3] = distArray[0][3]
					+ (billsMult * distArray[1][3])  
					+ (topicMult * distArray[2][3]); 

			double partIntroduced = num[0] / denom[0];			
			double partOtherChamber = num[1] / denom[1];
			double partPassed = num[2] / denom[2];
			double partChaptered = num[3] / denom[3]; 

			double LES = (partIntroduced + partOtherChamber + partPassed + partChaptered) * LESMult;
			List<Result> comps = new ArrayList<Result>();
			if ( !Double.isNaN(LES) ) {
				comps.add(new Result(new BigDecimal(String.format("%.5f", LES)), new BigDecimal(0.0)) );
				dist.addResults(comps);
			}
		}
	}
	
	private double totalFrom(Assembly assembly, int index) {
		double ret = 0.0;
		for ( District dist: assembly.getDistrictList()) {
			List<Result> values = dist.getResults();
			if ( values.size() != 0 ) {
				Long iVal = values.get(index).getValue().longValue();
				ret = ret + iVal;
			}
		}
		return ret;
	}

	private void buildcurrentTopics(OpenState openState) throws Exception {
		currentTopics = new TreeSet<String>(); 
		InputStream is = ComputeAssembly.class.getResourceAsStream("/topics/" + openState.getState() + "TopicBills2013.txt");
		InputStreamReader isr = new InputStreamReader(is, "ASCII");
		BufferedReader br = new BufferedReader(isr);
		String line;
		while ( (line = br.readLine()) != null ) {
			currentTopics.add(line);
		}
		is.close();
//		System.out.println(currentTopics);
	}

}
