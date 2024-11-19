package com.itext.pdf.generation.pojo;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "O4NPDSubmissionDeliverable_Threshold", schema = "dbo")
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

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		DeliverableThreshold that = (DeliverableThreshold) o;
		return Objects.equals(id, that.id) && Objects.equals(itemStatus, that.itemStatus)
				&& Objects.equals(isTemporaryCopy, that.isTemporaryCopy)
				&& Objects.equals(thresholdType, that.thresholdType) && Objects.equals(nsvPerCase, that.nsvPerCase)
				&& Objects.equals(gm, that.gm) && Objects.equals(cogs, that.cogs) && Objects.equals(volume, that.volume)
				&& Objects.equals(ros, that.ros) && Objects.equals(distribution, that.distribution)
				&& Objects.equals(governanceMilestoneId, that.governanceMilestoneId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, itemStatus, isTemporaryCopy, thresholdType, nsvPerCase, gm, cogs, volume, ros,
				distribution, governanceMilestoneId);
	}
}
