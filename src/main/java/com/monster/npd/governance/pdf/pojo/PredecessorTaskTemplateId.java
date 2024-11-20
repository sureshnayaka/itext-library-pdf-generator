package com.monster.npd.governance.pdf.pojo;

import java.io.Serializable;

public class PredecessorTaskTemplateId implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private Long requestId;
	private Long governanceMilestoneId;

	public int hashCode() {
		return (int) (requestId + governanceMilestoneId);
		//return Objects.hash(requestId, governanceMilestoneId);
	}

	public boolean equals(Object object) {
		if (object instanceof PredecessorTaskTemplateId) {
			PredecessorTaskTemplateId otherId = (PredecessorTaskTemplateId) object;
			return (otherId.requestId == this.governanceMilestoneId)
					&& (otherId.governanceMilestoneId == this.governanceMilestoneId);
		}
		return false;
	}

}
