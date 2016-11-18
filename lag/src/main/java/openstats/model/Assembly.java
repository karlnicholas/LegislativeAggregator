package openstats.model;

import java.util.*;

import javax.xml.bind.annotation.XmlRootElement;

import openstats.dbmodel.*;
import openstats.model.District.CHAMBER;

@XmlRootElement
public class Assembly implements Comparable<Assembly> {

	private String state;
	private String session;
	private Group group;
	private List<District> districtList;
	private List<InfoItem> infoItems;
	private List<Result> results;
	
	public Assembly() {
		infoItems = new ArrayList<InfoItem>();
		results = new ArrayList<Result>();
		districtList = new ArrayList<District>();
	}
	public Assembly(String state, String session) {
		this.state = state;
		this.session = session;
		districtList = new ArrayList<District>();
		infoItems = new ArrayList<InfoItem>();
		results = new ArrayList<Result>();
	}

	// empty, for templates
	public Assembly(Assembly assembly) {
		state = assembly.getState();
		session = assembly.getSession();
		districtList = new ArrayList<District>();
		for ( District district: assembly.getDistrictList()) {
			districtList.add(new District(district));
		}
		infoItems = new ArrayList<InfoItem>();
		results = new ArrayList<Result>();
	}

	
	public Assembly(DBAssembly dbAssembly) {
		state = dbAssembly.getState();
		session = dbAssembly.getSession();
		districtList = new ArrayList<District>();
		for ( DBDistrict dbDistrict: dbAssembly.getDistrictList()) {
			districtList.add(new District(dbDistrict));
		}
		infoItems = new ArrayList<InfoItem>();
		results = new ArrayList<Result>();
	}

	public void addInfoItems(List<InfoItem> infoItems) {
		this.infoItems.addAll(infoItems);
	}

	public void addResults(List<Result> results) {
		this.results.addAll(results);
	}
	public void addResult(Result result) {
		this.results.add(result);
	}
	public void copyGroup(DBGroup dbGroup, DBAssembly dbAssembly) {

		group = new Group(dbGroup.getGroupName(), dbGroup.getGroupDescription());
		for ( DBDistrict dbDistrict: dbAssembly.getDistrictList()) {
			District district = findDistrict(dbDistrict);
			district.copyGroup(dbGroup, dbDistrict);
		}

		for( DBInfoItem dbInfoItem: dbAssembly.getGroupInfoMap().get(dbGroup).getGroupItems() ) {
			infoItems.add(new InfoItem(dbInfoItem));
		}
		for ( Result Result: dbAssembly.getGroupResultsMap().get(dbGroup).getResults()) {
			results.add(Result);
		}

	}
	public District findDistrict(DBDistrict dbDistrict) {
		CHAMBER chamber = dbDistrict.getChamber();
		String district = dbDistrict.getDistrict();
		return findDistrict(chamber, district);
	}
	public District findDistrict(CHAMBER chamber, String district) {
		for ( District d: districtList ) {
			if ( d.getChamber() == chamber && d.getDistrict().equals(district)) return d; 
		}
		return null;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getSession() {
		return session;
	}
	public void setSession(String session) {
		this.session = session;
	}
	public Group getGroup() {
		return group;
	}
	public void setGroup(Group group) {
		this.group = group;
	}
	public List<District> getDistrictList() {
		return districtList;
	}
	public void setDistrictList(List<District> districtList) {
		this.districtList = districtList;
	}	
	public List<InfoItem> getInfoItems() {
		return infoItems;
	}
	public void setInfoItems(List<InfoItem> infoItems) {
		this.infoItems = infoItems;
	}
	public List<Result> getResults() {
		return results;
	}
	public void setResults(List<Result> results) {
		this.results = results;
	}
	@Override
	public int compareTo(Assembly assembly) {
		int s = state.compareTo(assembly.state);
		if ( s != 0 ) return s;
		return this.session.compareTo(assembly.session);
	}
}
