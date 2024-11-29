package com.monster.npd.governance.pdf.config;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.monster.npd.governance.pdf.utils.Utils;

public class CustomPhysicalNamingStrategy implements PhysicalNamingStrategy {

	private static Logger logger = LoggerFactory.getLogger(CustomPhysicalNamingStrategy.class);
	
	private static final String GOVERNANCE_AUDIT ="GOVERNANCE_AUDIT"; 
	private static final String IDENTITY_TABLE ="opentextentityidentitycomponentsidentity";

	@Value("${npd.table.prefix}")
	private String PREFIX;

	@Value("${npd.request.deliverableThreshold}")
	public String deliverableThreshold;

	@Value("${npd.request.governanceMilestone}")
	public String governanceMilestone;

	@Value("${npd.request.commercialMarketScope}")
	public String commercialMarketScope;

	@Value("${npd.request.requiredUserOrganizationid}")
	public String UserRequiredORG_ID;

	@Override
	public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment context) {
		if (name == null || name.getText() == null || Utils.isNullOrEmptyString(UserRequiredORG_ID)) {
			logger.error("Input Identifier or its text is null.");
			throw new IllegalArgumentException("Input Identifier or its text cannot be null");
		}

		String tableName = name.getText();

		if (tableName.toLowerCase().contains(IDENTITY_TABLE)) {
			String tableNameWithPrefix = UserRequiredORG_ID + tableName;
			logger.debug("Returning table name with UserRequiredORG_ID prefix: {}", tableNameWithPrefix);
			return Identifier.toIdentifier(tableNameWithPrefix);
		} else if (name.getText().equalsIgnoreCase(GOVERNANCE_AUDIT)) {
			return name;
		}
		String tableNameWithPrefix = PREFIX + tableName;
		logger.debug("Returning table name with default prefix: {}", tableNameWithPrefix);

		return Identifier.toIdentifier(tableNameWithPrefix);
	}

	@Override
	public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment context) {
		return convertColumnName(name);
	}

	@Override
	public Identifier toPhysicalSequenceName(Identifier name, JdbcEnvironment context) {
		return name;
	}

	@Override
	public Identifier toPhysicalSchemaName(Identifier name, JdbcEnvironment context) {
		return name;
	}

	@Override
	public Identifier toPhysicalCatalogName(Identifier name, JdbcEnvironment jdbcEnvironment) {
		return null;
	}

	private Identifier convertColumnName(Identifier name) throws NullPointerException {

		if (Utils.isNullOrEmptyString(name.getText())) {
			logger.info(PREFIX, "{}", name.getText());
			throw new NullPointerException("Empty column name.");

		}
		String columnName = name.getText();
		// identifiers
		Map<String, String> columnMapping = new HashMap<>();
		columnMapping.put("RequestIdA26399F71487410E", governanceMilestone);
		columnMapping.put("RequestId851FE0BB24A194E2", deliverableThreshold);
		columnMapping.put("RequestId93C5C6F18B8EA853", commercialMarketScope);

		// corresponding identifier
		for (Map.Entry<String, String> entry : columnMapping.entrySet()) {
			if (columnName.contains(entry.getKey())) {
				return Identifier.toIdentifier(entry.getValue());
			}
		}
		return name;
	}

}
