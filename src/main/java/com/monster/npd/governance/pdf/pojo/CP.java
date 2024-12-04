package com.monster.npd.governance.pdf.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CP {

	private String cpName;
	private Boolean isSamePreviourCP;
	private Boolean isActive;
	private String approverComments;
	private String pmComments;
	private String commercialRelationalComments;
	private String incrementalReplacemntalSKU;
	private String portfolioDelistStrategy;
	private String specificSKUCutOffIntro;
	private String switchDateAndDrivingDateReason;
	private String commercialStrategy;

}
