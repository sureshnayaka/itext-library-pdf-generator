package com.monster.npd.governance.pdf.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
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

	@Column(name = "Project_Name", length = 64)
	private String projectName;

	@Column(name = "Current_Checkpoint", length = 64)
	private String currentCheckpoint;

	@Column(name = "Case_Pack_Size")
	private Integer casePackSize;

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

	@Column(name = "ProgramTag", length = 128)
	private String programTag;

	@Column(name = "R_PO_SKU_Details_Id")
	private Long rPoSkuDetailsId;

	@Column(name = "R_PO_PM_Id")
	private Long rPoPmId;

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

	@Column(name = "R_PO_PROGRAM_TAG_ID")
	private Long rPoProgramTagId;

	@Column(name = "R_PO_E2E_PM_Id")
	private Long rPoE2ePmId;

	@OneToOne
	@JoinColumn(name = "R_PO_BRANDS_Id", referencedColumnName = "id", insertable = false, updatable = false)
	private BrandModel brands;

	@OneToOne
	@JoinColumn(name = "R_PO_PLATFORMS_Id", referencedColumnName = "id", insertable = false, updatable = false)
	private PlatformModel platforms;


	@OneToOne
	@JoinColumn(name = "R_PO_VARIANT_SKU_Id", referencedColumnName = "id", insertable = false, updatable = false)
	private SkuVariantModel variantSku;

	@OneToOne
	@JoinColumn(name = "R_PO_PACKAGING_TYPE_Id", referencedColumnName = "id", insertable = false, updatable = false)
	private PackagingTypeModel packagingType;

}
