package openstats.model;

import openstats.dbmodel.DBInfoItem;

public class InfoItem {
	private String label;
	private String description;
	public InfoItem() {}
	public InfoItem(DBInfoItem dbInfoItem) {
		this.label = dbInfoItem.getLabel();
		this.description = dbInfoItem.getDescription();
	}
	public InfoItem(String label, String description) {
		this.label = label;
		this.description = description;
	}
	public InfoItem(InfoItem infoItem) {
		this.label = infoItem.getLabel();
		this.description = infoItem.getDescription();
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

}
