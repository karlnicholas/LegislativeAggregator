package openstats.dbmodel;

import java.io.Serializable;
import java.util.*;

import javax.persistence.*;

import openstats.model.InfoItem;

@SuppressWarnings("serial")
@Entity(name = "DBGroupInfo")
@Table(name = "DBGroupInfo",catalog="lag",schema="public")
public class DBGroupInfo implements Serializable {
	@Id @GeneratedValue(strategy=GenerationType.AUTO) private Long id;

	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	@JoinTable(name="DBGroupInfo_GroupItems",catalog="lag",schema="public",
			joinColumns=@JoinColumn(name="DBGroupInfo"))
	@OrderColumn
	private List<DBInfoItem> GroupItems = new ArrayList<DBInfoItem>();
	
	public DBGroupInfo() {}
	public DBGroupInfo(List<InfoItem> InfoItems) {
		for ( InfoItem infoItem: InfoItems) {
			GroupItems.add(new DBInfoItem(infoItem));
		}
	}
	public List<DBInfoItem> getGroupItems() {
		return GroupItems;
	}
	public void setGroupItems(List<DBInfoItem> GroupItems) {
		this.GroupItems = GroupItems;
	}
}