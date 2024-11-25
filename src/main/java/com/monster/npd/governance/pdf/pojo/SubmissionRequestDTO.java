package com.monster.npd.governance.pdf.pojo;

public class SubmissionRequestDTO {
    private Long id;
    private String projectTypeDisplayName;
    private String programTag;
    private String subType;

    // Constructors
    public SubmissionRequestDTO(Long id, String projectTypeDisplayName, String programTag, String subType) {
        this.id = id;
        this.projectTypeDisplayName = projectTypeDisplayName;
        this.programTag = programTag;
        this.subType = subType;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProjectTypeDisplayName() {
        return projectTypeDisplayName;
    }

    public void setProjectTypeDisplayName(String projectTypeDisplayName) {
        this.projectTypeDisplayName = projectTypeDisplayName;
    }

    public String getProgramTag() {
        return programTag;
    }

    public void setProgramTag(String programTag) {
        this.programTag = programTag;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }
}
