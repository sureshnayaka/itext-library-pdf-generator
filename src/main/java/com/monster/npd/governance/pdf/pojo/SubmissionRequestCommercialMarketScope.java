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
@Table(name = "SubmissionRequestCommercial_Market_Scope")
@Getter
@Setter
public class SubmissionRequestCommercialMarketScope implements Serializable {

	private static final long serialVersionUID = 1L;

	@Embeddable
	public static class Id implements Serializable {
		private static final long serialVersionUID = 1L;

		@Column(name = "RequestId93C5C6F18B8EA853", nullable = false)
		private Long requestId;

		@Column(name = "Commercial_Market_Scope_Id", nullable = false)
		private Long commercialMarketScopeId;

		// Constructors, Getters, Setters, Equals, and HashCode
		public Id() {
		}

		public Id(Long requestId, Long commercialMarketScopeId) {
			this.requestId = requestId;
			this.commercialMarketScopeId = commercialMarketScopeId;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;
			Id id = (Id) o;
			return requestId.equals(id.requestId) && commercialMarketScopeId.equals(id.commercialMarketScopeId);
		}

		@Override
		public int hashCode() {
			return Objects.hash(requestId, commercialMarketScopeId);
		}
	}

	@EmbeddedId
	private Id id;

	@ManyToOne
	@JoinColumn(name = "RequestId93C5C6F18B8EA853", referencedColumnName = "Id", insertable = false, updatable = false)
	private SubmissionRequest submissionRequest;

	// Constructors, Getters, and Setters
	public SubmissionRequestCommercialMarketScope() {
	}

	public SubmissionRequestCommercialMarketScope(Id id, SubmissionRequest submissionRequest) {
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
