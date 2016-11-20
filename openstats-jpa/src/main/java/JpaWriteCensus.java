

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import openstats.client.census.CensusAssembly;
import openstats.client.census.CensusTable;
import openstats.client.census.CensusTable.StringPair;
import openstats.client.openstates.*;
import openstats.dbmodel.DBAssemblyHandler;
import openstats.facades.AssemblyFacade;
import openstats.model.*;

public class JpaWriteCensus {

	private static EntityManagerFactory emf;
	private static EntityManager em;

	public static void main(String[] args) throws Exception {
		new JpaWriteCensus().run();
	}

	private void initJpa() throws Exception {
		emf = Persistence.createEntityManagerFactory("openstats");
		em = emf.createEntityManager();
	}
	
	private void run() throws Exception {
		initJpa();
		
		CensusAssembly censusAssembly = new CensusAssembly();
		AssemblyFacade assemblyFacade = new AssemblyFacade(em);
		
		CensusTable censusTable = new CensusTable("B19301", "PER CAPITA INCOME IN THE PAST 12 MONTHS (IN 2012 INFLATION-ADJUSTED DOLLARS)");
		List<StringPair> cells = new ArrayList<StringPair>();
		cells.add(new StringPair("B19301_001E", "Estimate: Total population"));
		cells.add(new StringPair("B19301_001M", "Margin of Error: Total population"));
		cells.add(new StringPair("B19301A_001E", "Estimate: People who are White alone"));
		cells.add(new StringPair("B19301A_001M", "Margin of Error: People who are White alone"));
		cells.add(new StringPair("B19301B_001E", "Estimate: People who are Black or African American alone"));
		cells.add(new StringPair("B19301B_001M", "Margin of Error: People who are Black or African American alone"));
		cells.add(new StringPair("B19301C_001E", "Estimate: People who are American Indian and Alaska Native alone"));
		cells.add(new StringPair("B19301C_001M", "Margin of Error: People who are American Indian and Alaska Native alone"));
		cells.add(new StringPair("B19301D_001E", "Estimate: People who are Asian alone"));
		cells.add(new StringPair("B19301D_001M", "Margin of Error: People who are Asian alone"));
		cells.add(new StringPair("B19301E_001E", "Estimate: People who are Native Hawaiian and Other Pacific Islander alone"));
		cells.add(new StringPair("B19301E_001M", "Margin of Error: People who are Native Hawaiian and Other Pacific Islander alone"));
		cells.add(new StringPair("B19301F_001E", "Estimate: People who are Some Other Race alone"));
		cells.add(new StringPair("B19301F_001M", "Margin of Error: People who are Some Other Race alone"));
		cells.add(new StringPair("B19301G_001E", "Estimate: Two or more races population"));
		cells.add(new StringPair("B19301G_001M", "Margin of Error: Two or more races population"));
		cells.add(new StringPair("B19301H_001E", "Estimate: White alone, not Hispanic or Latino population"));
		cells.add(new StringPair("B19301H_001M", "Margin of Error: White alone, not Hispanic or Latino population"));
		cells.add(new StringPair("B19301I_001E", "Estimate: People who are Hispanic or Latino"));
		cells.add(new StringPair("B19301I_001M", "Margin of Error: People who are Hispanic or Latino"));
		censusTable.cells = cells;
		
		EntityTransaction et = em.getTransaction();
		et.begin();

//		for ( OpenState openState: OpenStateClasses.getOpenStates() ) {
			OpenState openState = new OpenStateClasses.CAOpenState();	
			Assembly assembly = DBAssemblyHandler.getAssembly(openState.getState(), openState.getSession(), em);
			censusAssembly.censusAssembly(openState, censusTable, assembly);
			assemblyFacade.writeAssembly(assembly);
			System.out.println(assembly.getState()+":"+assembly.getDistrictList().size());
//		}

		et.commit();

/*
 		OpenState testAction = new GAOpenState();
 		Assembly assembly = buildAssembly(testAction, em);
		writeJpa(assembly);
*/		
		
		emf.close();
	}

/*
	private static void printAllActions(Bill bill) {
		for ( Bill.Action action: bill.actions ) {
			System.out.println(action);
		}
	}
	private static TreeMap<org.openstates.data.Legislator, AuthorStats> readLegislators() throws Exception {
		TreeMap<org.openstates.data.Legislator, AuthorStats> legislators = new TreeMap<>();
		for ( org.openstates.data.Legislator legislator: org.openstates.model.Legislators.values()) {
			legislators.put(legislator, new AuthorStats());
		}
		return legislators;
	}
*/

}
