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

}
