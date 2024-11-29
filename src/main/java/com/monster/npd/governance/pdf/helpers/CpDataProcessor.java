package com.monster.npd.governance.pdf.helpers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.monster.npd.governance.pdf.pojo.CP;
import com.monster.npd.governance.pdf.pojo.CheckpointData;
import com.monster.npd.governance.pdf.pojo.DeliverableThresholdDTO;
import com.monster.npd.governance.pdf.pojo.GovernanceAction;
import com.monster.npd.governance.pdf.pojo.MarketScope;
import com.monster.npd.governance.pdf.pojo.MarketScopeDTO;
import com.monster.npd.governance.pdf.pojo.MetricData;
import com.monster.npd.governance.pdf.pojo.SubmissionGovernanceMilestone;
import com.monster.npd.governance.pdf.pojo.SubmissionRequest;
import com.monster.npd.governance.pdf.pojo.SubmissionRequestGovernanceMilestone;
import com.monster.npd.governance.pdf.repository.DeliverableThresholdRepository;
import com.monster.npd.governance.pdf.repository.GovernanceAuditSummaryRepository;
import com.monster.npd.governance.pdf.repository.MarketScopeRepository;
import com.monster.npd.governance.pdf.repository.SubmissionRequestGovernanceMilestoneRepository;
import com.monster.npd.governance.pdf.repository.SubmissionsRequestRepository;

@Component
public class CpDataProcessor {

	private static final Logger logger = LogManager.getLogger(CpDataProcessor.class);

	private static final String APPROVED = "Approved";
	private static final String CPS = "CP";

	@Autowired
	private SubmissionRequestGovernanceMilestoneRepository submissionRequestGovernanceMilestoneRepository;

	@Autowired
	private MarketScopeRepository commercialMarketScopeRepository;

	@Autowired
	private DeliverableThresholdRepository deliverableThresholdRepository;

	@Autowired
	private GovernanceAuditSummaryRepository auditSummaryRepository;

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
				String currentCheckpoint = request.getSubmissionRequest().getCurrentCheckpoint();
				boolean isActiveCP = Optional.ofNullable(governanceMilestone)
						.map(governance -> governance.getStage().contains(currentCheckpoint)).orElse(false);

				cps.add(new CP(governanceMilestone.getStage(), false, isActiveCP));
			}
//			cps = cps = cps.stream()
//			         .sorted(Comparator.comparing(CP::getCpName).reversed())
//			         .collect(Collectors.toList());

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
		}).collect(Collectors.toList());
	}

	public Optional<SubmissionRequest> getProjectDetailsForCP0(long requestId) {
		return submissionRequestGovernanceMilestoneRepository.findByIdRequestId(requestId).stream().findFirst()
				.map(SubmissionRequestGovernanceMilestone::getSubmissionRequest);
	}

	public List<GovernanceAction> getGovernanceAuditSummary(int requestId) {

		List<GovernanceAction> governanceActions = new ArrayList<>();
		auditSummaryRepository.findByRequestId(requestId).forEach(audit -> {
			governanceActions.add(new GovernanceAction(audit.getPerformedBy(), audit.getUserAction(),
					audit.getComments(), audit.getPerformedDate()));
		});
		return governanceActions;

	}

	public boolean isApprovedCp(long requestId) {
		getCPforVolumeSummary(requestId);
		return submissionRequestGovernanceMilestoneRepository.findByIdRequestId(requestId).stream()
				.anyMatch(request -> CPS.concat("0").equalsIgnoreCase(request.getGovernanceMilestone().getStage())
						&& APPROVED.equalsIgnoreCase(request.getGovernanceMilestone().getDecision()));
	}

	public Map<String, CheckpointData> getCPforVolumeSummary(long requestId) {
		Map<String, CheckpointData> data = new HashMap<>();
		List<SubmissionRequestGovernanceMilestone> submissionGovernanceMilestones = submissionRequestGovernanceMilestoneRepository
				.findByIdRequestId(requestId).stream().filter(request -> {
					String currentCheckpoint = request.getSubmissionRequest().getCurrentCheckpoint();
					String cp = request.getGovernanceMilestone().getStage();
					String decision = request.getGovernanceMilestone().getDecision();

					// Condition 1: Matches the current checkpoint
					boolean matchesCheckpoint = cp != null && cp.contains(currentCheckpoint);

					// Condition 2: Decision is "Approved"
					boolean isApproved = APPROVED.equalsIgnoreCase(decision);

					return matchesCheckpoint || isApproved;
				}).toList();
		for (SubmissionRequestGovernanceMilestone request : submissionGovernanceMilestones) {
			List<MarketScope> marketScope = commercialMarketScopeRepository
					.findByPoGovernanceMSId(request.getId().getGovernanceMilestoneId());
			for (MarketScope scope : marketScope) {
				if (scope.getLeadMarket()) {

					CheckpointData cp0 = new CheckpointData(new MetricData(scope.getAnnualisedYear1Volume(), "N/A"),
							new MetricData(scope.getThreeMonthLaunchVolume(), "N/A "),
							new MetricData(scope.getNsvCase(), "N/A"), new MetricData(" ", " "));

					data.put(request.getGovernanceMilestone().getStage(), cp0);
				}
			}
		}
		return data;

	}

	public String getLastCPDate(long requestId) {
		String cpDate = null;

		List<SubmissionRequestGovernanceMilestone> milestones = submissionRequestGovernanceMilestoneRepository
				.findByIdRequestId(requestId);
		Optional<SubmissionRequest> optionalSubmissionRequest = milestones.stream().findFirst()
				.map(SubmissionRequestGovernanceMilestone::getSubmissionRequest);

		if (optionalSubmissionRequest.isPresent()) {
			SubmissionRequest submissionRequest = optionalSubmissionRequest.get();
			String currentCheckpoint = submissionRequest.getCurrentCheckpoint();
			if (!"0".equals(currentCheckpoint)) {
				int lastCheckpoint = Integer.parseInt(currentCheckpoint) - 1;
				for (SubmissionRequestGovernanceMilestone milestone : milestones) {
					String stage = milestone.getGovernanceMilestone().getStage();
					if (stage.toLowerCase().equals(String.valueOf(CPS + lastCheckpoint).toLowerCase())) {
						cpDate = milestone.getGovernanceMilestone().getDecisionDate().toString();
						break;
					}
				}
			}
		}

		return cpDate;

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
}
