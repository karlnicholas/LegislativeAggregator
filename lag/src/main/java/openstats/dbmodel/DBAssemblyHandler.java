package openstats.dbmodel;

import java.util.*;

import javax.persistence.*;

import openstats.model.Assembly;

public class DBAssemblyHandler {
	private TreeMap<String, DBAssembly> assemblyMap = null;
	
	private DBAssemblyHandler() {}
	private static class SingletonHelper {
		private static final DBAssemblyHandler INSTANCE = new DBAssemblyHandler();
	}
	private static void checkInit(DBAssemblyHandler handler, EntityManager em) {
		if ( handler.assemblyMap == null ) {
			handler.assemblyMap = new TreeMap<String, DBAssembly>();
		}
	}

	public static void createAssembly(Assembly assembly, EntityManager em) throws OpenStatsException {
		DBAssemblyHandler handler = SingletonHelper.INSTANCE;
		synchronized(handler) {
			checkInit(handler, em);
			String key = assembly.getState()+'-'+assembly.getSession();
			if ( handler.assemblyMap.containsKey(key) ) throw new OpenStatsException("Assembly already created: " + key);
			DBAssembly dbAssembly = new DBAssembly(assembly);
			em.persist(dbAssembly);
			handler.assemblyMap.put(key, dbAssembly);
		}
	}

	public static Assembly getAssembly(String state, String session, EntityManager em) {
		DBAssemblyHandler handler = SingletonHelper.INSTANCE;
		synchronized(handler) {
			checkInit(handler, em);
			return loadAssembly(state, session, em);
		}
	}

	public static DBAssembly getDBAssembly(String state, String session, EntityManager em) {
		DBAssemblyHandler handler = SingletonHelper.INSTANCE;
		synchronized(handler) {
			checkInit(handler, em);
			return loadDBAssembly(state, session, em);
		}
	}

	private static DBAssembly loadDBAssembly(String state, String session, EntityManager em) {
		DBAssemblyHandler handler = SingletonHelper.INSTANCE;
		DBAssembly dbAssembly;
		String key = state+'-'+session;
		if ( !handler.assemblyMap.containsKey(key) ) {
			dbAssembly = em.createNamedQuery(DBAssembly.assemblyTemplate, DBAssembly.class)
					.setParameter(1, state)
					.setParameter(2, session)
					.getSingleResult();
		    //
			TypedQuery<DBDistrict> districtLegislatorsQuery = em.createNamedQuery(DBDistrict.districtLegislatorsQuery, DBDistrict.class );
			//
			for ( DBDistrict dbDistrict: dbAssembly.getDistrictList()) {
				DBDistrict rDistrict = districtLegislatorsQuery.setParameter(1, dbDistrict).getSingleResult();
				dbDistrict.setLegislators(rDistrict.getLegislators());
			}
			handler.assemblyMap.put(key, dbAssembly);
		} else {
			dbAssembly = handler.assemblyMap.get(key);
		}
		return dbAssembly;
	}

	private static Assembly loadAssembly(String state, String session, EntityManager em) {
		return new Assembly(loadDBAssembly(state, session, em));
	}
}
