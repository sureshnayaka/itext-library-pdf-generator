package com.monster.npd.governance.pdf.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PageHeaderDetailsDTO {
	
	private String projectName;
	private String projectType;
	private String projectSubType;
	private String programTag;
	private String reportingQ;
	private String currentActiveCP;
	private String e2ePm;
	private String lastCPdate;

}
