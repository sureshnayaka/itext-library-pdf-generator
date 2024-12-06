package com.monster.npd.governance.pdf.helpers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.monster.npd.governance.pdf.pojo.CP;
import com.monster.npd.governance.pdf.pojo.DeliverableThresholdDTO;
import com.monster.npd.governance.pdf.pojo.MarketScope;
import com.monster.npd.governance.pdf.pojo.MarketScopeDTO;
import com.monster.npd.governance.pdf.pojo.SubmissionGovernanceMilestone;
import com.monster.npd.governance.pdf.pojo.SubmissionRequest;
import com.monster.npd.governance.pdf.pojo.SubmissionRequestGovernanceMilestone;
import com.monster.npd.governance.pdf.repository.DeliverableThresholdRepository;
import com.monster.npd.governance.pdf.repository.MarketScopeRepository;
import com.monster.npd.governance.pdf.repository.SubmissionRequestGovernanceMilestoneRepository;
import com.monster.npd.governance.pdf.repository.SubmissionsRequestRepository;
import com.monster.npd.governance.pdf.utils.Utils;

@Component
public class CpDataProcessor {

	private static final Logger logger = LogManager.getLogger(CpDataProcessor.class);

	private static final String APPROVED = "Approved";

	@Autowired
	private SubmissionRequestGovernanceMilestoneRepository submissionRequestGovernanceMilestoneRepository;

	@Autowired
	private MarketScopeRepository commercialMarketScopeRepository;

	@Autowired
	private DeliverableThresholdRepository deliverableThresholdRepository;

	@Autowired
	private SubmissionsRequestRepository submissionRequestRepository;

	/**
	 * @author Suresh
	 * @param requestId
	 * @implNote Method used to fetch the cps from the talbe and from the response
	 *           to print number of cp on the PDF.
	 * @return List<CP>
	 */
	public List<CP> getCPListForPdfGeneration(long requestId) {
	    List<CP> cps = new ArrayList<>();

	    try {
	        List<SubmissionRequestGovernanceMilestone> submissionRequest = submissionRequestGovernanceMilestoneRepository
	                .findByIdRequestId(requestId);

	        if (submissionRequest == null || submissionRequest.isEmpty()) {
	            logger.warn("No SubmissionRequestGovernanceMilestone found for requestId: {}", requestId);
	            return cps; // return empty list if no data found
	        }

	        for (SubmissionRequestGovernanceMilestone request : submissionRequest) {
	            SubmissionGovernanceMilestone governanceMilestone = request.getGovernanceMilestone();
	            if (governanceMilestone == null) {
	                logger.warn("GovernanceMilestone is null for requestId: {}", requestId);
	                continue;
	            }

	            String currentCheckpoint = request.getSubmissionRequest().getCurrentCheckpoint();
	            boolean isActiveCP = Optional.ofNullable(governanceMilestone.getStage())
	                    .map(stage -> stage.contains(currentCheckpoint)).orElse(false);
	            String decision = Optional.ofNullable(governanceMilestone.getDecision()).orElse("");
	            boolean isApprovedCP = APPROVED.equalsIgnoreCase(decision);
	            boolean isSamePrevious = Optional.ofNullable(governanceMilestone.getIsSamePreviousCp()).orElse(false);

	            if (isApprovedCP || isActiveCP || isSamePrevious) {
	                cps.add(new CP(governanceMilestone.getStage(), governanceMilestone.getIsSamePreviousCp(),
	                        isActiveCP, governanceMilestone.getComments(),
	                        governanceMilestone.getProjectManagerComments(),
	                        governanceMilestone.getCommercialRationaleForChanges(),
	                        governanceMilestone.getIncrementalReplacemntalSKU(),
	                        governanceMilestone.getPortfolioDelistStrategy(),
	                        governanceMilestone.getSpecificSKUCutOffIntro(),
	                        governanceMilestone.getSwitchDateAndDrivingDateReason(),
	                        governanceMilestone.getCommercialStrategy()));
	            }
	        }

	    } catch (Exception e) {
	        logger.error("Error occurred while fetching CP data for requestId {}: {}", requestId, e.getMessage(), e);
	    }
	    return cps;
	}
	
 
	/**
	 * @author Suresh
	 * @param governanceId
	 * @return
	 * @implNote Method used to fetch the market scope table records based on the
	 *           governance Id.
	 */
	public List<MarketScopeDTO> getMarketScopeById(long governanceId) {
		return fetchAndMapToDTO(governanceId, commercialMarketScopeRepository::findByPoGovernanceMSId,
				MarketScopeDTO::new);
	}

	/**
	 * @author Suresh
	 * @param governanceId
	 * @return
	 * @implNote Method used to fetch the Deliverable threshold table records based
	 *           on the governance Id.
	 */
	public List<DeliverableThresholdDTO> getDeliverableThresholdById(long governanceId) {
		return fetchAndMapToDTO(governanceId, deliverableThresholdRepository::findByPoGovernanceMSId,
				DeliverableThresholdDTO::new);
	}

	/**
	 * 
	 * @param <T>
	 * @param <R>
	 * @param requestId
	 * @param fetchFunction
	 * @param dtoConstructor
	 * @return
	 * @implNote Helper method used to call repository.
	 */
	private <T, R> List<T> fetchAndMapToDTO(long requestId, Function<Long, List<R>> fetchFunction,
			BiFunction<String, List<R>, T> dtoConstructor) {
		List<SubmissionRequestGovernanceMilestone> submissionRequests = submissionRequestGovernanceMilestoneRepository
				.findByIdRequestId(requestId);

		return submissionRequests.stream().map(request -> {
			Long governanceMilestoneId = request.getId().getGovernanceMilestoneId();
			List<R> data = fetchFunction.apply(governanceMilestoneId);
			String stage = request.getGovernanceMilestone().getStage();
			return dtoConstructor.apply(stage, data);
		}).toList();
	}

	public Optional<SubmissionRequest> getProjectDetailsForCP0(long requestId) {
		return submissionRequestGovernanceMilestoneRepository.findByIdRequestId(requestId).stream().findFirst()
				.map(SubmissionRequestGovernanceMilestone::getSubmissionRequest);
	}

	/**
	 * @author Suresh
	 * @param requestId
	 * @return
	 * @throws Exception
	 * @implNote Method used to fetch the records from submission request table and
	 *           its respective FK table records.
	 */
	public SubmissionRequest getSubmissionRequest(long requestId) throws Exception {
		return submissionRequestRepository.findById(requestId)
				.orElseThrow(() -> new Exception("SubmissionRequest not found with id: " + requestId));
	}

	/**
	 * 
	 * @param requestId
	 * @param cpName
	 * @return
	 * @implNote Method used to get the CP approved date for the current cp.
	 */
	public List<SubmissionRequestGovernanceMilestone> getCPApprovedDate(long requestId, String cpName) {
		return submissionRequestGovernanceMilestoneRepository.findByIdRequestId(requestId).stream().filter(request -> {
			String decision = request.getGovernanceMilestone().getDecision();
			boolean isApproved = APPROVED.equalsIgnoreCase(decision);

			return isApproved && request.getGovernanceMilestone().getStage().equalsIgnoreCase(cpName);
		}).toList();
	}

	public static String extractDate(String dateTimeString) {
		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
		DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

		// Parse the string to LocalDateTime
		LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, inputFormatter);

		// Format it to only the date
		return dateTime.format(outputFormatter);
	}

	public String getHeaderValues(long requestId) {
		String heading = "";
		SubmissionRequest submissionRequest;
		try {
			submissionRequest = getSubmissionRequest(requestId);
			List<MarketScope> commercialMarketScopes = getMarketScopeById(requestId).stream()
					.filter(dto -> dto.getCpName().equals("CP0")).findFirst().map(MarketScopeDTO::getMarketScope)
					.orElseThrow(() -> new RuntimeException("cpName not found: {}".concat("CP0")));
			String marketLeadName = commercialMarketScopes.get(0).getPoMarketsId().getDisplayName();
			if (!Utils.isNullOrEmptyObject(submissionRequest)) {
				String market = getDisplayNameOrDefault(marketLeadName != null ? marketLeadName : null);
				String brand = getDisplayNameOrDefault(
						submissionRequest.getBrands() != null ? submissionRequest.getBrands().getDisplayName() : null);
				String platform = getDisplayNameOrDefault(
						submissionRequest.getPlatforms() != null ? submissionRequest.getPlatforms().getDisplayName()
								: null);
				String variant = getDisplayNameOrDefault(
						submissionRequest.getVariantSku() != null ? submissionRequest.getVariantSku().getDisplayName()
								: null);
				String packageType = getDisplayNameOrDefault(submissionRequest.getPackagingType() != null
						? submissionRequest.getPackagingType().getDisplayName()
						: null);

				heading = market + "_" + brand + "_" + platform + "_" + variant + "_" + packageType;
			}
		} catch (Exception e) {
			logger.error("Error occured in fetching heading.");
		}
		return heading;
	}

	private String getDisplayNameOrDefault(String displayName) {
		return (displayName == null || displayName.isEmpty()) ? "N/A" : displayName;
	}
}
