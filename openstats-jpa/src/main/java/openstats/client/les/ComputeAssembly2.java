package openstats.client.les;

import java.math.BigDecimal;
import java.util.*;

import openstats.client.openstates.*;
import openstats.client.openstates.OpenState.BILLACTION;
import openstats.client.openstates.OpenState.BILLTYPE;
import openstats.client.util.Statistics;
import openstats.dbmodel.Result;
import openstats.model.*;
import openstats.model.District.CHAMBER;

public class ComputeAssembly2 {
//	private static final Logger logger = Logger.getLogger(ComputeAssembly2.class.getName());
	
	class AuthorStats {
		public AuthorStats() {
			billData = new long[2][];
			for ( int i=0; i<2; ++i ) {
				billData[i] = new long[4];
				for ( int j=0;j<4;++j) {
					billData[i][j] = 0;
				}
			}
		}
		public long billData[][];
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
		for ( int i=0, ie=Labels.DISTRICTCOMPUTATIONLABELS.size(); i<ie; ++i ) {
			infoItems.add( new InfoItem(Labels.DISTRICTCOMPUTATIONLABELS.get(i), Labels.DISTRICTCOMPUTATIONDESCS.get(i)));
		}
		infoItems.add( new InfoItem(Labels.ASSEMBLYCOMPUTATIONLABEL, Labels.ASSEMBLYCOMPUTATIONDESC));
		assembly.addInfoItems(infoItems);
		// skipping descriptions for the moment
		
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
//				logger.warning("District not found:"+":"+legislator.chamber+":"+ legislator.district );
				continue;
			}
			
			Legislator aLeg = district.findLegislator(legislator.full_name);
			if ( aLeg == null ) {
//				logger.warning("Legislator not found:"+legislator.full_name);
			}
			if (district.getLegislators().size() == 0 ) {
//				logger.warning("No Legislators for district:"+chamber+":"+dNum);
				continue;
			} else {
				aLeg = district.getLegislators().get(0);
			}
			
			List<Result> valueList = new ArrayList<Result>();
			valueList.add(new Result(BigDecimal.valueOf(sponsorStats.billData[0][0]), BigDecimal.valueOf(0)));
			valueList.add(new Result(BigDecimal.valueOf(sponsorStats.billData[0][3]), BigDecimal.valueOf(0)));
			valueList.add(new Result(BigDecimal.valueOf(sponsorStats.billData[1][0]), BigDecimal.valueOf(0)));
			valueList.add(new Result(BigDecimal.valueOf(sponsorStats.billData[1][1]), BigDecimal.valueOf(0)));
			valueList.add(new Result(BigDecimal.valueOf(sponsorStats.billData[1][3]), BigDecimal.valueOf(0)));
			valueList.add(new Result(BigDecimal.valueOf(sponsorStats.billData[1][2]), BigDecimal.valueOf(0)));
			aLeg.setResults(valueList);
		}
		//
		aggregateCounts(assembly);
		assembly.getResults().add(new Result(new BigDecimal("0.0000"), new BigDecimal("0")));
		//
		computeScores(assembly);
		//
//		computeSkewness(assembly);
		System.out.println(openState.getState()+":"+computeSkewness(assembly));
	}
	
	private void aggregateCounts(Assembly assembly) {
		List<Result> assemblyResults = new ArrayList<Result>();
		for ( District district: assembly.getDistrictList() ) {
			
			List<Result> districtResults = new ArrayList<Result>();
			for ( Legislator legislator: district.getLegislators() ) {
				int distCounter = 0;
				for ( Result result: legislator.getResults() ) {
					if ( districtResults.size() >= distCounter+1 ) { 
						districtResults.set(distCounter, districtResults.get(distCounter).add(result));
					} else {
						districtResults.add(new Result(result));
					}
					distCounter++;
				}
			}
			district.addResults(districtResults);
			
			int assemblyCounter = 0;
			for ( Result result: district.getResults() ) {
				if ( assemblyResults.size() >= assemblyCounter+1 ) { 
					assemblyResults.set(assemblyCounter, assemblyResults.get(assemblyCounter).add(result));
				} else {
					assemblyResults.add(new Result(result));
				}
				assemblyCounter++;
			}
		}
		if ( assemblyResults.size() == 6 ) {
			assembly.addResults(assemblyResults);
			double divisor = assemblyResults.get(2).getValue().doubleValue();
			divisor += assemblyResults.get(3).getValue().doubleValue();
			divisor += assemblyResults.get(4).getValue().doubleValue();
			divisor += assemblyResults.get(5).getValue().doubleValue();
			
			double hitcount = (assemblyResults.get(5).getValue().doubleValue() + assemblyResults.get(4).getValue().doubleValue()) / divisor; 
			if ( !(Double.isNaN(hitcount) || Double.isInfinite(hitcount)) ) {
				Result result = new Result(new BigDecimal(String.format("%.5f", hitcount)), new BigDecimal(0.0));
				assembly.addResult(result);
			}
			double score = assemblyResults.get(2).getValue().doubleValue();
			score += (assemblyResults.get(3).getValue().doubleValue() * 5.0); 
			score += ((assemblyResults.get(4).getValue().doubleValue()
					+ assemblyResults.get(5).getValue().doubleValue()) * 10.0); 
			if ( !Double.isNaN(score) ) {
				Result result = new Result(new BigDecimal(String.format("%.5f", score)), new BigDecimal(0.0));
				assembly.addResult(result);
			}
		} else {
			Result result = new Result(new BigDecimal(0.0), new BigDecimal(0.0));
			assembly.addResult(result);
			assembly.addResult(result);
			assembly.addResult(result);
			assembly.addResult(result);
			assembly.addResult(result);
			assembly.addResult(result);
			assembly.addResult(result);
			assembly.addResult(result);
		}
	}

	
	public BigDecimal computeSkewness(Assembly assembly) {
		double[] stats = new double[assembly.getDistrictList().size()];
		int idx=0;
		for ( District district: assembly.getDistrictList() ) {
			List<Result> valueList = district.getResults();
			if ( valueList.size() < 9 ) continue;
			stats[idx++] = valueList.get(8).getValue().doubleValue();
		}
		Statistics statistics = new Statistics(stats);
/*		
		List<InfoItem> infoItems = new ArrayList<InfoItem>();
		for ( int i=0, ie=Labels.ASSEMBLYCOMPUTATIONLABEL.size(); i<ie; ++i ) {
			infoItems.add( new InfoItem( Labels.ASSEMBLYCOMPUTATIONLABEL.get(i), Labels.ASSEMBLYCOMPUTATIONDESC.get(i)) );
		}
		assembly.addInfoItems(infoItems);
*/		
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
		assembly.addResult(new Result(bdSkewness, new BigDecimal(0.0)));

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
	
	public void computeScores(Assembly assembly) {
				
//		ArrayList<Long> lidsAll = makeRList();
/*		
		List<InfoItem> infoItems = new ArrayList<InfoItem>();
		for ( int i=0, ie=Labels.DISTRICTCOMPUTATIONLABEL.size(); i<ie; ++i ) {
			infoItems.add( new InfoItem( Labels.DISTRICTCOMPUTATIONLABEL.get(i), Labels.DISTRICTCOMPUTATIONDESC.get(i)) );
		}
		assembly.addInfoItems(infoItems);
*/	
		double LESMult = new Double(assembly.getDistrictList().size()/4.0);

		double[][] denomArray = new double[2][4];

		denomArray[0][0] = totalFrom(assembly, 0);
		denomArray[0][1] = 0.0;
		denomArray[0][2] = 0.0;
		denomArray[0][3] = totalFrom(assembly, 1); 
		
		denomArray[1][0] = totalFrom(assembly, 2);
		denomArray[1][1] = totalFrom(assembly, 3); 
		denomArray[1][2] = totalFrom(assembly, 4); 
		denomArray[1][3] = totalFrom(assembly, 5); 
		
		// make the array inverse cumulative across rows 
		for ( int j=0; j < 2; ++j ) {
			for ( int i=0; i < 4; ++i ) {
				double sum = 0.0;
				for ( int i2=i; i2 < 4; ++i2 ) {
					sum += denomArray[j][i2]; 
				}
				denomArray[j][i] = sum;
			}
		}

		double billsMult = 5.0;
		
		
		double[] denom = new double[4];
		denom[0] = denomArray[0][0]
				+ (billsMult * denomArray[1][0]);  

		denom[1] = denomArray[0][1]
				+ (billsMult * denomArray[1][1]);  

		denom[2] = denomArray[0][2]
				+ (billsMult * denomArray[1][2]);  
	
		denom[3] = denomArray[0][3]
				+ (billsMult * denomArray[1][3]);  

		double[][] distArray = new double[2][4];

		// Lets to Hit Rate from Mathews?
		for ( District dist: assembly.getDistrictList()) {

			List<Result> valueList = dist.getResults();
			if ( valueList.size() != 0 ) {

				double divisor = valueList.get(2).getValue().doubleValue();
				divisor += valueList.get(3).getValue().doubleValue();
				divisor += valueList.get(4).getValue().doubleValue();
				divisor += valueList.get(5).getValue().doubleValue();
				
				double hitcount = (valueList.get(5).getValue().doubleValue() + valueList.get(4).getValue().doubleValue()) / divisor; 
				if ( !(Double.isNaN(hitcount) || Double.isInfinite(hitcount)) ) {
					Result result = new Result(new BigDecimal(String.format("%.5f", hitcount)), new BigDecimal(0.0));
					dist.addResult(result);
					dist.getLegislators().get(0).addResult(result);
				}
			} else {
				continue;
			}
		}
		// let's do Legislator Score from Ellickson
		for ( District dist: assembly.getDistrictList()) {

			List<Result> valueList = dist.getResults();
			if ( valueList.size() != 0 ) {

				double score = valueList.get(2).getValue().doubleValue();
				score += (valueList.get(3).getValue().doubleValue() * 5.0); 
				score += ((valueList.get(4).getValue().doubleValue()
						+ valueList.get(5).getValue().doubleValue()) * 10.0); 
				if ( !Double.isNaN(score) ) {
					Result result = new Result(new BigDecimal(String.format("%.5f", score)), new BigDecimal(0.0));
					dist.addResult(result);
					dist.getLegislators().get(0).addResult(result);
				}
			} else {
				continue;
			}
		}

		// Do legislative effectiveness scores from Volden & Wiseman.
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
	
			} else {
				continue;
			}
				
			// make the array inverse cumulative across rows 
			for ( int j=0; j < 2; ++j ) {
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
					+ (billsMult * distArray[1][0]);

			num[1] = distArray[0][1]
					+ (billsMult * distArray[1][1]);  

			num[2] = distArray[0][2]
					+ (billsMult * distArray[1][2]);  

			num[3] = distArray[0][3]
					+ (billsMult * distArray[1][3]);  

			double partIntroduced = num[0] / denom[0];			
			double partOtherChamber = num[1] / denom[1];
			double partPassed = num[2] / denom[2];
			double partChaptered = num[3] / denom[3]; 

			double LES = (partIntroduced + partOtherChamber + partPassed + partChaptered) * LESMult;
			if ( !Double.isNaN(LES) ) {
				Result result = new Result(new BigDecimal(String.format("%.5f", LES)), new BigDecimal(0.0));
				dist.addResult(result);
				dist.getLegislators().get(0).addResult(result);
//				System.out.println(dist.getChamber()+":"+dist.getDistrict()+":"+String.format("%.5f", LES));
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

}
