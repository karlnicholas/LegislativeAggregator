

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import openstats.client.les.Labels;
import openstats.data.AssemblyRepository;
import openstats.dbmodel.*;
import openstats.model.Assembly;
import openstats.util.AssemblyCsvHandler;

public class JpaReadAssembly {

	EntityManagerFactory emf;
	EntityManager em;
	AssemblyRepository assemblyRepo;

	public static void main(String[] args) throws Exception {
		/*		
//		initJpa();

		for( ReadAction testAction: OpenStateClasses.getTestActions() ) {
			Assembly Assembly = buildAssembly(testAction);
			writeCsv(Assembly);
		}
*/
		new JpaReadAssembly().run();
	}
	
	private void run() throws Exception {
		initJpa();

		try {
			List<DBGroup> dbGroups = new ArrayList<DBGroup>();
			dbGroups.add( DBGroupHandler.getDBGroup("B19301", em));
			dbGroups.add( DBGroupHandler.getDBGroup("BILLPROGRESS", em));
//			DBGroup dbGroup = DBGroupHandler.getDBGroup(Labels.LESGROUPNAME, em);
//			dbGroups.add( DBGroupHandler.getDBGroup(Labels.LESGROUPNAME, em) );
			Assembly assembly = assemblyRepo.buildAssemblyFromGroups(dbGroups, "CA", "2013");
			Writer writer = new OutputStreamWriter(System.out);
			
			AssemblyCsvHandler csvHandler = new AssemblyCsvHandler();
			csvHandler.writeCsv(assembly, writer);
			
			writer.flush();
		} finally {
			emf.close();
		}

	}

	private void initJpa() throws Exception {
		emf = Persistence.createEntityManagerFactory("openstats");
		em = emf.createEntityManager();
		assemblyRepo = new AssemblyRepository(em);
	}

}