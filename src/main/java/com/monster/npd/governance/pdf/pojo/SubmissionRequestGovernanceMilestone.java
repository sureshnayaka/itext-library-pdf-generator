package com.monster.npd.governance.pdf.pojo;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "SubmissionRequestGovernance_Milestone")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SubmissionRequestGovernanceMilestone implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private Identifier id;

    @ManyToOne
    @JoinColumn(name = "RequestIdA26399F71487410E", referencedColumnName = "id", insertable = false, updatable = false)
    private SubmissionRequest submissionRequest;

    @ManyToOne
    @JoinColumn(name = "Governance_Milestone_Id", referencedColumnName = "id", insertable = false, updatable = false)
    private SubmissionGovernanceMilestone governanceMilestone;

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class Identifier implements Serializable {
        private static final long serialVersionUID = 1L;

        @Column(name = "RequestIdA26399F71487410E", nullable = false)
        private Long requestId;

        @Column(name = "Governance_Milestone_Id", nullable = false)
        private Long governanceMilestoneId;
    }
}
