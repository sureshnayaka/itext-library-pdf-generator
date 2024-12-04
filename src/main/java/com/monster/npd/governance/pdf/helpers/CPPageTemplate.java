package com.monster.npd.governance.pdf.helpers;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.monster.npd.governance.pdf.pojo.DeliverableThreshold;
import com.monster.npd.governance.pdf.pojo.DeliverableThresholdDTO;
import com.monster.npd.governance.pdf.pojo.MarketScope;
import com.monster.npd.governance.pdf.pojo.MarketScopeDTO;
import com.monster.npd.governance.pdf.pojo.MarketScopeSummary;
import com.monster.npd.governance.pdf.pojo.SubmissionRequest;
import com.monster.npd.governance.pdf.table.config.DeliverableThresholdTableFieldConfig;
import com.monster.npd.governance.pdf.table.config.MarketScopeTableFieldConfig;
import com.monster.npd.governance.pdf.table.config.PortfolioTableFieldConfig;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.*;

@Component
public class CPPageTemplate {

	private static final Logger logger = LogManager.getLogger(CPPageTemplate.class);

	private static final String MARKET_SCOPE_TITLE = "Market-scope";
	private static final String DELIVERABLE_THRESHOLD_TITLE = "Deliverables V/S Thresholds:";
	private static final String PORTFOLIO_TITLE = "Portfolio Strategy :";
	private static final String DEFAULT_VALUE = "N/A";
	private static final String THIS_PROJECT = "This Project";
	private static final String MONSTER_GREEN = "% of Monster Green";

	@Autowired
	private CpDataProcessor cpHelper;

	@Autowired
	private MarketScopeTableFieldConfig fieldConfig;

	@Autowired
	private DeliverableThresholdTableFieldConfig deliverableThresholdTableFieldConfig;

	@Autowired
	private PortfolioTableFieldConfig portfolioTableFieldConfig;

	/**
	 * 
	 * @param document
	 * @param requestId
	 * @param cpName
	 * @throws DocumentException
	 * @Description Method used to create the table on the pdf for market scope with
	 *              records.
	 */
	public void addMarketScopeTableForCp(Document document, long requestId, String cpName) throws DocumentException {

		float[] columnWidths = { 3f, 2f, 2.5f, 3.5f, 3f, 2f, 2f, 3.5f, 3f, 3.5f, 1.5f, 3f };

		List<MarketScope> commercialMarketScopes = cpHelper.getMarketScopeById(requestId).stream()
				.filter(dto -> dto.getCpName().equals(cpName)).findFirst().map(MarketScopeDTO::getMarketScope)
				.orElseThrow(() -> new RuntimeException("cpName not found: {}".concat(cpName)));

		String[][] rowData = getTableContentForMarketScope(commercialMarketScopes);

		String[] headers = fieldConfig.getHeaders().toArray(new String[0]);

		PDfGenerationHelpers.addTableData(document, MARKET_SCOPE_TITLE, columnWidths, rowData, headers, false);

	}

	public void addDeliverablesVSThresholdForCp(Document document, long requestId, String cpName)
			throws DocumentException {

		float[] columnWidths = { 2.5f, 2.5f, 2.5f, 2.5f, 2f, 2f, 2f };

		List<DeliverableThreshold> commercialDeliveralbeThreshold = cpHelper.getDeliverableThresholdById(requestId)
				.stream().filter(dto -> dto.getCpName().equals(cpName)).findFirst()
				.map(DeliverableThresholdDTO::getDeliverableThresholds)
				.orElseThrow(() -> new RuntimeException("cpName not found: {}".concat(cpName)));

		String[][] rowData = getTableContentForDelveriableThreshold(commercialDeliveralbeThreshold);

		String[] headers = deliverableThresholdTableFieldConfig.getHeaders().toArray(new String[0]);

		PDfGenerationHelpers.addTableData(document, DELIVERABLE_THRESHOLD_TITLE, columnWidths, rowData, headers, false);
	}

	public void addPortFolioStartegyForCp0(Document document, long requestId) throws DocumentException {

		float[] columnWidths = { 3.5f, 3.5f, 3f, 3f, 5f, };

		String[] headers = portfolioTableFieldConfig.getHeaders().toArray(new String[0]);

		Optional<SubmissionRequest> submissionRequestOpt = cpHelper.getProjectDetailsForCP0(requestId);

		submissionRequestOpt.ifPresent(submissionRequest -> {
			String[][] rowData = {
					{ getValueOrDefault(Optional.ofNullable(submissionRequest.getIncrementalReplacementSku())),
							getValueOrDefault(Optional.ofNullable(submissionRequest.getPortfolioDelistStrategy())),
							getValueOrDefault(Optional.ofNullable(submissionRequest.getSpecificSkuCutOffIntro())),
							getValueOrDefault(
									Optional.ofNullable(submissionRequest.getSwitchDateAndDrivingDateReason())),
							getValueOrDefault(Optional.ofNullable(submissionRequest.getCommercialStrategy())) } };

			PDfGenerationHelpers.addTableData(document, PORTFOLIO_TITLE, columnWidths, rowData, headers, false);
		});

	}

	public void addAnualizedSummaryCheckPoints(Document document, long requestId, String cpName)
			throws DocumentException {

		float[] columnWidths = { 2.5f, 2.5f, 2.5f, 2.5f, 2f, 2f };

		List<MarketScopeDTO> commercialMarketScopes = cpHelper.getMarketScopeById(requestId);
		Set<String> targetCpNames = CheckpointHandler.getCPSummary(cpName);

		List<MarketScopeSummary> leadeMarketScope = commercialMarketScopes.stream()
				.filter(marketScopeDTO -> targetCpNames.contains(marketScopeDTO.getCpName()))
				.flatMap(marketScopeDTO -> marketScopeDTO.getMarketScope().stream()
						.filter(marketScope -> Boolean.TRUE.equals(marketScope.getLeadMarket()))
						.map(marketScope -> new MarketScopeSummary(marketScopeDTO.getCpName(),
								marketScope.getAnnualisedYear1Volume(), null,
								marketScope.getAnnualisedYear1NSVLocalCurrency(), marketScope.getGrossMargin(), null)))
				.collect(Collectors.toList());

		System.out.println("leadeMarketScope contains " + leadeMarketScope.size() + " items");
		String[][] rowData = getTableContentForCpSummary(leadeMarketScope);
		// String[][] rowData = {{"0","100000","n/a"," ", "", ""}, {"0","100000","40","
		// ", "", ""}};
		String[] headers = { "Checkpoint ", "Annualised Vol [24 Eq cases]", "% Volume v/s Prior CP",
				"Anualised NSV [€]", "GM%", "Aligned DP Date" };
		PDfGenerationHelpers.addTableData(document, "Summary changes versus last checkpoint", columnWidths, rowData,
				headers, true);

	}

	// Helper function to handle null or empty checks using Optional
	public String getValueOrDefault(Optional<String> value) {
		return value.filter(v -> !v.trim().isEmpty()).orElse(DEFAULT_VALUE);
	}

	public String[][] getTableContentForMarketScope(List<MarketScope> commercialMarketScopes) {
		Function<Object, String> defaultValue = value -> Optional.ofNullable(value).map(String::valueOf)
				.orElse(DEFAULT_VALUE);

		Function<MarketScope, String> leadMarketValue = scope -> Boolean.TRUE.equals(scope.getLeadMarket()) ? "Y" : "N";
		return commercialMarketScopes.stream()
				.map(scope -> new String[] { "Great Britain", leadMarketValue.apply(scope),
						processNumber(scope.getThreeMonthLaunchVolume()),
						processNumber(scope.getAnnualisedYear1Volume()),
						processNumber(scope.getCannibalisationImpact()), processNumber(scope.getNsvCase()),
						processNumber(scope.getCogCase()), processNumber(scope.getAnnualisedYear1NSVLocalCurrency()),
						processNumber(scope.getAnnualisedYear1NSVEuro()),
						processNumber(scope.getGrossProfit()).concat("€"), processNumber(scope.getGrossMargin()).concat("%"),
						defaultValue.apply(scope.getTargetDPInWarehouse()) })
				.toArray(String[][]::new);
	}

	public String[][] getTableContentForDelveriableThreshold(List<DeliverableThreshold> deliverableThresholds) {

		AtomicInteger index = new AtomicInteger(0);
		return deliverableThresholds.stream()
				.map(scope -> new String[] { index.getAndIncrement() == 0 ? THIS_PROJECT : MONSTER_GREEN,
						processNumber(scope.getNsvPerCase()), processNumber(scope.getGm()) + "%",
						processNumber(scope.getCogs()), processNumber(scope.getVolume()), processNumber(scope.getRos()),
						processNumber(scope.getDistribution()) })
				.toArray(String[][]::new);
	}

	public String[][] getTableContentForCpSummary(List<MarketScopeSummary> marketScopeSummaries) {
		Function<Object, String> defaultValue = value -> Optional.ofNullable(value).map(String::valueOf)
				.orElse(DEFAULT_VALUE);

		return marketScopeSummaries.stream()
				.map(scope -> new String[] { defaultValue.apply(Integer.parseInt(scope.getCpName().substring(2))),
						processNumber(scope.getAnualizedVolume()), processNumber(scope.getVolumeChange()),
						processNumber(scope.getAnualisedNsv()), processNumber(scope.getGmPercentage()),
						defaultValue.apply(scope.getAlignedDP()) })
				.toArray(String[][]::new);
	}

	public static String processNumber(Object value) {
		if (value == null || value.toString().isEmpty()) {
			return "0.00"; // Default for null or empty
		}
		try {
			double numericValue = Double.parseDouble(value.toString());
			if (Math.abs(numericValue) > 1000) {
				return String.format("%,.2f", numericValue); // Format as "1,000.00" with two decimals
			} else {
				return String.format("%.2f", numericValue); // Format as "0.00" with two decimals
			}
		} catch (NumberFormatException e) {
			return "0.00"; // Fallback for invalid number
		}
	}

}
