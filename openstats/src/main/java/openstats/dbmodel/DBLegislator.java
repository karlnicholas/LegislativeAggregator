package openstats.dbmodel;

import java.io.Serializable;
import java.util.*;

import javax.persistence.*;

import openstats.model.*;

@SuppressWarnings("serial")
@NamedQueries({ 
	@NamedQuery(name = DBLegislator.legislatorResultsQuery, query = "select l from DBLegislator l join fetch l.groupResultsMap lListgrm where l = ?1 and key(lListgrm) in (?2)" )
})
@Entity(name="DBLegislator")
@Table(name="DBLegislator",catalog="lag",schema="public")
public class DBLegislator implements Serializable {
	@Id @GeneratedValue(strategy=GenerationType.AUTO) private Long id;
	public static final String legislatorResultsQuery = "DBLegislator.legislatorResultsQuery";
	
	private String name;
	private String party;
	@Temporal(value = TemporalType.DATE)
	private Date startDate;
	@Temporal(value = TemporalType.DATE)
	private Date endDate;
	
	@OneToMany(fetch=FetchType.LAZY, cascade={CascadeType.ALL}, targetEntity=openstats.dbmodel.DBGroupResults.class)
	@JoinTable(name="DBLegislator_groupResultsMap",catalog="lag",schema="public",
	    joinColumns=@JoinColumn(name="DBLegislator"),
	    inverseJoinColumns=@JoinColumn(name="DBGroupResults"))
	@MapKeyJoinColumn(name="DBGroup")
	private Map<DBGroup, DBGroupResults> groupResultsMap;
		
	public DBLegislator() {
		groupResultsMap = new LinkedHashMap<DBGroup, DBGroupResults>();		
	}
	public DBLegislator(Legislator legislator) {
		name = legislator.getName();
		party = legislator.getParty();
		startDate = legislator.getStartDate();
		endDate = legislator.getEndDate();
		groupResultsMap = new LinkedHashMap<DBGroup, DBGroupResults>();		
	}

	public DBLegislator copyGroup(DBGroup dbGroup, Legislator legislator) {
		// skip legislators for now
		groupResultsMap.put(dbGroup, new DBGroupResults(legislator.getResults()) );
		// useful for chaining
		return this;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getParty() {
		return party;
	}
	public void setParty(String party) {
		this.party = party;
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
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
}
