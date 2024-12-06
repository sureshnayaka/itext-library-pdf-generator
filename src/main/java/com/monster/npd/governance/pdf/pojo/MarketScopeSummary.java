package com.monster.npd.governance.pdf.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class MarketScopeSummary {

	private String cpName;
	private String anualizedVolume;
	private Float volumeChange;
	private String anualisedNsv;
	private String gmPercentage;
	private String alignedDP;

}
