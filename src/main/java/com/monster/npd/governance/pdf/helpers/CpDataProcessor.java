package com.monster.npd.governance.pdf.helpers;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

@Component
public class CpDataProcessor {

	private static final Logger logger = LogManager.getLogger(CpDataProcessor.class);

	@Autowired
	private SubmissionRequestGovernanceMilestoneRepository submissionRequestGovernanceMilestoneRepository;

	@Autowired
	private MarketScopeRepository commercialMarketScopeRepository;

	@Autowired
	private DeliverableThresholdRepository deliverableThresholdRepository;

	@Autowired
	private GovernanceAuditSummaryRepository auditSummaryRepository;

	public List<CP> getCpByRequestId(long requestId) {
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
						.map(g -> g.getStage().contains(currentCheckpoint)).orElse(false);
				// return false if stage or currentCheckpoint is null

				cps.add(new CP(governanceMilestone.getStage(), false, isActiveCP));
			}
		} catch (Exception e) {
			logger.error("Error occurred while fetching CP data for requestId {}: {}", requestId, e.getMessage(), e);
		}

		return cps;
	}

	public List<MarketScopeDTO> getMarketScopeById(long requestId) {
		return fetchAndMapToDTO(requestId, commercialMarketScopeRepository::findByPoGovernanceMSId,
				MarketScopeDTO::new);
	}

	public List<DeliverableThresholdDTO> getDeliverableThresholdById(long requestId) {
		return fetchAndMapToDTO(requestId, deliverableThresholdRepository::findByPoGovernanceMSId,
				DeliverableThresholdDTO::new);
	}

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
				.anyMatch(request -> "CP0".equalsIgnoreCase(request.getGovernanceMilestone().getStage())
						&& "Approved".equalsIgnoreCase(request.getGovernanceMilestone().getDecision()));
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
					boolean isApproved = "Approved".equalsIgnoreCase(decision);

					return matchesCheckpoint || isApproved;
				}).toList();
		for (SubmissionRequestGovernanceMilestone request : submissionGovernanceMilestones) {
			List<MarketScope> marketScope = commercialMarketScopeRepository
					.findByPoGovernanceMSId(request.getId().getGovernanceMilestoneId());
			for (MarketScope scope : marketScope) {
				if (scope.getLeadMarket()) {

					CheckpointData cp0 = new CheckpointData(new MetricData(scope.getAnnualisedYear1Volume(), "-"),
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
					if (stage.toLowerCase().equals(String.valueOf("CP" + lastCheckpoint).toLowerCase())) {
						cpDate = milestone.getGovernanceMilestone().getDecisionDate().toString();
						break;
					}
				}
			}
		}

		return cpDate;

	}

}
