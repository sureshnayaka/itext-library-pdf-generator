package com.monster.npd.governance.pdf.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "SubmissionCommercial_Market_Scope")
@Getter
@Setter
public class MarketScope {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // If the ID is auto-incremented, otherwise remove this
	@Column(name = "Id", nullable = false)
	private Long id;

	@Column(name = "Three_Month_LaunchVolume", length = 64)
	private String threeMonthLaunchVolume;

	@Column(name = "AnnualisedYear1Volume", length = 64)
	private String annualisedYear1Volume;

	@Column(name = "Cannibilisation_Impact", length = 64)
	private String cannibalisationImpact;

	@Column(name = "NSVCase", length = 64)
	private String nsvCase;

	@Column(name = "COGCase", length = 64)
	private String cogCase;

	@Column(name = "AnnualisedYear1NSV_EURO", length = 64)
	private String annualisedYear1NSVEuro;

	@Column(name = "AnnualisedYear1NSV_LocalCurrency", length = 64)
	private String annualisedYear1NSVLocalCurrency;

	@Column(name = "GrossProfit", length = 64)
	private String grossProfit;

	@Column(name = "GrossMargin", length = 64)
	private String grossMargin;

	@Column(name = "LeadMarket")
	private Boolean leadMarket;

	@Column(name = "TargetDPInWarehouse", length = 64)
	private String targetDPInWarehouse;

	@Column(name = "EnterCurrencyDirectlyInEuros")
	private Boolean enterCurrencyDirectlyInEuros;

	@Column(name = "S_ITEM_STATUS")
	private Integer itemStatus;

	@Column(name = "S_IS_TEMPORARY_COPY")
	private Boolean isTemporaryCopy;

	@Lob
	@Column(name = "S_TEMPORARY_COPY_DATA")
	private byte[] temporaryCopyData;

	@OneToOne
	@JoinColumn(name = "R_PO_MARKETS_Id", referencedColumnName = "id", insertable = false, updatable = false)
	private LeadMarketModel poMarketsId;

	@Column(name = "R_PO_GOVERNANCE_MILESTONE_ID")
	private Long poGovernanceMSId;
	
	@Column(name ="VolumeChangeVsPrevious")
	private Float volumeChangeVsPrevious;
}
