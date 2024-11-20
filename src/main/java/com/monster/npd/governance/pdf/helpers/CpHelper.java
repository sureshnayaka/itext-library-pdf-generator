package com.monster.npd.governance.pdf.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.monster.npd.governance.pdf.pojo.CP;
import com.monster.npd.governance.pdf.pojo.DeliverableThreshold;
import com.monster.npd.governance.pdf.pojo.DeliverableThresholdDTO;
import com.monster.npd.governance.pdf.pojo.MarketScope;
import com.monster.npd.governance.pdf.pojo.MarketScopeDTO;
import com.monster.npd.governance.pdf.pojo.SubmissionGovernanceMilestone;
import com.monster.npd.governance.pdf.pojo.SubmissionRequestGovernanceMilestone;
import com.monster.npd.governance.pdf.repository.DeliverableThresholdRepository;
import com.monster.npd.governance.pdf.repository.MarketScopeRepository;
import com.monster.npd.governance.pdf.repository.SubmissionRequestGovernanceMilestoneRepository;

@Component
public class CpHelper {

	private static final Logger logger = LogManager.getLogger(CpHelper.class);

	@Autowired
	private SubmissionRequestGovernanceMilestoneRepository submissionRequestGovernanceMilestoneRepository;

	@Autowired
	private MarketScopeRepository commercialMarketScopeRepository;

	@Autowired
	private DeliverableThresholdRepository deliverableThresholdRepository;

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
						.map(g -> g.getStage().contains(currentCheckpoint)).orElse(false); // return false if stage or
																							// currentCheckpoint is null

				cps.add(new CP(governanceMilestone.getStage(), false, isActiveCP));
			}
		} catch (Exception e) {
			logger.error("Error occurred while fetching CP data for requestId {}: {}", requestId, e.getMessage(), e);
		}

		return cps;
	}

	public List<MarketScopeDTO> getMarketScopeById(long requestId) {
		List<SubmissionRequestGovernanceMilestone> submissionRequest = submissionRequestGovernanceMilestoneRepository
				.findByIdRequestId(requestId);
		List<MarketScopeDTO> dtos = new ArrayList<>();

		for (SubmissionRequestGovernanceMilestone request : submissionRequest) {
			List<MarketScope> marketScopes = commercialMarketScopeRepository
					.findByPoGovernanceMSId(request.getId().getGovernanceMilestoneId());
			dtos.add(new MarketScopeDTO(request.getGovernanceMilestone().getStage(), marketScopes));
		}
		return dtos;

	}

	public List<DeliverableThresholdDTO> getDeliverableThresholdById(long requestId) {

		List<SubmissionRequestGovernanceMilestone> submissionRequest = submissionRequestGovernanceMilestoneRepository
				.findByIdRequestId(requestId);
		List<DeliverableThresholdDTO> dtos = new ArrayList<>();

		for (SubmissionRequestGovernanceMilestone request : submissionRequest) {
			List<DeliverableThreshold> deliverableThresholds = deliverableThresholdRepository
					.findByPoGovernanceMSId(request.getId().getGovernanceMilestoneId());
			dtos.add(new DeliverableThresholdDTO(request.getGovernanceMilestone().getStage(), deliverableThresholds));
		}
		return dtos;
	}

}
