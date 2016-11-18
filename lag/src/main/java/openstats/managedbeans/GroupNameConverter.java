package openstats.managedbeans;

import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.*;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import openstats.dbmodel.*;

@ManagedBean(name = "groupNameConverterBean") 
@FacesConverter(value = "groupNameConverter")
public class GroupNameConverter implements Converter {
	
	@Inject
    private EntityManager em;
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {		
		try {
			return DBGroupHandler.getDBGroup(value, em);
		} catch (OpenStatsException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		return ((DBGroup)value).getGroupName();
	}

}
