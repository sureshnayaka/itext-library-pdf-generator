package com.monster.npd.governance.pdf.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "SubmissionGovernance_Audit")
public class SubmissionGovernanceAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "S_ITEM_STATUS")
    private Integer sItemStatus;

    @Column(name = "S_IS_TEMPORARY_COPY")
    private Boolean sIsTemporaryCopy;

    @Lob
    @Column(name = "S_TEMPORARY_COPY_DATA")
    private byte[] sTemporaryCopyData;

    @Column(name = "UserAction", length = 64)
    private String userAction;

    @Lob
    @Column(name = "Comments")
    private String comments;

    @Column(name = "LastModifiedBy_Id")
    private Long lastModifiedById;

    @Column(name = "CreatedBy_Id")
    private Long createdById;

    @Column(name = "S_LASTMODIFIEDDATE")
    private LocalDateTime sLastModifiedDate;

    @Column(name = "S_CREATEDDATE")
    private LocalDateTime sCreatedDate;

    @Column(name = "PerformedBy", length = 64)
    private String performedBy;

    @Column(name = "PerformedDate")
    private LocalDateTime performedDate;

    @Column(name = "R_PO_REQUEST_Id")
    private Long rPoRequestId;
}
