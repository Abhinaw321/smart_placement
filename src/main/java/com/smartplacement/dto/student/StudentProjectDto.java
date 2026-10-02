package com.smartplacement.dto.student;

import com.smartplacement.entity.StudentProject;
import jakarta.validation.constraints.NotBlank;

public class StudentProjectDto {

    private Long id;

    @NotBlank(message = "Project title is required")
    private String title;

    private String description;
    private String technologies;
    private String githubUrl;
    private String demoUrl;

    public StudentProjectDto() {
    }

    public StudentProjectDto(Long id, String title, String description, String technologies, String githubUrl, String demoUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.technologies = technologies;
        this.githubUrl = githubUrl;
        this.demoUrl = demoUrl;
    }

    public static StudentProjectDto fromEntity(StudentProject project) {
        return new StudentProjectDto(
                project.getId(),
                project.getTitle(),
                project.getDescription(),
                project.getTechnologies(),
                project.getGithubUrl(),
                project.getDemoUrl()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTechnologies() {
        return technologies;
    }

    public void setTechnologies(String technologies) {
        this.technologies = technologies;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }

    public String getDemoUrl() {
        return demoUrl;
    }

    public void setDemoUrl(String demoUrl) {
        this.demoUrl = demoUrl;
    }
}
