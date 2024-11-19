package com.monster.npd.governance.pdf.pojo;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "SubmissionRequestDeliverable_Threshold")
@Getter
@Setter
public class SubmissionRequestDeliverableThreshold implements Serializable {

    private static final long serialVersionUID = 1L;

    @Embeddable
    public static class Id implements Serializable {
        private static final long serialVersionUID = 1L;

        @Column(name = "RequestId851FE0BB24A194E2", nullable = false)
        private Long requestId;

        @Column(name = "Deliverable_Threshold_Id", nullable = false)
        private Long deliverableThresholdId;

        // Constructors, Getters, Setters, Equals, and HashCode
        public Id() {}

        public Id(Long requestId, Long deliverableThresholdId) {
            this.requestId = requestId;
            this.deliverableThresholdId = deliverableThresholdId;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Id id = (Id) o;
            return Objects.equals(requestId, id.requestId) &&
                   Objects.equals(deliverableThresholdId, id.deliverableThresholdId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(requestId, deliverableThresholdId);
        }
    }

    @EmbeddedId
    private Id id;

    @ManyToOne
    @JoinColumn(name = "RequestId851FE0BB24A194E2", referencedColumnName = "Id", insertable = false, updatable = false)
    private SubmissionRequest submissionRequest;

    // Constructors, Getters, and Setters
    public SubmissionRequestDeliverableThreshold() {}

    public SubmissionRequestDeliverableThreshold(Id id, SubmissionRequest submissionRequest) {
        this.id = id;
        this.submissionRequest = submissionRequest;
    }

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public SubmissionRequest getSubmissionRequest() {
        return submissionRequest;
    }

    public void setSubmissionRequest(SubmissionRequest submissionRequest) {
        this.submissionRequest = submissionRequest;
    }
}
