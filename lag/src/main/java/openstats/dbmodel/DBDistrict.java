package openstats.dbmodel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

import javax.persistence.*;

import openstats.model.*;
import openstats.model.District.CHAMBER;

@NamedQueries({ 
	@NamedQuery(name = DBDistrict.districtResultsQuery, query = "select distinct d from DBDistrict d join fetch d.groupResultsMap dListgrm where d = ?1 and key(dListgrm) in (?2)" ), 
	@NamedQuery(name = DBDistrict.districtLegislatorsQuery, query = "select d from DBDistrict d left outer join fetch d.legislators where d = ?1" )
})
@SuppressWarnings("serial")
@Entity(name = "DBDistrict")
@Table(name = "DBDistrict",catalog="lag",schema="public")
public class DBDistrict implements Comparable<DBDistrict>, Serializable {
	@Id @GeneratedValue(strategy=GenerationType.AUTO) private Long id;
	public static final String districtResultsQuery = "DBDistrict.districtResultsQuery";
	public static final String districtLegislatorsQuery = "DBDistrict.districtLegislatorsQuery";
	
	public Long getId() { return id; }
	
	@Column(length=3)
	private String district;
	@Enumerated(EnumType.ORDINAL)
	private CHAMBER chamber;
	private String description;
	
	@OneToMany(cascade = CascadeType.ALL, fetch=FetchType.LAZY)
	@JoinTable(name="DBDistrict_legislators",catalog="lag",schema="public",
		joinColumns=@JoinColumn(name="DBDistrict"))
	private List<DBLegislator> legislators;
	
	@OneToMany(cascade={CascadeType.ALL}, fetch=FetchType.LAZY)
	@JoinTable(name="DBDistrict_groupResultsMap",catalog="lag",schema="public",
	    joinColumns=@JoinColumn(name="DBDistrict"),
	    inverseJoinColumns=@JoinColumn(name="DBGroupResults"))
	@MapKeyJoinColumn(name="DBGroup")
	private Map<DBGroup, DBGroupResults> groupResultsMap;
		
	public DBDistrict() {
		groupResultsMap = new LinkedHashMap<DBGroup, DBGroupResults>();
		legislators = new ArrayList<DBLegislator>();
	}
	public DBDistrict(District district) {
		this.district = district.getDistrict();
		this.chamber = district.getChamber();
		this.description = district.getDescription();
		groupResultsMap = new LinkedHashMap<DBGroup, DBGroupResults>();
		legislators = new ArrayList<DBLegislator>();
		for ( Legislator legislator: district.getLegislators()) {
			legislators.add(new DBLegislator(legislator));
		}
	}
	public void copyGroup(DBGroup dbGroup, District district) {
		// skip legislators for now
		// copy even if blank
		getGroupResultsMap().put(dbGroup, new DBGroupResults(district.getResults()) );
/*		
		for ( Legislator legislator: district.getLegislators() ) {
			DBLegislator dbLegislator = findLegislator(legislator);
			if ( dbLegislator == null ) {
				dbLegislator = new DBLegislator(legislator);
				getLegislators().add(dbLegislator);
			}
			dbLegislator.copyGroup(dbGroup, legislator);
		}
*/
		for ( DBLegislator dbLegislator: legislators) {
			Legislator legislator = district.findLegislator(dbLegislator);
			// work on fillers
			if ( legislator == null ) {
				// create legislator with blank results for filler
				legislator = new Legislator(dbLegislator);
				List<Result> results = new ArrayList<Result>();
				for ( int i=0, j = district.getResults().size(); i<j; ++i ) {
					results.add(new Result(BigDecimal.ZERO, BigDecimal.ZERO));
				}
				legislator.addResults(results);
			} else if ( legislator.getResults().size() < district.getResults().size() ) {
				List<Result> results = new ArrayList<Result>();
				for ( int i=legislator.getResults().size(), j = district.getResults().size(); i<j; ++i ) {
					results.add(new Result(BigDecimal.ZERO, BigDecimal.ZERO));
				}
				legislator.addResults(results);
			}
			dbLegislator.copyGroup(dbGroup, legislator);
		}
	}
	
	public DBLegislator findLegislator(Legislator legislator) {
		for ( DBLegislator tLeg: getLegislators() ) {
			if ( tLeg.getName().equals( legislator.getName() ) ) return tLeg;
		}
		return null;
	}

	public void removeGroup(DBGroup dbGroup) {
		// skip legislators for now
		getGroupResultsMap().remove(dbGroup);
	}
	public void clearGroupResultsMap() {
		groupResultsMap = new LinkedHashMap<DBGroup, DBGroupResults>();		
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
	public List<DBLegislator> getLegislators() {
		return legislators;
	}
	public void setLegislators(List<DBLegislator> legislators) {
		this.legislators = legislators;
	}	
	public Map<DBGroup, DBGroupResults> getGroupResultsMap() {
		return groupResultsMap;
	}
	public void setGroupResultsMap(Map<DBGroup, DBGroupResults> groupResultsMap) {
		this.groupResultsMap = groupResultsMap;
	}
	@Override
	public int compareTo(DBDistrict o) {
		if ( !chamber.equals(o.chamber)) return chamber.compareTo(o.chamber);
		return district.compareTo(o.district);
	}
	public void fillGroupResultsMap() {
		groupResultsMap = new LinkedHashMap<DBGroup, DBGroupResults>();		
	}

}
