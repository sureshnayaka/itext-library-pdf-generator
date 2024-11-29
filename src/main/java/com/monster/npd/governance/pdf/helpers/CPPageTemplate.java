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

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.monster.npd.governance.pdf.pojo.DeliverableThreshold;
import com.monster.npd.governance.pdf.pojo.DeliverableThresholdDTO;
import com.monster.npd.governance.pdf.pojo.MarketScope;
import com.monster.npd.governance.pdf.pojo.MarketScopeDTO;
import com.monster.npd.governance.pdf.pojo.MarketScopeSummary;
import com.monster.npd.governance.pdf.pojo.SubmissionRequest;
import com.monster.npd.governance.pdf.table.config.DeliverableThresholdTableFieldConfig;
import com.monster.npd.governance.pdf.table.config.MarketScopeTableFieldConfig;
import com.monster.npd.governance.pdf.table.config.PortfolioTableFieldConfig;

@Component
public class CPPageTemplate {

	private static final Logger logger = LogManager.getLogger(CPPageTemplate.class);



	private static final String MARKET_SCOPE_TITLE =  "Market-scope";
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

		float[] columnWidths = { 3f, 2f, 3f, 3f, 2.5f, 2f, 2f, 2.5f, 3f, 3.5f, 3.8f, 3.5f };

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

		PDfGenerationHelpers.addTableData(document, DELIVERABLE_THRESHOLD_TITLE, columnWidths, rowData, headers,
				false);
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
		Function<Object, String> defaultValue = value -> Optional.ofNullable(value).map(String::valueOf).orElse(DEFAULT_VALUE);

		return commercialMarketScopes.stream().map(scope -> new String[] { DEFAULT_VALUE,
				defaultValue.apply(scope.getLeadMarket()), defaultValue.apply(scope.getThreeMonthLaunchVolume()),
				defaultValue.apply(scope.getAnnualisedYear1Volume()),
				defaultValue.apply(scope.getCannibalisationImpact()), defaultValue.apply(scope.getNsvCase()),
				defaultValue.apply(scope.getCogCase()), defaultValue.apply(scope.getAnnualisedYear1NSVLocalCurrency()),
//				defaultValue.apply(scope.getEnterCurrencyDirectlyInEuros()),
				defaultValue.apply(scope.getAnnualisedYear1NSVEuro()), defaultValue.apply(scope.getGrossProfit()),
				defaultValue.apply(scope.getGrossMargin()), defaultValue.apply(scope.getTargetDPInWarehouse()) })
				.toArray(String[][]::new);
	}

	public String[][] getTableContentForDelveriableThreshold(List<DeliverableThreshold> deliverableThresholds) {
		Function<Object, String> defaultValue = value -> Optional.ofNullable(value).map(String::valueOf).orElse(DEFAULT_VALUE);
		AtomicInteger index = new AtomicInteger(0);
		return deliverableThresholds.stream()
				.map(scope -> new String[] { index.getAndIncrement() == 0 ? THIS_PROJECT : MONSTER_GREEN,
						defaultValue.apply(scope.getNsvPerCase()), defaultValue.apply(scope.getGm()),
						defaultValue.apply(scope.getCogs()), defaultValue.apply(scope.getVolume()),
						defaultValue.apply(scope.getRos()), defaultValue.apply(scope.getDistribution()) })
				.toArray(String[][]::new);
	}

	public String[][] getTableContentForCpSummary(List<MarketScopeSummary> marketScopeSummaries) {
		Function<Object, String> defaultValue = value -> Optional.ofNullable(value).map(String::valueOf).orElse(DEFAULT_VALUE);
		return marketScopeSummaries.stream()
				.map(scope -> new String[] { defaultValue.apply(Integer.parseInt(scope.getCpName().substring(2))),
						defaultValue.apply(scope.getAnualizedVolume()), defaultValue.apply(scope.getVolumeChange()),
						defaultValue.apply(scope.getAnualisedNsv()), defaultValue.apply(scope.getGmPercentage()),
						defaultValue.apply(scope.getAlignedDP()) })
				.toArray(String[][]::new);
	}
}
