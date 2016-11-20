package openstats.dbmodel;

import java.io.Serializable;
import java.util.*;

import javax.persistence.*;

@SuppressWarnings("serial")
@Entity(name = "DBGroupResults")
@Table(name = "DBGroupResults",catalog="lag",schema="public")
public class DBGroupResults implements Serializable {
	@Id @GeneratedValue(strategy=GenerationType.AUTO) private Long id;

	@ElementCollection(fetch=FetchType.EAGER)
	@OrderColumn
	private List<Result> Results;
	
	public DBGroupResults() {
		Results = new ArrayList<Result>();
	}
	public DBGroupResults(List<Result> Results) {
		this.Results = Results;
	}

	public List<Result> getResults() {
		return Results;
	}
	public void setResult( List<Result> Results) {
		this.Results = Results;
	}
}
