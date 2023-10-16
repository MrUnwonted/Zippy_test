package com.camerinfolks.repository;

import com.camerinfolks.utils.BeanUtils;
import org.hibernate.*;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryScalarReturn;
import org.hibernate.engine.spi.NamedSQLQueryDefinition;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Repository
public class BaseRepository {
	
    @PersistenceContext
    private EntityManager entityManager;
    
	protected Session getSession() {
		return (Session) entityManager.getDelegate();
	}
	
	public Session getQuerySession(){
		return (Session) entityManager.getDelegate();
	}
	
	@Transactional
	public void persist(Object object) {
		System.out.println(entityManager);
		entityManager.persist(object);
		entityManager.flush();
	}
	
	@Transactional
	public void update(Object object) {
		getSession().merge(object);
		entityManager.flush();
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public Object find(Class clazz, Serializable key) {
		return entityManager.find(clazz, key);
		// timer.done();

	}
	
	@Transactional
	public List find(final Class clazz) {
		// TestTimer timer = new TestTimer("---------- Count of "+ clazz + " "
		// );
		if (clazz == null) {
			throw new IllegalArgumentException("Class to list must not be null");
		}
		List list = entityManager.createQuery(
				"from " + clazz.getName() + " entity").getResultList();

		// timer.done();
		return list;
	}
	
	@Transactional
	public List find(String queryKey, Object[] object) {
		// TestTimer timer = new TestTimer("---------- find of "+ queryKey + "
		// object --> " + object );
//		logger.debug("Executing Query name is------" + queryKey);
		if (queryKey == null) {
			throw new IllegalArgumentException("queryKey should not be null");
		}
		List list = null;
		/*
		 * HibernateTemplate template = getHibernateTemplate(); template
		 * .setCacheQueries(true);
		 * logger.info(" DONE.. setCacheQueries DONE..");
		 */

		Query query = entityManager
				.createNamedQuery(appendMysqlQueryKey(queryKey));
		if (object != null && object.length > 0) {
			for (int i = 0; i < object.length; i++) {
				query.setParameter(i + 1, object[i]);
			}
		}
		list = query.getResultList();
//		logger.info(" Result Size. " + list != null ? list.size()
//				: " empty List");

		// statistics(PurchaseItem.class);

		// timer.done();
		return list;
	}
	
	private String appendMysqlQueryKey(String queryKey) {
		String query = queryKey;
			try {
				query = queryKey + "_mysql";
				entityManager.createNamedQuery(query);
			} catch (MappingException e) {
				query = queryKey;
			} catch (HibernateException e) {
				query = queryKey;
			} catch (IllegalArgumentException e) {
				query = queryKey;
			}
		
		return query;
	}
	
	
	public List findQuery(String queryValue, Object[] object)
	{
		if (queryValue == null) {
			throw new IllegalArgumentException("queryKey should not be null");
		}
		List list = null;
//		String queryKey =queryValue+ "_mysql";
		Query query = entityManager.createNativeQuery(queryValue);
		if (object != null && object.length > 0) {
			for (int i = 0; i < object.length; i++) {
				query.setParameter(i + 1, object[i]);
			}
		}
		list = query.getResultList();
		return list;
		
	}
	
	public List findQuery(String queryValue)
	{
		if (queryValue == null) {
			throw new IllegalArgumentException("queryKey should not be null");
		}
		List list = null;
		Query query = entityManager.createNativeQuery(queryValue);
		list = query.getResultList();
		return list;
		
	}
	@Transactional
	 public int updateQuery(final String queryKey, final Object[] params) {
	        if (queryKey == null || params == null) {
	            throw new IllegalArgumentException(
	                    "Query Key or Parameters should not be null");
	        }
//	        logger.debug("Executing Query name is------" + queryKey);
	        Query query = entityManager.createNativeQuery(queryKey);
//	        Query query = entityManager
//	                .createNamedQuery(appendMysqlQueryKey(queryKey));
	        bindParamtersToQuery(params, query);
	        int rowsEffected = query.executeUpdate();
//	        logger.info(" No of rows effected for current update.................."
//	                + rowsEffected);
	        return rowsEffected;
	    }
	
	  protected void bindParamtersToQuery(final Object[] parameters, Query query) {
	        if (parameters != null) {
	            int i = 0;
	            while (i < parameters.length) {
	                query.setParameter(i + 1, parameters[i++]);
	            }
	        }
	    }
	public List listForSuggesstion(Class clazz,
			Map<String, Object> criteriaMap, int startIndex, int maxRows) {
		Criteria criteria = suggestion(clazz, criteriaMap);
		criteria.setMaxResults(maxRows);
		criteria.setFirstResult(startIndex);
		criteria.setFirstResult(startIndex).setMaxResults(maxRows);
		return criteria.list();
	}
	
	private Criteria suggestion(final Class clazz,
			final Map<String, Object> criteriaMap) {
		//		printCriteria(criteriaMap);
		Session session = getSession();
		Criteria criteria = session.createCriteria(clazz);
		if(!BeanUtils.isNull(criteriaMap))
		{

			Iterator<String> keys = criteriaMap.keySet().iterator();
			String key = null;
			while (keys.hasNext()) {
				key = keys.next();
				Object value = criteriaMap.get(key);
				if (value == null) {
					criteria.add(Restrictions.isNull(key));
				} else if (value instanceof Integer || value instanceof Long
						|| value instanceof Float || value instanceof Double
						|| value instanceof Boolean) {
					criteria.add(Restrictions.eq(key, value));
				} else if (value instanceof Map) {
					Map<String, Object> valueMap = (Map<String, Object>) value;
					if (valueMap.containsKey("ne")) {
						criteria.add(Restrictions.ne(key, valueMap.get("ne")));
					}
				} else {
					criteria.add(Restrictions.like(key, value.toString())
							.ignoreCase());
				}
			}
		}
		return criteria;
	}
	
	public List searchCriteria(final String queryKey, final String appendQuery,
			final List<?> searchList) {
		org.hibernate.Query finalQuery = getModifiedQuery(queryKey,
				appendQuery, searchList);

//		if (startIndex != -1 && maxResults != -1) {
//			finalQuery.setFirstResult(startIndex).setMaxResults(maxResults);
//		}
		return finalQuery.list();
	}
	
	private org.hibernate.Query getModifiedQuery(final String queryKey,
			final String appendQuery, final List<?> searchList) {
		Session session = getSession();
		boolean isNativeQuery = session
				.getNamedQuery(appendMysqlQueryKey(queryKey)) instanceof SQLQuery;
		String queryString = session.getNamedQuery(
				appendMysqlQueryKey(queryKey)).getQueryString();
		// Do the query modifications

		if (appendQuery != null) {
			queryString += appendQuery;
			if (queryString.contains("where")) {
				String mySearchQuery[] = queryString.split("where");
				if (mySearchQuery[1].length() <= 1) {
					/*
					 * logger.info("mySearch Query is..." + mySearchQuery[0]);
					 * logger.info("mySearch Query is..." + mySearchQuery[1]);
					 */
					queryString = mySearchQuery[0];
				}
			}
		}
		queryString = truncToDate(queryString);

		org.hibernate.Query finalQuery = null;
		if (isNativeQuery) {
			finalQuery = session.createSQLQuery(queryString);
			NamedSQLQueryDefinition sqlQDef = ((SessionFactoryImpl) getSession()
					.getSessionFactory())
					.getNamedSQLQuery(appendMysqlQueryKey(queryKey));
			NativeSQLQueryReturn[] temp = sqlQDef.getQueryReturns();
			for (NativeSQLQueryReturn tempObj : temp) {
				NativeSQLQueryScalarReturn t = (NativeSQLQueryScalarReturn) tempObj;
				((SQLQuery) finalQuery).addScalar(t.getColumnAlias(), t
						.getType());
			}
		} else {
			finalQuery = session.createQuery(queryString);
		}

		Iterator<?> iterator = null;
		iterator = searchList.iterator();
		int i = 0;
		while (iterator.hasNext()) {
			Object temp = iterator.next();
			if (temp instanceof String)
				finalQuery.setParameter(i++, (String) temp);
			else if (temp instanceof Date)
				finalQuery.setParameter(i++, (Date) temp);
			else if (temp instanceof Boolean)
				finalQuery.setParameter(i++, (Boolean) temp);
			else if (temp instanceof Enum)
				finalQuery.setParameter(i++, (Enum) temp);
			else if (temp instanceof Double)
				finalQuery.setParameter(i++, (Double) temp);
			else
				finalQuery.setParameter(i++, (Long) temp);
		}
		return finalQuery;
	}
	private String truncToDate(String query) {
			if (query.contains("TRUNC")) {
				query = query.replaceAll("TRUNC", "DATE");
			}
			if (query.contains("trunc")) {
				query = query.replaceAll("trunc", "date");
			}
			if (query.contains("to_char")) {
				query = query.replaceAll("to_char", "date_format");
			}
			if (query.contains("YYYY-MM-DD")) {
				query = query.replaceAll("YYYY-MM-DD", "%Y-%m-%d");
			}
			if (query.contains("YYYY/MM/DD")) {
				query = query.replaceAll("YYYY/MM/DD", "%Y/%m/%d");
			}
			if (query.contains("dd/MM/yyyy")) {
				query = query.replaceAll("dd/MM/yyyy", "%d/%m/%Y");
			}
			if (query.contains("dd/mm/yyyy")) {
				query = query.replaceAll("dd/mm/yyyy", "%d/%m/%Y");
			}
			if (query.contains("DD/MM/YYYY")) {
				query = query.replaceAll("DD/MM/YYYY", "%d/%m/%Y");
			}
			if (query.contains("MM/dd/yyyy")) {
				query = query.replaceAll("MM/dd/yyyy", "%m/%d/%Y");
			}
			if (query.contains("HH:mi")) {
				query = query.replaceAll("HH:mi", "%h:%i");
			}
			if (query.contains("hh:mi AM")) {
				query = query.replaceAll("hh:mi AM", "%h:%i %p");
			}
			if (query.contains("SYSDATE")) {
				query = query.replaceAll("SYSDATE", "SYSDATE()");
			}
			if (query.contains("sysdate")) {
				query = query.replaceAll("sysdate", "sysdate()");
			}
			/*if (query.contains("to_date")) {
				query = query.replaceAll("to_date", "str_to_date");
			}
			if (query.contains("nvl")) {
				query = query.replaceAll("nvl", "coalesce");
			}*/
			if (query.contains("NVL")) {
				query = query.replaceAll("NVL", "coalesce");
			}
		return query;
	}
}