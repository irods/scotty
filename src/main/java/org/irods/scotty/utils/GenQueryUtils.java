package org.irods.scotty.utils;

import java.util.ArrayList;
import java.util.List;

import org.irods.jargon.core.pub.EnvironmentalInfoAO;
import org.irods.jargon.core.pub.IRODSGenQueryExecutor;
import org.irods.jargon.core.pub.IRODSGenQueryExecutorImpl;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.pub.SpecificQueryAO;
import org.irods.jargon.core.pub.domain.SpecificQueryDefinition;
import org.irods.jargon.core.query.IRODSGenQuery;
import org.irods.jargon.core.query.IRODSQueryResultSetInterface;
import org.irods.jargon.core.query.JargonQueryException;
import org.irods.jargon.core.query.RodsGenQueryEnum;
import org.irods.jargon.core.query.SpecificQuery;
import org.irods.jargon.core.query.SpecificQueryResultSet;
import org.irods.jargon.core.utils.IRODSDataConversionUtil;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.DataNotFoundException;
import org.irods.jargon.core.exception.JargonException;

public class GenQueryUtils {
	
	public static long sumTotalSizeDataObjectsInZone(
			Boolean inTrash,
			IRODSAccount irodsAccount,
			IRODSFileSystem irodsFileSystem,
			String user,
			String resource) throws JargonException {

		if (irodsAccount == null) {
			throw new IllegalArgumentException("iRODSAccount is null");
		}
		
		String zone = irodsAccount.getZone();
		if (zone == null) {
			throw new IllegalArgumentException("Zone is null");
		}
		if (irodsFileSystem == null) {
			throw new IllegalArgumentException("iRODSFileSystem is null");
		}

		final String trashPath = "%/trash/home/%";
		final String zonePath = "/" + zone + "/%";

		IRODSGenQueryExecutor irodsGenQueryExecutor = new IRODSGenQueryExecutorImpl(
				irodsFileSystem.getIrodsSession(), irodsAccount);

		StringBuilder query = new StringBuilder();
		query.append("SELECT SUM(");
		query.append(RodsGenQueryEnum.COL_DATA_SIZE.getName());

		query.append(") WHERE ");
		
		// specify the zone by zone path - cannot use COL_D_OWNER_ZONE here because data objects
		// in this zone could be owned by users in federated zones
		//query.append(RodsGenQueryEnum.COL_D_DATA_PATH.getName());
		query.append(RodsGenQueryEnum.COL_COLL_NAME.getName());
		query.append(" LIKE '");
		query.append(IRODSDataConversionUtil
				.escapeSingleQuotes(zonePath));
		query.append("'");
		
		// just count data objects in this zone that are in the trash		
		if (inTrash) {
			query.append(" AND ");
			query.append(RodsGenQueryEnum.COL_COLL_NAME.getName());
			query.append(" LIKE '");
			query.append(IRODSDataConversionUtil
					.escapeSingleQuotes(trashPath));
			query.append("'");
		}
		
		// add user to query if specified
		if ( user != null) {
			query.append(" AND ");
			query.append(RodsGenQueryEnum.COL_D_OWNER_NAME.getName());
			query.append(" = '");
			query.append(IRODSDataConversionUtil
					.escapeSingleQuotes(user));
			query.append("'");
		}
		
		// add resource to query if specified
		if ( resource != null) {
			query.append(" AND ");
			query.append(RodsGenQueryEnum.COL_D_RESC_NAME.getName());
			query.append(" = '");
			query.append(IRODSDataConversionUtil
					.escapeSingleQuotes(resource));
			query.append("'");
		}

		IRODSGenQuery irodsQuery = IRODSGenQuery.instance(query.toString(), 1);
		IRODSQueryResultSetInterface resultSet;

		try {
			resultSet = irodsGenQueryExecutor
					.executeIRODSQueryAndCloseResultInZone(irodsQuery, 0, zone);
		} catch (JargonQueryException e) {
			throw new JargonException(e);
		}

		long fileCtr = 0;

		if (resultSet.getResults().size() > 0) {
			fileCtr = IRODSDataConversionUtil
					.getLongOrZeroFromIRODSValue(resultSet.getFirstResult()
							.getColumn(0));
		}

		return fileCtr;

	}
	
//	public static int countTotalDataObjectsInZone(
//			IRODSAccount irodsAccount, IRODSFileSystem irodsFileSystem) throws JargonException {
//
//		if (irodsAccount == null) {
//			throw new IllegalArgumentException("iRODSAccount is null");
//		}
//		String zone = irodsAccount.getZone();
//		if (zone == null) {
//			throw new IllegalArgumentException("Zone is null");
//		}
//		if (irodsFileSystem == null) {
//			throw new IllegalArgumentException("iRODSFileSystem is null");
//		}

//		ObjStat objStat = retrieveObjectStatForPath(absolutePathToParent);

//		if (objStat == null) {
//			log.error("no file found for path:{}", absolutePathToParent);
//			throw new FileNotFoundException("no file found for given path");
//		}

		/*
		 * See if jargon supports the given object type
		 */
//		MiscIRODSUtils.evaluateSpecCollSupport(objStat);
//
//		String effectiveAbsolutePath = MiscIRODSUtils
//				.determineAbsolutePathBasedOnCollTypeInObjectStat(objStat);
//		log.info("determined effectiveAbsolutePathToBe:{}",
//				effectiveAbsolutePath);
//
//		// I cannot get children if this is not a directory (a file has no
//		// children)
//		if (!objStat.isSomeTypeOfCollection()) {
//			log.error(
//					"this is a file, not a directory, and therefore I cannot get a count of the children: {}",
//					absolutePathToParent);
//			throw new JargonException(
//					"attempting to count children under a file at path:"
//							+ absolutePathToParent);
//		}
//
//		IRODSGenQueryExecutor irodsGenQueryExecutor = new IRODSGenQueryExecutorImpl(
//				irodsFileSystem.getIrodsSession(), irodsAccount);
//
//		StringBuilder query = new StringBuilder();
//		query.append("SELECT COUNT(");
//		query.append(RodsGenQueryEnum.COL_DATA_NAME.getName());
//
//		query.append(") WHERE ");
//		query.append(RodsGenQueryEnum.COL_D_OWNER_ZONE.getName());
//		query.append(" = '");
//		query.append(IRODSDataConversionUtil
//				.escapeSingleQuotes(zone));
//		query.append("'");
//
//		IRODSGenQuery irodsQuery = IRODSGenQuery.instance(query.toString(), 1);
//		IRODSQueryResultSetInterface resultSet;
//
//		try {
//			resultSet = irodsGenQueryExecutor
//					.executeIRODSQueryAndCloseResultInZone(irodsQuery, 0, zone);
//		} catch (JargonQueryException e) {
//			throw new JargonException(e);
//		}
//
//		int fileCtr = 0;
//
//		if (resultSet.getResults().size() > 0) {
//			fileCtr = IRODSDataConversionUtil
//					.getIntOrZeroFromIRODSValue(resultSet.getFirstResult()
//							.getColumn(0));
//		}
//
//		return fileCtr;
//
//	}
	
	public static int countDataObjectsInZone(
			Boolean inTrash,
			IRODSAccount irodsAccount,
			IRODSFileSystem irodsFileSystem,
			String user,
			String resource) throws JargonException {

		if (irodsAccount == null) {
			throw new IllegalArgumentException("iRODSAccount is null");
		}
		String zone = irodsAccount.getZone();
		if (zone == null) {
			throw new IllegalArgumentException("Zone is null");
		}
		if (irodsFileSystem == null) {
			throw new IllegalArgumentException("iRODSFileSystem is null");
		}

		final String trashPath = "/" + zone + "/trash/home/%";
		final String zonePath = "/" + zone + "/%";

		IRODSGenQueryExecutor irodsGenQueryExecutor = new IRODSGenQueryExecutorImpl(
				irodsFileSystem.getIrodsSession(), irodsAccount);
		
		StringBuilder query = new StringBuilder();
		query.append("SELECT COUNT(");
		query.append(RodsGenQueryEnum.COL_DATA_NAME.getName());

		query.append(") WHERE ");
		
		// specify the zone by zone path - cannot use COL_D_OWNER_ZONE here because data objects
		// in this zone could be owned by users in federated zones
		query.append(RodsGenQueryEnum.COL_COLL_NAME.getName());		
		query.append(" LIKE '");
		query.append(IRODSDataConversionUtil
				.escapeSingleQuotes(zonePath));
		query.append("'");
		
		// just count data objects in this zone that are in the trash		
		if (inTrash) {
			query.append(" AND ");
			query.append(RodsGenQueryEnum.COL_COLL_NAME.getName());
			query.append(" LIKE '");
			query.append(IRODSDataConversionUtil
					.escapeSingleQuotes(trashPath));
			query.append("'");
		}
		
		// add user to query if specified
		if ( user != null) {
			query.append(" AND ");
			query.append(RodsGenQueryEnum.COL_D_OWNER_NAME.getName());
			query.append(" = '");
			query.append(IRODSDataConversionUtil
					.escapeSingleQuotes(user));
			query.append("'");
		}
		
		// add resource to query if specified
		if ( resource != null) {
			query.append(" AND ");
			query.append(RodsGenQueryEnum.COL_D_RESC_NAME.getName());
			query.append(" = '");
			query.append(IRODSDataConversionUtil
					.escapeSingleQuotes(resource));
			query.append("'");
		}

		IRODSGenQuery irodsQuery = IRODSGenQuery.instance(query.toString(), 1);
		IRODSQueryResultSetInterface resultSet;

		try {
			resultSet = irodsGenQueryExecutor
					.executeIRODSQueryAndCloseResultInZone(irodsQuery, 0, zone);
		} catch (JargonQueryException e) {
			throw new JargonException(e);
		}

		int fileCtr = 0;

		if (resultSet.getResults().size() > 0) {
			fileCtr = IRODSDataConversionUtil
					.getIntOrZeroFromIRODSValue(resultSet.getFirstResult()
							.getColumn(0));
		}

		return fileCtr;

	}
	
	public static int countCollectionsInZone(
			Boolean inTrash,
			IRODSAccount irodsAccount,
			IRODSFileSystem irodsFileSystem,
			String user) throws JargonException {

		if (irodsAccount == null) {
			throw new IllegalArgumentException("iRODSAccount is null");
		}
		String zone = irodsAccount.getZone();
		if (zone == null) {
			throw new IllegalArgumentException("Zone is null");
		}
		if (irodsFileSystem == null) {
			throw new IllegalArgumentException("iRODSFileSystem is null");
		}

		final String trashPath = "/" + zone + "/trash/home/%";
		final String zonePath = "/" + zone + "/%";

		IRODSGenQueryExecutor irodsGenQueryExecutor = new IRODSGenQueryExecutorImpl(
				irodsFileSystem.getIrodsSession(), irodsAccount);
		StringBuilder query = new StringBuilder();
		query.append("SELECT COUNT(");
		query.append(RodsGenQueryEnum.COL_COLL_NAME.getName());
		
		query.append(") WHERE ");

		// specify the zone by zone path - cannot use COL_D_OWNER_ZONE here because collections
		// in this zone could be owned by users in federated zones
		query.append(RodsGenQueryEnum.COL_COLL_NAME.getName());		
		query.append(" LIKE '");
		query.append(IRODSDataConversionUtil
				.escapeSingleQuotes(zonePath));
		query.append("'");
		
		// just count collections in this zone that are in the trash
		if (inTrash) {
			query.append(" AND ");
			query.append(RodsGenQueryEnum.COL_COLL_NAME.getName());
			query.append(" LIKE '");
			query.append(IRODSDataConversionUtil
					.escapeSingleQuotes(trashPath));
			query.append("'");
		}

		
		// add user to query if specified
		if ( user != null) {
			query.append(" AND ");
			query.append(RodsGenQueryEnum.COL_COLL_OWNER_NAME.getName());
			query.append(" = '");
			query.append(IRODSDataConversionUtil
					.escapeSingleQuotes(user));
			query.append("'");
		}

		IRODSGenQuery irodsQuery = IRODSGenQuery.instance(query.toString(), 1);
		IRODSQueryResultSetInterface resultSet;

		try {
			resultSet = irodsGenQueryExecutor
					.executeIRODSQueryAndCloseResultInZone(irodsQuery, 0, zone);
		} catch (JargonQueryException e) {
			throw new JargonException(e);
		}

		int fileCtr = 0;

		if (resultSet.getResults().size() > 0) {
			fileCtr = IRODSDataConversionUtil
					.getIntOrZeroFromIRODSValue(resultSet.getFirstResult()
							.getColumn(0));
		}

		return fileCtr;

	}
	
	public static int countCollectionsWithObjectsInZone(
			Boolean inTrash,
			IRODSAccount irodsAccount,
			IRODSFileSystem irodsFileSystem,
			String user) throws JargonException {

		if (irodsAccount == null) {
			throw new IllegalArgumentException("iRODSAccount is null");
		}
		String zone = irodsAccount.getZone();
		if (zone == null) {
			throw new IllegalArgumentException("Zone is null");
		}
		if (irodsFileSystem == null) {
			throw new IllegalArgumentException("iRODSFileSystem is null");
		}

		int fileCtr = -1;
		String specificQueryAlias = "showCollectionsWithObjectsInZone";
		
		EnvironmentalInfoAO environmentalInfoAO = irodsFileSystem
				.getIRODSAccessObjectFactory().getEnvironmentalInfoAO(
						irodsAccount);
		
		if (environmentalInfoAO.isAbleToRunSpecificQuery()) {

			final String trashPath = "%/trash/home/%";
			final String zonePath = "/" + zone + "/%";
			
			List<String> args = new ArrayList<String>();
			args.add(zonePath);
			
			StringBuilder query = new StringBuilder();
			query.append("select count(distinct R_DATA_MAIN.coll_id ) from R_COLL_MAIN,R_DATA_MAIN ");
			query.append("where R_DATA_MAIN.coll_id = R_COLL_MAIN.coll_id ");
			// specify the zone by zone path - cannot use COL_D_OWNER_ZONE here because collections
			// in this zone could be owned by users in federated zones
			query.append("and R_COLL_MAIN.coll_name like ?");
				
			if (inTrash) {
				query.append(" and R_COLL_MAIN.coll_name like ?");
				
				specificQueryAlias += "InTrash";
				args.add(trashPath);
			}
			
			// add user to query if specified
			if ( user != null) {
				query.append(" and  R_COLL_MAIN.coll_owner_name = ?");
				
				specificQueryAlias += "WithUser";
				args.add(user);
			}
			
			// first look to see if this specific query has already been created
			SpecificQueryAO queryAO = irodsFileSystem.getIRODSAccessObjectFactory()
					.getSpecificQueryAO(irodsAccount);
			
			// if this specific query does not exist, create it
			SpecificQueryDefinition specificQueryDef = null;
			try {
				specificQueryDef = queryAO.findSpecificQueryByAlias(specificQueryAlias);
			} catch(DataNotFoundException ex) {
				specificQueryDef = new SpecificQueryDefinition(specificQueryAlias, query.toString());
				queryAO.addSpecificQuery(specificQueryDef);
			}

			// now run it
			SpecificQuery specificQuery = SpecificQuery.instanceArguments(specificQueryAlias, args, 0);
			SpecificQueryResultSet specificQueryResultSet = null;;
			try {
				specificQueryResultSet = queryAO.executeSpecificQueryUsingAlias(specificQuery, 1);
			} catch (DataNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return 0;
			} catch (JargonQueryException e) {
				throw new JargonException(e);
			}
			
			if (specificQueryResultSet.getResults().size() > 0) {
				fileCtr = IRODSDataConversionUtil
						.getIntOrZeroFromIRODSValue(specificQueryResultSet.getFirstResult().getColumn(0));
			}
		}
		
		return fileCtr;

	}
}
