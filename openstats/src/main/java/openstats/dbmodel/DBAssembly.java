package openstats.dbmodel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

import javax.persistence.*;
import javax.xml.bind.annotation.*;

import openstats.model.*;
import openstats.model.District.CHAMBER;

@SuppressWarnings("serial")
@XmlRootElement
@NamedQueries({
	@NamedQuery(name = DBAssembly.assemblyTemplate, query = "select a from DBAssembly a join fetch a.districtList where a.state = ?1 and a.session = ?2" ), 
	@NamedQuery(name = DBAssembly.assemblyGroup, query = "select a from DBAssembly a join fetch a.groupInfoMap agim where a.state = ?1 and a.session = ?2 and key(agim) in (?3)" ), 
	@NamedQuery(name = DBAssembly.assemblyBase, query = "select a from DBAssembly a join fetch a.groupInfoMap agim where a.state = ?1 and a.session = ?2 and key(agim) in (?3)" ), 
	@NamedQuery(name = DBAssembly.assemblyDistrictList, query = "select a from DBAssembly a join fetch a.districtList where a = ?1 " ), 
	@NamedQuery(name = DBAssembly.assemblyResults, query = "select distinct a from DBAssembly a join fetch a.groupResultsMap agrm where a = ?1 and key(agrm) in (?2)" )	
})
@Entity(name="DBAssembly")
@Table(name="DBAssembly",catalog="lag",schema="public")
public class DBAssembly implements Comparable<DBAssembly>, Serializable {
	@Id @GeneratedValue(strategy=GenerationType.AUTO) private Long id;

	public static final String assemblyTemplate = "DBAssembly.assemblyTemplate";
	public static final String assemblyGroup = "DBAssembly.assemblyGroup";
	public static final String assemblyBase = "DBAssembly.getAssemblyBase";
	public static final String assemblyDistrictList = "DBAssembly.assemblyDistrictList";
	public static final String assemblyResults = "DBAssembly.assemblyResults";
	private String state;
	private String session;
	
	@OneToMany(cascade = CascadeType.ALL, fetch=FetchType.LAZY)
	@JoinTable(name="DBAssembly_districtList",catalog="lag",schema="public",
		joinColumns=@JoinColumn(name="DBAssembly"))
	private List<DBDistrict> districtList;
		
	@OneToMany(cascade = CascadeType.ALL, fetch=FetchType.LAZY )
	@JoinTable(name="DBAssembly_groupInfoMap",catalog="lag",schema="public",
	    joinColumns=@JoinColumn(name="DBAssembly"),
	    inverseJoinColumns=@JoinColumn(name="DBGroupInfo"))
	@MapKeyJoinColumn(name="DBGroup")
	private Map<DBGroup, DBGroupInfo> groupInfoMap;
	
	@OneToMany(cascade = CascadeType.ALL, fetch=FetchType.LAZY )
	@JoinTable(name="DBAssembly_groupResultsMap",catalog="lag",schema="public",
	    joinColumns=@JoinColumn(name="DBAssembly"),
	    inverseJoinColumns=@JoinColumn(name="DBGroupResults"))
	@MapKeyJoinColumn(name="DBGroup")
	private Map<DBGroup, DBGroupResults> groupResultsMap;
		
	public DBAssembly() {
		districtList = new ArrayList<DBDistrict>();
		groupInfoMap = new LinkedHashMap<DBGroup, DBGroupInfo>();
		groupResultsMap = new LinkedHashMap<DBGroup, DBGroupResults>();
	}
	
	// empty, for templates
	public DBAssembly(Assembly assembly) {
		state = assembly.getState();
		session = assembly.getSession();
		districtList = new ArrayList<DBDistrict>();
		for ( District district: assembly.getDistrictList() ) {			
			districtList.add(new DBDistrict(district));
		}
		groupInfoMap = new LinkedHashMap<DBGroup, DBGroupInfo>();
		groupResultsMap = new LinkedHashMap<DBGroup, DBGroupResults>();
	}
	
	public void copyGroup(DBGroup dbGroup, Assembly assembly) {
		groupInfoMap.put(dbGroup, new DBGroupInfo(assembly.getInfoItems()));
		groupResultsMap.put(dbGroup, new DBGroupResults(assembly.getResults()) );
/*
		for ( District district: assembly.getDistrictList() ) {
			findDistrict(district.getChamber(), district.getDistrict())
				.copyGroup(dbGroup, district);
		}
*/
		for ( DBDistrict dbDistrict: districtList ) {
			District district = assembly.findDistrict(dbDistrict);
			if ( district == null ) {
				// create district w/ empty results for filler
				district = new District(dbDistrict);
				List<Result> results = new ArrayList<Result>();
				for (int i=0, j=assembly.getResults().size(); i<j; ++i ) {
					results.add(new Result(BigDecimal.ZERO, BigDecimal.ZERO));
				}
				district.addResults(results);
			} else if (district.getResults().size() < assembly.getResults().size()) {
				List<Result> results = new ArrayList<Result>();
				for (int i=district.getResults().size(), j=assembly.getResults().size(); i<j; ++i ) {
					results.add(new Result(BigDecimal.ZERO, BigDecimal.ZERO));
				}
				district.addResults(results);
			}
			dbDistrict.copyGroup(dbGroup, district);
		}
		
	}
	
	public void removeGroup(DBGroup dbGroup) {
		groupInfoMap.remove(dbGroup);
		groupResultsMap.remove(dbGroup);		

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
	public Map<DBGroup, DBGroupInfo> getGroupInfoMap() {
		return groupInfoMap;
	}
	public void setGroupInfoMap(Map<DBGroup, DBGroupInfo> groupInfoMap) {
		this.groupInfoMap = groupInfoMap;
	}
	public Map<DBGroup, DBGroupResults> getGroupResultsMap() {
		return groupResultsMap;
	}
	public void setGroupResultsMap(Map<DBGroup, DBGroupResults> groupResultsMap) {
		this.groupResultsMap = groupResultsMap;
	}
	public void clearGroupResultsMap() {
		groupResultsMap = new LinkedHashMap<DBGroup, DBGroupResults>();
	}
	public DBDistrict findDistrict(CHAMBER chamber, String district) {
		for ( DBDistrict d: districtList ) {
			if ( d.getChamber() == chamber && d.getDistrict().equals(district)) return d; 
		}
		return null;
	}
	public List<DBDistrict> getDistrictList() {
		return districtList;
	}
	public void setDistrictList(List<DBDistrict> districtList) {
		this.districtList = districtList;
	}
	@Override
	public int compareTo(DBAssembly dbAssembly) {
		int s = state.compareTo(dbAssembly.state);
		if ( s != 0 ) return s;
		return this.session.compareTo(dbAssembly.session);
	}

}
