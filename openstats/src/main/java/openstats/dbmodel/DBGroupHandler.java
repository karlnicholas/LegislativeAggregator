package openstats.dbmodel;

import java.util.*;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;

import openstats.model.Group;

public class DBGroupHandler {

	private DBGroupHandler() {}
	private static class SingletonHelper {
		private static final DBGroupHandler INSTANCE = new DBGroupHandler();
	}
	private static void checkInit(DBGroupHandler handler, EntityManager em) {
		if ( handler.groupMap == null ) {
			handler.groupMap = new TreeMap<String, DBGroup>();
	        CriteriaBuilder cb = em.getCriteriaBuilder();
	        CriteriaQuery<DBGroup> criteria = cb.createQuery(DBGroup.class);
	        Root<DBGroup> groupNameRoot = criteria.from(DBGroup.class);
	        for( DBGroup groupNameDb: em.createQuery(criteria.select(groupNameRoot)).getResultList() ) {
	        	handler.groupMap.put(groupNameDb.getGroupName(), groupNameDb);
	        }
		}
	}

	private TreeMap<String, DBGroup> groupMap = null;
	
	public static DBGroup createDBGroup(Group group, EntityManager em) throws OpenStatsException {
		DBGroupHandler handler = SingletonHelper.INSTANCE;
		synchronized(handler) {
			checkInit(handler, em);
			if ( handler.groupMap.containsKey(group.getGroupName()) ) {
				return handler.groupMap.get(group.getGroupName());
			}
			DBGroup dbGroup = new DBGroup(group);
			em.persist(dbGroup);
			handler.groupMap.put(dbGroup.getGroupName(), dbGroup);
			return dbGroup;
		}
	}

	public static DBGroup getDBGroup(String groupName, EntityManager em) throws OpenStatsException {
		DBGroupHandler handler = SingletonHelper.INSTANCE;
		synchronized(handler) {
			checkInit(handler, em);
			return handler.groupMap.get(groupName);
		}
	}

	public static void updateDBGroup(DBGroup dbGroup, EntityManager em) throws OpenStatsException {
		DBGroupHandler handler = SingletonHelper.INSTANCE;
		synchronized(handler) {
			checkInit(handler, em);
			em.persist(dbGroup);
			handler.groupMap.put(dbGroup.getGroupName(), dbGroup);
		}
	}

	public static void deleteDBGroup(DBGroup dbGroup, EntityManager em) throws OpenStatsException {
		DBGroupHandler handler = SingletonHelper.INSTANCE;
		synchronized(handler) {
			checkInit(handler, em);
			em.remove(dbGroup);
			handler.groupMap.remove(dbGroup.getGroupName());
		}
	}
}
