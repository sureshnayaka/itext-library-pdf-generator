package com.monster.npd.governance.pdf.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "SubmissionRequest")
@Getter
@Setter
public class SubmissionRequest {

	@Id
	@Column(name = "Id")
	private Long id;

//    @Column(name = "S_ITEM_STATUS")
//    private Integer sItemStatus;
//
//    @Column(name = "S_IS_TEMPORARY_COPY")
//    private Boolean sIsTemporaryCopy;
//
//    @Column(name = "S_TEMPORARY_COPY_DATA")
//    private byte[] sTemporaryCopyData;

	@Column(name = "Project_Name", length = 64)
	private String projectName;

	@Column(name = "Current_Checkpoint", length = 64)
	private String currentCheckpoint;

//    @Column(name = "Bottler", length = 64)
//    private String bottler;
//
//    @Column(name = "Production_Site", length = 64)
//    private String productionSite;
//
//    @Column(name = "Case_Pack_Size")
//    private Integer casePackSize;
//
//    @Column(name = "Consumer_Unit_Size")
//    private Integer consumerUnitSize;
//
//    @Column(name = "SKU_Details", length = 64)
//    private String skuDetails;
//
//    @Column(name = "Request_Rationale", columnDefinition = "nvarchar(max)")
//    private String requestRationale;
//
//    @Column(name = "Request_Status", length = 64)
//    private String requestStatus;
//
//    @Column(name = "Is_SVP_Aligned")
//    private Boolean isSvpAligned;
//
//    @Column(name = "RequestID", length = 64)
//    private String requestId;
//
//    @Column(name = "SecondaryPackagingType", length = 64)
//    private String secondaryPackagingType;
//
//    @Column(name = "IsSecondaryPackaging")
//    private Boolean isSecondaryPackaging;
//
//    @Column(name = "NewFGSAP")
//    private Boolean newFgsap;
//
//    @Column(name = "NewHBCFormula")
//    private Boolean newHbcFormula;
//
//    @Column(name = "NewPrimaryPackaging")
//    private Boolean newPrimaryPackaging;
//
//    @Column(name = "NewRDFormula")
//    private Boolean newRdFormula;
//
//    @Column(name = "PostLaunchAnalysis")
//    private Boolean postLaunchAnalysis;
//
//    @Column(name = "PostProductionRegistartion")
//    private Boolean postProductionRegistration;
//
//    @Column(name = "PreProductionRegistartion")
//    private Boolean preProductionRegistration;
//
//    @Column(name = "RegistrationDossier")
//    private Boolean registrationDossier;

	@Column(name = "TechnicalDescription", columnDefinition = "nvarchar(max)")
	private String technicalDescription;

	@Column(name = "ProjectAbout", columnDefinition = "nvarchar(max)")
	private String projectAbout;

	@Column(name = "WhyProject", columnDefinition = "nvarchar(max)")
	private String whyProject;

	@Column(name = "IncrementalReplacemntalSKU", length = 64)
	private String incrementalReplacementSku;

	@Column(name = "SpecificSKUCutOffIntro", length = 64)
	private String specificSkuCutOffIntro;

	@Column(name = "CommercialStrategy", length = 2000)
	private String commercialStrategy;

	@Column(name = "PortfolioDelistStrategy", columnDefinition = "nvarchar(max)")
	private String portfolioDelistStrategy;

	@Column(name = "SwitchDateAndDrivingDateReason", columnDefinition = "nvarchar(max)")
	private String switchDateAndDrivingDateReason;

	@Column(name = "ProcessIdentifier", length = 64)
	private String processIdentifier;

	@Column(name = "MPMProjectID")
	private Integer mpmProjectId;

//    @Column(name = "Risk", columnDefinition = "nvarchar(max)")
//    private String risk;
//
//    @Column(name = "Critical_Items", columnDefinition = "nvarchar(max)")
//    private String criticalItems;
//
//    @Column(name = "BusinessCaseComments", columnDefinition = "nvarchar(max)")
//    private String businessCaseComments;
//
//    @Column(name = "NumberOfLinkedMarkets", length = 64)
//    private String numberOfLinkedMarkets;
//
//    @Column(name = "ProgMangerComments", columnDefinition = "nvarchar(max)")
//    private String progManagerComments;
//
//    @Column(name = "ISMigrated")
//    private Boolean isMigrated;

	@Column(name = "ProgramTag", length = 128)
	private String programTag;

//    @Column(name = "Registration_Classification_Available")
//    private Boolean registrationClassificationAvailable;
//
//    @Column(name = "REGISTRATION_Requirement")
//    private Boolean registrationRequirement;
//
//    @Column(name = "LeadFormula")
//    private Boolean leadFormula;
//
//    @Column(name = "R_PO_PACKAGING_TYPE_Id")
//    private Long rPoPackagingTypeId;
//
//    @Column(name = "R_PO_PROJECT_TYPE_Id")
//    private Long rPoProjectTypeId;
//
//    @Column(name = "LastModifiedBy_Id")
//    private Long lastModifiedById;
//
//    @Column(name = "CreatedBy_Id")
//    private Long createdById;

//    @Column(name = "S_LASTMODIFIEDDATE")
//    private Date sLastModifiedDate;
//
//    @Column(name = "S_CREATEDDATE")
//    private Date sCreatedDate;

//    @Column(name = "S_LBB_ID", length = 256)
//    private String sLbbId;
//
//    @Column(name = "S_LBB_CURRENT_STATE", length = 256)
//    private String sLbbCurrentState;
//
//    @Column(name = "S_LBB_CURRENT_STATE_ID", length = 256)
//    private String sLbbCurrentStateId;
//
//    @Column(name = "S_LBB_PARENT_STATE", length = 256)
//    private String sLbbParentState;
//
//    @Column(name = "S_LBB_ROOT_STATE", length = 256)
//    private String sLbbRootState;
//
//    @Column(name = "S_LBB_PREVIOUS_STATE", length = 256)
//    private String sLbbPreviousState;
//
//    @Column(name = "S_LBB_PREVIOUS_STATE_ID", length = 256)
//    private String sLbbPreviousStateId;
//
//    @Column(name = "S_LBB_PRIOR_ACTIVITY", length = 512)
//    private String sLbbPriorActivity;
//
//    @Column(name = "S_LBB_PRIOR_EVENT", length = 128)
//    private String sLbbPriorEvent;
//
//    @Column(name = "S_INSTANCE_STATUS", length = 15)
//    private String sInstanceStatus;
//
//    @Column(name = "R_PO_COMMERCIAL_CATEGORISATION_Id")
//    private Long rPoCommercialCategorisationId;
//
//    @Column(name = "R_PO_REQUEST_TYPE_Id")
//    private Long rPoRequestTypeId;
//
//    @Column(name = "R_PO_VARIANT_SKU_Id")
//    private Long rPoVariantSkuId;
//
//    @Column(name = "R_PO_OPS_PM_Id")
//    private Long rPoOpsPmId;
//
//    @Column(name = "R_PO_EARLY_MANUFACTURING_SITE_Id")
//    private Long rPoEarlyManufacturingSiteId;

	@Column(name = "R_PO_E2E_PM_Id")
	private Long rPoE2ePmId;

//    @Column(name = "R_PO_DRAFT_MANUFACTURING_LOCATION_Id")
//    private Long rPoDraftManufacturingLocationId;
//
//    @Column(name = "R_PO_CORP_PM_Id")
//    private Long rPoCorpPmId;
//
//    @Column(name = "R_PO_BRANDS_Id")
//    private Long rPoBrandsId;
//
//    @Column(name = "R_PO_PLATFORMS_Id")
//    private Long rPoPlatformsId;
//
//    @Column(name = "R_PO_BUSINESS_UNIT_Id")
//    private Long rPoBusinessUnitId;
//
//    @Column(name = "R_PO_CONSUMER_UNIT_SIZE_Id")
//    private Long rPoConsumerUnitSizeId;
//
//    @Column(name = "R_PO_CASE_PACK_SIZE_Id")
//    private Long rPoCasePackSizeId;
//
//    @Column(name = "R_PO_BOTTLER_Id")
//    private Long rPoBottlerId;
//
//    @Column(name = "R_PO_PRODUCTION_SITE_Id")
//    private Long rPoProductionSiteId;
//
//    @Column(name = "R_PO_GOVERNANCE_APPROACH_Id")
//    private Long rPoGovernanceApproachId;
//
	@Column(name = "R_PO_SKU_Details_Id")
	private Long rPoSkuDetailsId;

	@Column(name = "R_PO_PM_Id")
	private Long rPoPmId;

//    @Column(name = "R_PO_LEAD_MARKET_Id")
//    private Long rPoLeadMarketId;
//
//    @Column(name = "R_PO_PROJECT_SPECIALIST_Id")
//    private Long rPoProjectSpecialistId;
//
//    @Column(name = "R_PO_SECONDARY_AW_SPECIALIST_Id")
//    private Long rPoSecondaryAwSpecialistId;
//
//    @Column(name = "R_PO_REGULATORY_AFFAIRS_Id")
//    private Long rPoRegulatoryAffairsId;
//
//    @Column(name = "R_PO_RTM_Id")
//    private Long rPoRtmId;

	@Column(name = "R_PO_CP_PROJECT_TYPE_Id")
	private Long rPoCpProjectTypeId;

	@Column(name = "R_PO_PROJECT_DETAIL_Id")
	private Long rPoProjectDetailId;

	@Column(name = "R_PO_PROJECT_SUB_TYPE_Id")
	private Long rPoProjectSubTypeId;

	@Column(name = "R_PO_RA_LEAD_Id")
	private Long rPoRaLeadId;

	@Column(name = "R_PO_PM_DELIVERY_QUARTER_Id")
	private Long rPoPmDeliveryQuarterId;

//    @Column(name = "R_PO_GFG_Id")
//    private Long rPoGfgId;

	@Column(name = "R_PO_PROGRAM_TAG_ID")
	private Long rPoProgramTagId;

//    @Column(name = "R_PO_FINAL_PROJECT_CLASSIFICATION_Id")
//    private Long rPoFinalProjectClassificationId;

//    @Column(name = "R_PO_PROJECT_CLASSIFICATION_Id")
//    private Date rPoProjectClassificationId;
//
//    @Column(name = "R_PO_FPMS_Id")
//    private Long rPoFpmsId;
//
//    @Column(name = "R_PO_RA_PEER_Id")
//    private Date rPoRaPeerId;

}
