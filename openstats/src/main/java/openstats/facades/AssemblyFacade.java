package openstats.facades;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.*;

import openstats.dbmodel.*;
import openstats.model.Assembly;

//
//The @Stateless annotation eliminates the need for manual transaction demarcation
//I have no idea what that means ...
//
@Stateless
public class AssemblyFacade {

	@Inject
	private EntityManager em;
	
	public AssemblyFacade() {}
    // for testing
    public AssemblyFacade(EntityManager em) {
    	this.em = em;
    }

	
    /**
     * Write (or update?) the DBAssembly referenced by the Assembly
     * 
     * @param Assembly
     * @throws OpenStatsException
     */

    public void writeAssembly(Assembly assembly) throws OpenStatsException {
		DBGroup dbGroup = DBGroupHandler.createDBGroup(assembly.getGroup(), em);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DBAssembly> criteria = cb.createQuery(DBAssembly.class);
        Root<DBAssembly> rAssembly = criteria.from(DBAssembly.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new
        // feature in JPA 2.0
        // criteria.select(member).where(cb.equal(member.get(Member_.email), email));
        criteria.select(rAssembly).where(
        		cb.equal(rAssembly.get("state"), assembly.getState()), 
        		cb.equal(rAssembly.get("session"), assembly.getSession())
        	);
        DBAssembly dbAssembly = em.createQuery(criteria).getSingleResult();
		dbAssembly.copyGroup(dbGroup, assembly);
		em.merge(dbAssembly);
	}

	/**
     * Delete the DBAssembly referenced by the Assembly
	 * 
	 * @param Assembly
	 * @throws OpenStatsException 
	 */
	public void deleteAssemblyGroup(String groupName, String state, String session) throws OpenStatsException {
		// maybe someday delete the group if nothing references it.
		DBAssembly dbAssembly = DBAssemblyHandler.getDBAssembly(state, session, em);
		dbAssembly.removeGroup(DBGroupHandler.getDBGroup(groupName, em));
		em.merge(dbAssembly);
		
	}
}
