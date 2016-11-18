package openstats.dbmodel;

import java.io.Serializable;

import javax.persistence.*;

import openstats.model.Group;

@SuppressWarnings("serial")
@Entity(name = "DBGroup")
@Table(name = "DBGroup",catalog="lag",schema="public")
public class DBGroup implements Comparable<DBGroup>, Serializable {
	@Id @GeneratedValue(strategy=GenerationType.AUTO) private Long id;
	
	@Column(unique=true)
	private String groupName;
	private String groupDescription;
	
	public DBGroup() {}
	public DBGroup(Group osGroup) {
		this.groupName = osGroup.getGroupName();
		this.groupDescription = osGroup.getGroupDescription();
	}
	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupDescription() {
		return groupDescription;
	}
	public void setGroupDescription(String groupDescription) {
		this.groupDescription = groupDescription;
	}
	@Override
	public int compareTo(DBGroup o) {
		return groupName.compareTo(o.groupName);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((groupName == null) ? 0 : groupName.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DBGroup other = (DBGroup) obj;
		if (groupName == null) {
			if (other.groupName != null)
				return false;
		} else if (!groupName.equals(other.groupName))
			return false;
		return true;
	}


}
