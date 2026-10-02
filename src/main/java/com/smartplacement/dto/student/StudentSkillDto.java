package com.smartplacement.dto.student;

import com.smartplacement.entity.SkillProficiency;
import com.smartplacement.entity.StudentSkill;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class StudentSkillDto {

    private Long id;

    @NotBlank(message = "Skill name is required")
    private String skillName;

    @NotNull(message = "Proficiency level is required")
    private SkillProficiency proficiency = SkillProficiency.INTERMEDIATE;

    public StudentSkillDto() {
    }

    public StudentSkillDto(Long id, String skillName, SkillProficiency proficiency) {
        this.id = id;
        this.skillName = skillName;
        this.proficiency = proficiency;
    }

    public static StudentSkillDto fromEntity(StudentSkill skill) {
        return new StudentSkillDto(
                skill.getId(),
                skill.getSkillName(),
                skill.getProficiency()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public SkillProficiency getProficiency() {
        return proficiency;
    }

    public void setProficiency(SkillProficiency proficiency) {
        this.proficiency = proficiency;
    }
}
