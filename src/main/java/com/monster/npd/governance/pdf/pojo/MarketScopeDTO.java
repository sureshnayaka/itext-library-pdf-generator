package com.monster.npd.governance.pdf.pojo;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class MarketScopeDTO {

	private String cpName;
	private List<MarketScope> marketScope;

}
