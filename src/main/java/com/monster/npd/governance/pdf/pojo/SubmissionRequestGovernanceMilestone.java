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
@Table(name = "SubmissionRequestGovernance_Milestone")
@Getter
@Setter
public class SubmissionRequestGovernanceMilestone implements Serializable {

	private static final long serialVersionUID = 1L;

	@Embeddable
	public static class Id implements Serializable {
		private static final long serialVersionUID = 1L;

		@Column(name = "RequestIdA26399F71487410E", nullable = false)
		private Long requestId;

		@Column(name = "Governance_Milestone_Id", nullable = false)
		private Long governanceMilestoneId;

		// Constructors, Getters, Setters, Equals, and HashCode
		public Id() {
		}

		public Id(Long requestId, Long governanceMilestoneId) {
			this.requestId = requestId;
			this.governanceMilestoneId = governanceMilestoneId;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;
			Id id = (Id) o;
			return Objects.equals(requestId, id.requestId)
					&& Objects.equals(governanceMilestoneId, id.governanceMilestoneId);
		}

		@Override
		public int hashCode() {
			return Objects.hash(requestId, governanceMilestoneId);
		}
	}

	@EmbeddedId
	private Id id;

	@ManyToOne
	@JoinColumn(name = "RequestIdA26399F71487410E", referencedColumnName = "Id", insertable = false, updatable = false)
	private SubmissionRequest submissionRequest;

	public SubmissionRequestGovernanceMilestone() {
	}

	public SubmissionRequestGovernanceMilestone(Id id, SubmissionRequest submissionRequest) {
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
