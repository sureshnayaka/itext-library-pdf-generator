package com.monster.npd.governance.pdf.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "SubmissionCommercial_Market_Scope")
public class MarketScope {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Id")
	private Long id;

	@Column(name = "Three_Month_LaunchVolume")
	private String threeMonthLaunchVolume;

	@Column(name = "AnnualisedYear1Volume")
	private String annualisedYear1Volume;

	@Column(name = "Cannibilisation_Impact")
	private String cannibilisationImpact;

	@Column(name = "NSVCase")
	private String nsvCase;

	@Column(name = "COGCase")
	private String cogCase;

	@Column(name = "AnnualisedYear1NSV_EURO")
	private String annualisedYear1NsvEuro;

	@Column(name = "AnnualisedYear1NSV_LocalCurrency")
	private String annualisedYear1NsvLocalCurrency;

	@Column(name = "GrossProfit")
	private String grossProfit;

	@Column(name = "GrossMargin")
	private String grossMargin;

	@Column(name = "LeadMarket")
	private Boolean leadMarket;

	@Column(name = "TargetDPInWarehouse")
	private String targetDpInWarehouse;

	@Column(name = "EnterCurrencyDirectlyInEuros")
	private Boolean enterCurrencyDirectlyInEuros;

	@Column(name = "S_ITEM_STATUS")
	private Integer itemStatus;

	@Column(name = "S_IS_TEMPORARY_COPY")
	private Boolean isTemporaryCopy;

	@Lob
	@Column(name = "S_TEMPORARY_COPY_DATA")
	private byte[] temporaryCopyData;

	@Column(name = "R_PO_MARKETS_Id")
	private Long poMarketsId;

	@Column(name = "VolumeChangeVsPrevious")
	private Float volumeChangeVsPrevious;

	@Column(name = "CPAgreedDeliveryWeek")
	private String cpAgreedDeliveryWeek;

	@Column(name = "R_PO_GOVERNANCE_MILESTONE_Id")
	private Long poGovernanceMilestoneId;

}
