package com.monster.npd.governance.pdf.pojo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
@Table(name = "GOVERNANCE_AUDIT")
public class GovernanceAuditSummary {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "UserAction")
	private String userAction;

	@Column(name = "Comments")
	private String comments;

	@Column(name = "PerformedBy")
	private String performedBy;

	@Column(name = "RequestId")
	private Integer requestId;
	
	@Column (name ="PerformedDate")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date performedDate;

}
