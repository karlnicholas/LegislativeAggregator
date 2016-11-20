package openstats.dbmodel;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import openstats.model.InfoItem;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity(name = "DBInfoItem")
@Table(name = "DBInfoItem",catalog="lag",schema="public")
public class DBInfoItem implements Serializable {
	@Id @GeneratedValue(strategy=GenerationType.AUTO) private Long id;
	
	private String Label;
	@Column(length=1023)
	private String description;
	public DBInfoItem() {}
	public DBInfoItem(InfoItem infoItem) {
		this.Label = infoItem.getLabel();
		this.description = infoItem.getDescription();
	}
	public String getLabel() {
		return Label;
	}
	public void setLabel(String label) {
		Label = label;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

}
