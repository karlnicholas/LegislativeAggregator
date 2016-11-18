package openstats.managedbeans;

import java.io.*;
import java.util.*;

import javax.annotation.PostConstruct;
import javax.faces.bean.*;
import javax.faces.context.*;
import javax.inject.Inject;

import openstats.data.AssemblyRepository;
import openstats.dbmodel.DBAssembly;
import openstats.dbmodel.DBGroup;
import openstats.model.Assembly;
import openstats.util.AssemblyCsvHandler;

@ManagedBean
@ViewScoped
public class ExportCsv implements Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
    private FacesContext facesContext;
    @Inject
    private AssemblyRepository assemblyRepo;
    
    private Assembly assembly;
    
	//
	private Map<String,Object> assemblyTitles;
	private String currentAssembly;
	//
//	private DBGroup[] assemblyGroups;
//	private DBGroup assemblyGroupItem;
	private DBGroup[] allAssemblyGroups;
	private DBGroup[] selectedAssemblyGroups;

	private boolean groupsSelected;

	
    @PostConstruct
	public void postConstruct() {
    	assemblyTitles = new TreeMap<String, Object>();
		List<DBAssembly> assemblies = assemblyRepo.listAllAssemblies(); 
		for ( DBAssembly assembly: assemblies ) {
			assemblyTitles.put(assembly.getState() + " " + assembly.getSession(), assembly.getState() + "-" + assembly.getSession());
		}
		setGroupsSelected(false);
	}
        
    private void loadGroups() {
		if ( currentAssembly == null || currentAssembly.isEmpty() ) return;
		String[] keys = currentAssembly.split("-");
		Set<DBGroup> groups = assemblyRepo.loadGroupsForAssembly(keys[0], keys[1]);
		this.allAssemblyGroups = new DBGroup[groups.size()];
		this.allAssemblyGroups = groups.toArray(allAssemblyGroups);
	}

	public Map<String, Object> getAssemblyTitles() {
		return assemblyTitles;
	}

	public void setAssemblyTitles(Map<String, Object> assemblyTitles) {
		this.assemblyTitles = assemblyTitles;
	}

	public String getCurrentAssembly() {
		return currentAssembly;
	}

	public void setCurrentAssembly(String currentAssembly) {
		this.currentAssembly = currentAssembly;
		loadGroups();
	}

	public DBGroup[] getAllAssemblyGroups() {
		return allAssemblyGroups;
	}

	public void setAllAssemblyGroups(DBGroup[] allAssemblyGroups) {
		this.allAssemblyGroups = allAssemblyGroups;
		groupsSelected = true;
	}

	public DBGroup[] getSelectedAssemblyGroups() {
		return selectedAssemblyGroups;
	}

	public void setSelectedAssemblyGroups(DBGroup[] selectedAssemblyGroups) {
		this.selectedAssemblyGroups = selectedAssemblyGroups;
		groupsSelected = true;
	}
	
    public boolean isGroupsSelected() {
		return groupsSelected;
	}

	public void setGroupsSelected(boolean groupsSelected) {
		this.groupsSelected = groupsSelected;
	}

	public void exportCsv() throws Exception {
    	AssemblyCsvHandler createCsv = new AssemblyCsvHandler();
    	String[] keys = currentAssembly.split("-");
    	Assembly assembly = assemblyRepo.buildAssemblyFromGroups(Arrays.asList(selectedAssemblyGroups), keys[0], keys[1]);

    	ExternalContext ec = facesContext.getExternalContext();

	    ec.responseReset(); // Some JSF component library or some Filter might have set some headers in the buffer beforehand. We want to get rid of them, else it may collide.
//	    ec.setResponseContentType("application/json;charset=UTF-8"); // Check http://www.iana.org/assignments/media-types for all types. Use if necessary ExternalContext#getMimeType() for auto-detection based on filename.
	    ec.setResponseContentType("text/csv;charset=UTF-8"); // Check http://www.iana.org/assignments/media-types for all types. Use if necessary ExternalContext#getMimeType() for auto-detection based on filename.
	    ec.setResponseHeader("Content-Disposition","attachment; filename=\""+currentAssembly+".csv\"");

	    Writer writer = new OutputStreamWriter(ec.getResponseOutputStream());
	    // Now you can write the InputStream of the file to the above OutputStream the usual way.
	    // ...
    	createCsv.writeCsv(assembly, writer );
//		JAXBContext ctx = JAXBContext.newInstance(Assembly.class);
//		ctx.createMarshaller().marshal(assembly, writer);
//	    ObjectMapper mapper = new ObjectMapper();
//	    OutputStream os = ec.getResponseOutputStream();
//		mapper.writeValue(os, assembly);
//		os.flush();

    	writer.flush();

	    facesContext.responseComplete(); // Important! Otherwise JSF will attempt to render the response which obviously will fail since it's already written with a file and cled.
    	
	}

    public void goToIndex() throws IOException {
    	facesContext.getExternalContext().redirect("");
    }
	
    public Assembly getAssembly() throws Exception {
		if ( assembly == null ) {
	    	String[] keys = currentAssembly.split("-");
	    	assembly = assemblyRepo.buildAssemblyFromGroups(Arrays.asList(selectedAssemblyGroups), keys[0], keys[1]);
		}
		return assembly;
	}

}
