package org.irods.scotty.utils;

import org.irods.jargon.core.pub.IRODSGenQueryExecutor;
import org.irods.jargon.core.pub.IRODSGenQueryExecutorImpl;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.pub.SimpleQueryExecutorAO;
import org.irods.jargon.core.query.IRODSGenQuery;
import org.irods.jargon.core.query.IRODSQueryResultSetInterface;
import org.irods.jargon.core.query.JargonQueryException;
import org.irods.jargon.core.query.RodsGenQueryEnum;
import org.irods.jargon.core.query.SimpleQuery;
import org.irods.jargon.core.utils.IRODSDataConversionUtil;
import org.irods.jargon.core.connection.IRODSAccount;
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

		IRODSGenQueryExecutor irodsGenQueryExecutor = new IRODSGenQueryExecutorImpl(
				irodsFileSystem.getIrodsSession(), irodsAccount);

		StringBuilder query = new StringBuilder();
		query.append("SELECT SUM(");
		query.append(RodsGenQueryEnum.COL_DATA_SIZE.getName());

		query.append(") WHERE ");
		query.append(RodsGenQueryEnum.COL_D_OWNER_ZONE.getName());
		query.append(" = '");
		query.append(IRODSDataConversionUtil
				.escapeSingleQuotes(zone));
		query.append("'");
		
		query.append(" AND ");
		query.append(RodsGenQueryEnum.COL_D_DATA_PATH.getName());
		
		if (inTrash) {
			query.append(" LIKE '");
		}
		else {
			query.append(" NOT LIKE '");
		}
		query.append(IRODSDataConversionUtil
				.escapeSingleQuotes(trashPath));
		query.append("'");
		
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
	
	public static int countTotalDataObjectsInZone(
			IRODSAccount irodsAccount, IRODSFileSystem irodsFileSystem) throws JargonException {

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

		IRODSGenQueryExecutor irodsGenQueryExecutor = new IRODSGenQueryExecutorImpl(
				irodsFileSystem.getIrodsSession(), irodsAccount);

		StringBuilder query = new StringBuilder();
		query.append("SELECT COUNT(");
		query.append(RodsGenQueryEnum.COL_DATA_NAME.getName());

		query.append(") WHERE ");
		query.append(RodsGenQueryEnum.COL_D_OWNER_ZONE.getName());
		query.append(" = '");
		query.append(IRODSDataConversionUtil
				.escapeSingleQuotes(zone));
		query.append("'");

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

		final String trashPath = "%/trash/home/%";

		IRODSGenQueryExecutor irodsGenQueryExecutor = new IRODSGenQueryExecutorImpl(
				irodsFileSystem.getIrodsSession(), irodsAccount);
		
		StringBuilder query = new StringBuilder();
		query.append("SELECT COUNT(");
		query.append(RodsGenQueryEnum.COL_DATA_NAME.getName());

		query.append(") WHERE ");
		query.append(RodsGenQueryEnum.COL_D_OWNER_ZONE.getName());
		query.append(" = '");
		query.append(IRODSDataConversionUtil
				.escapeSingleQuotes(zone));
		query.append("'");
		
		query.append(" AND ");
		query.append(RodsGenQueryEnum.COL_D_DATA_PATH.getName());
		
		if (inTrash) {
			query.append(" LIKE '");
		}
		else {
			query.append(" NOT LIKE '");
		}
		query.append(IRODSDataConversionUtil
				.escapeSingleQuotes(trashPath));
		query.append("'");
		
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

		final String trashPath = "%/" + zone + "/trash%";

		IRODSGenQueryExecutor irodsGenQueryExecutor = new IRODSGenQueryExecutorImpl(
				irodsFileSystem.getIrodsSession(), irodsAccount);
		StringBuilder query = new StringBuilder();
		query.append("SELECT COUNT(");
		query.append(RodsGenQueryEnum.COL_COLL_NAME.getName());

		query.append(") WHERE ");
		query.append(RodsGenQueryEnum.COL_COLL_OWNER_ZONE.getName());
		query.append(" = '");
		query.append(IRODSDataConversionUtil
				.escapeSingleQuotes(zone));
		query.append("'");
		
		query.append(" AND ");
		query.append(RodsGenQueryEnum.COL_COLL_NAME.getName());
		
		if (inTrash) {
			query.append(" LIKE '");
		}
		else {
			query.append(" NOT LIKE '");
		}
		query.append(IRODSDataConversionUtil
				.escapeSingleQuotes(trashPath));
		query.append("'");
		
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
	
	// this does not work yet - keep getting -816000 from irods
//	public static int countCollectionsWithObjectsUnderZoneWithoutTrash2(
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
//		
//		//final String trashPath = "%/trash/home/%";
//		//final String querySQL = "select count(COLL_ID) from R_DATA_MAIN where DATA_OWNER_ZONE=? and DATA_PATH not like '%/trash/home/%'";
//		final String querySQL = "select COLL_ID from R_DATA_MAIN where DATA_OWNER_ZONE='tempZone'";
//		SimpleQueryExecutorAO simpleQueryExecutorAO = irodsFileSystem
//			.getIRODSAccessObjectFactory().getSimpleQueryExecutorAO(
//					irodsAccount);
//
//		SimpleQuery simpleQuery = SimpleQuery.instanceWithOneArgument(querySQL, zone, 0);
//		IRODSQueryResultSetInterface resultSet = simpleQueryExecutorAO
//			.executeSimpleQuery(simpleQuery);
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
//	}
	

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

		final String trashPath = "%/trash/home/%";

		// TODO: This query really needs to have the DISTINCT keyword, but don't know how
		// to do that yet since iquest (and therefore jargon GenQuery) does not support it
		// looking into using simple query, but example comment out does not work
		IRODSGenQueryExecutor irodsGenQueryExecutor = new IRODSGenQueryExecutorImpl(
				irodsFileSystem.getIrodsSession(), irodsAccount);
		StringBuilder query = new StringBuilder();
		query.append("SELECT COUNT(");
		query.append(RodsGenQueryEnum.COL_D_COLL_ID.getName());

		query.append(") WHERE ");
		query.append(RodsGenQueryEnum.COL_D_OWNER_ZONE.getName());
		query.append(" = '");
		query.append(IRODSDataConversionUtil
				.escapeSingleQuotes(zone));
		query.append("'");
		
		query.append(" AND ");
		query.append(RodsGenQueryEnum.COL_D_DATA_PATH.getName());
		
		if (inTrash) {
			query.append(" LIKE '");
		}
		else {
			query.append(" NOT LIKE '");
		}
		query.append(IRODSDataConversionUtil
				.escapeSingleQuotes(trashPath));
		query.append("'");
		
		// add user to query if specified
		if ( user != null) {
			query.append(" AND ");
			query.append(RodsGenQueryEnum.COL_D_OWNER_NAME.getName());
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


}
