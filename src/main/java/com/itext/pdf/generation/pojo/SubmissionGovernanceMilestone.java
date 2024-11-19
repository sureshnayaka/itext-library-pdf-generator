package com.itext.pdf.generation.pojo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "O4NPDSubmissionGovernance_Milestone")
public class SubmissionGovernanceMilestone {

    @Id
    @Column(name = "Id")
    private Long id;

    @Column(name = "Stage", length = 64)
    private String stage;

    @Column(name = "Decision", length = 64)
    private String decision;

    @Column(name = "Decision_Date")
    private Date decisionDate;

    @Column(name = "Target_Start_Date")
    private Date targetStartDate;

    @Lob
    @Column(name = "Comments")
    private String comments;

    @Column(name = "RevisedTargetDate")
    private Date revisedTargetDate;

    @Column(name = "LiveDate")
    private Date liveDate;

    @Column(name = "S_ITEM_STATUS")
    private Integer sItemStatus;

    @Column(name = "S_IS_TEMPORARY_COPY")
    private Boolean sIsTemporaryCopy;

    @Lob
    @Column(name = "S_TEMPORARY_COPY_DATA")
    private byte[] sTemporaryCopyData;

    @Column(name = "CommercialRationaleForChanges", length = 64)
    private String commercialRationaleForChanges;

    @Column(name = "GovernanceApproach", length = 64)
    private String governanceApproach;

    @Lob
    @Column(name = "ProjectManagerComments")
    private String projectManagerComments;
}
