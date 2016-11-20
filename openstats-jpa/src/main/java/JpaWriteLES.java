

import javax.persistence.*;

import openstats.client.les.ComputeAssembly2;
import openstats.client.openstates.*;
import openstats.dbmodel.DBAssemblyHandler;
import openstats.facades.AssemblyFacade;
import openstats.model.*;

public class JpaWriteLES {

	private EntityManagerFactory emf;
	private EntityManager em;

	public static void main(String[] args) throws Exception {
		new JpaWriteLES().run();
	}

	private void initJpa() throws Exception {
		emf = Persistence.createEntityManagerFactory("openstats");
		em = emf.createEntityManager();
	}
	
	private void run() throws Exception {
		initJpa();
		
		ComputeAssembly2 computeAssembly = new ComputeAssembly2();
		AssemblyFacade assemblyFacade = new AssemblyFacade(em);
		
//		EntityTransaction et = em.getTransaction();
//		et.begin();
		
		for( OpenState openState: OpenStateClasses.getOpenStates()) {
//			OpenState openState = new OpenStateClasses.CAOpenState();
//			System.out.println("State: " + openState.getState());
			Assembly assembly = DBAssemblyHandler.getAssembly(openState.getState(), openState.getSession(), em);
			computeAssembly.computeAssemblyLES(openState, assembly);
//			assemblyFacade.writeAssembly(assembly);
		}

//		et.commit();

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
