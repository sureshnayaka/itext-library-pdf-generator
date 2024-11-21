package com.monster.npd.governance.pdf.pojo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class GovernanceAction {
	private String name;
	private String action;
	private String comments;
	private Date date;

}
