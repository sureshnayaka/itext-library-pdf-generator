package com.monster.npd.governance.pdf.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "SubmissionDeliverable_Threshold")
@Getter
@Setter
public class DeliverableThreshold {

	@Id
	@Column(name = "Id", nullable = false)
	private Long id;

	@Column(name = "S_ITEM_STATUS")
	private Integer itemStatus;

	@Column(name = "S_IS_TEMPORARY_COPY")
	private Boolean isTemporaryCopy;

	@Lob
	@Column(name = "S_TEMPORARY_COPY_DATA")
	private byte[] temporaryCopyData;

	@Column(name = "ThresholdType", length = 64)
	private String thresholdType;

	@Column(name = "NSVPerCase")
	private Float nsvPerCase;

	@Column(name = "GM", length = 64)
	private String gm;

	@Column(name = "COGS")
	private Float cogs;

	@Column(name = "Volume")
	private Integer volume;

	@Column(name = "ROS", length = 64)
	private String ros;

	@Column(name = "Distribution", length = 64)
	private String distribution;

	@Column(name = "R_PO_GOVERNANCE_MILESTONE_Id")
	private Long governanceMilestoneId;

}
