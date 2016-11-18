package openstats.model;

import java.util.*;

import openstats.dbmodel.*;

public class District {
	public static enum CHAMBER {UPPER, LOWER};

	private String district;
	private CHAMBER chamber;
	private String description;
	private List<Result> results;
	private List<Legislator> legislators;

	public District() {}
	
	public District(String district, CHAMBER chamber) {
		this.district = district;
		this.chamber = chamber;
		description = null;
		results = new ArrayList<Result>();
		legislators = new ArrayList<Legislator>();
	}
	// empty, for templates
	public District(DBDistrict dbDistrict) {
		district = dbDistrict.getDistrict();
		chamber = dbDistrict.getChamber();
		description = dbDistrict.getDescription();
		results = new ArrayList<Result>();
		legislators = new ArrayList<Legislator>();
		for ( DBLegislator dbLegislator: dbDistrict.getLegislators() ) {
			legislators.add( new Legislator(dbLegislator));
		}
	}
	// deep copy constructor
	public District(District district) {
		this.district = district.getDistrict();
		this.chamber = district.getChamber();
		this.description = district.getDescription();
		this.results = new ArrayList<Result>(district.getResults());
		legislators = new ArrayList<Legislator>(district.getLegislators());
	}
	public void addResults(List<Result> results) {
		this.results.addAll(results);
	}
	public void addResult(Result result) {
		this.results.add(result);
	}

	public void copyGroup(DBGroup dbGroup, DBDistrict dbDistrict) {
		for ( Result Result: dbDistrict.getGroupResultsMap().get(dbGroup).getResults()) {
			results.add(Result);
		}
		for(DBLegislator dbLegislator: dbDistrict.getLegislators()) {
			Legislator legislator = findLegislator(dbLegislator);
			legislator.copyGroup(dbGroup, dbLegislator);
		}
	}
	public Legislator findLegislator(DBLegislator dbLegislator) {
		return findLegislator(dbLegislator.getName());
	}
	
	public Legislator findLegislator(String name) {
		for ( Legislator l: legislators ) {
			if ( l.getName().equals(name) ) return l; 
		}
		return null;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public CHAMBER getChamber() {
		return chamber;
	}
	public void setChamber(CHAMBER chamber) {
		this.chamber = chamber;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<Result> getResults() {
		return results;
	}
	public void setResults(List<Result> results) {
		this.results = results;
	}
	public List<Legislator> getLegislators() {
		return legislators;
	}
	public void setLegislators(List<Legislator> legislators) {
		this.legislators = legislators;
	}
}
