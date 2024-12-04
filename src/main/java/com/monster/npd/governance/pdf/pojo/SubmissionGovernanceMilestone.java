package com.monster.npd.governance.pdf.pojo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "SubmissionGovernance_Milestone")
@Getter
@Setter
public class SubmissionGovernanceMilestone {

	@Id
	@Column(name = "Id")
	private Long id;

	@Column(name = "Stage", length = 64)
	private String stage;

	@Column(name = "Decision", length = 64)
	private String decision;

	@Column(name = "Decision_Date")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date decisionDate;

	@Column(name = "Target_Start_Date")
	private Date targetStartDate;

	@Lob
	@Column(name = "Comments")
	private String comments;

	@Column(name = "RevisedTargetDate")
	private Date revisedTargetDate;

	@Column(name = "LiveDate")
	private Date liveDate;

	@Column(name = "S_ITEM_STATUS")
	private Integer sItemStatus;

	@Column(name = "IsCommercialChangeToPreviousCPs")
	private Boolean isSamePreviousCp;

	@Lob
	@Column(name = "S_TEMPORARY_COPY_DATA")
	private byte[] sTemporaryCopyData;

	@Column(name = "CommercialRationaleForChanges", length = 64)
	private String commercialRationaleForChanges;

	@Lob
	@Column(name = "ProjectManagerComments")
	private String projectManagerComments;

	@Column(name = "IncrementalReplacemntalSKU")
	private String incrementalReplacemntalSKU;

	@Column(name = "PortfolioDelistStrategy")
	private String portfolioDelistStrategy;

	@Column(name = "SpecificSKUCutOffIntro")
	private String specificSKUCutOffIntro;

	@Column(name = "SwitchDateAndDrivingDateReason")
	private String switchDateAndDrivingDateReason;

	@Column(name = "CommercialStrategy")
	private String commercialStrategy;

}
