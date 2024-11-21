package com.monster.npd.governance.pdf.helpers;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

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
import com.monster.npd.governance.pdf.pojo.SubmissionRequest;

@Component
public class CPPageTemplate {

	private static final Logger logger = LogManager.getLogger(CPPageTemplate.class);

	@Autowired
	private CpDataProcessor cpHelper;

	public void addMarketScopeTableForCp(Document document, long requestId, String cpName) throws DocumentException {

		float[] columnWidths = { 3f, 2f, 3f, 3f, 2.5f, 2f, 2f, 3f, 2.5f, 3f, 3.5f, 3.8f, 3.5f };

		List<MarketScope> commercialMarketScopes = cpHelper.getMarketScopeById(requestId).stream()
				.filter(dto -> dto.getCpName().equals(cpName)).findFirst().map(MarketScopeDTO::getMarketScope)
				.orElseThrow(() -> new RuntimeException("cpName not found: {}".concat(cpName)));

		String[][] rowData = getTableContentForMarketScope(commercialMarketScopes);

		String[] headers = { "Harmonised Market (S) ", "Lead Market", "3 Month Launch Volume (24 EQ)",
				"Annualised Year 1 Volume (24 EQ)", "Cannabalisation Impact(Total Annual Cases)", "NSV / Case",
				"COG'S / Case", "Annualised Year 1 NSV (Local Currency)", "Currency in Euros?",
				"Annualised Year 1 NSV (EURO)", "Gross Profit", "Gross Margin", "Target DP in Warehouse Week/Year" };

		PDfGenerationHelpers.addTableData(document, "Market-scope", columnWidths, rowData, headers);

	}

	public void addDeliverablesVSThresholdForCp(Document document, long requestId, String cpName)
			throws DocumentException {

		float[] columnWidths = { 2.5f, 2.5f, 2.5f, 2.5f, 2f, 2f, 2f };

		List<DeliverableThreshold> commercialDeliveralbeThreshold = cpHelper.getDeliverableThresholdById(requestId)
				.stream().filter(dto -> dto.getCpName().equals(cpName)).findFirst()
				.map(DeliverableThresholdDTO::getDeliverableThresholds)
				.orElseThrow(() -> new RuntimeException("cpName not found: {}".concat(cpName)));

		String[][] rowData = getTableContentForDelveriableThreshold(commercialDeliveralbeThreshold);

		String[] headers = { " ", "NSV Per Case", "GM %", "Target COG`S", "Total Volume", "Unit ROS (UROS)",
				"Numerical Distribution (ND)" };
		PDfGenerationHelpers.addTableData(document, "Deliverables V/S Thresholds:", columnWidths, rowData, headers);
	}

	public void addPortFolioStartegyForCp0(Document document, long requestId) throws DocumentException {

		float[] columnWidths = { 3.5f, 3.5f, 4f, 5f, 3f, };

		String[] headers = { "Incremental Or Replacement SKU?", "What Is The Portfolio Delist Strategy?",
				"Specific Cut Off Date For Introduction Of New SKU?", "What is driving your launch date?",
				"Commercial Strategy(Consumer Price Proposal)" };
		
		Optional<SubmissionRequest> submissionRequestOpt = cpHelper.getProjectDetailsForCP0(requestId);
		
		submissionRequestOpt.ifPresent(submissionRequest -> {
			String[][] rowData = {
					{ getValueOrDefault(Optional.ofNullable(submissionRequest.getIncrementalReplacementSku())),
							getValueOrDefault(Optional.ofNullable(submissionRequest.getPortfolioDelistStrategy())),
							getValueOrDefault(Optional.ofNullable(submissionRequest.getSpecificSkuCutOffIntro())),
							getValueOrDefault(
									Optional.ofNullable(submissionRequest.getSwitchDateAndDrivingDateReason())),
							getValueOrDefault(Optional.ofNullable(submissionRequest.getCommercialStrategy())) } };

			PDfGenerationHelpers.addTableData(document, "Portfolio Strategy :", columnWidths, rowData, headers);
		});

	}

	// Helper function to handle null or empty checks using Optional
	public String getValueOrDefault(Optional<String> value) {
		return value.filter(v -> !v.trim().isEmpty()).orElse("_");
	}

	public String[][] getTableContentForMarketScope(List<MarketScope> commercialMarketScopes) {
		Function<Object, String> defaultValue = value -> Optional.ofNullable(value).map(String::valueOf).orElse("_");

		return commercialMarketScopes.stream().map(scope -> new String[] { "Default Name",
				defaultValue.apply(scope.getLeadMarket()), defaultValue.apply(scope.getThreeMonthLaunchVolume()),
				defaultValue.apply(scope.getAnnualisedYear1Volume()),
				defaultValue.apply(scope.getCannibalisationImpact()), defaultValue.apply(scope.getNsvCase()),
				defaultValue.apply(scope.getCogCase()), defaultValue.apply(scope.getAnnualisedYear1NSVLocalCurrency()),
				defaultValue.apply(scope.getEnterCurrencyDirectlyInEuros()),
				defaultValue.apply(scope.getAnnualisedYear1NSVEuro()), defaultValue.apply(scope.getGrossProfit()),
				defaultValue.apply(scope.getGrossMargin()), defaultValue.apply(scope.getTargetDPInWarehouse()) })
				.toArray(String[][]::new);
	}

	public String[][] getTableContentForDelveriableThreshold(List<DeliverableThreshold> deliverableThresholds) {
		Function<Object, String> defaultValue = value -> Optional.ofNullable(value).map(String::valueOf).orElse("_");
		AtomicInteger index = new AtomicInteger(0);
		return deliverableThresholds.stream()
				.map(scope -> new String[] { index.getAndIncrement() == 0 ? "This Project" : "% of Monster Green",
						defaultValue.apply(scope.getNsvPerCase()), defaultValue.apply(scope.getGm()),
						defaultValue.apply(scope.getCogs()), defaultValue.apply(scope.getVolume()),
						defaultValue.apply(scope.getRos()), defaultValue.apply(scope.getDistribution()) })
				.toArray(String[][]::new);
	}
}
