package com.smartplacement.dto.analytics;

/**
 * Department/branch-level placement aggregation statistics.
 */
public class DepartmentPlacementStatDto {

    private String branch;
    private Long totalStudents;
    private Long placedStudents;
    private Double placementPercentage;
    private Double averageCtcLpa;

    public DepartmentPlacementStatDto() {
    }

    public DepartmentPlacementStatDto(String branch, Long totalStudents, Long placedStudents,
                                      Double placementPercentage, Double averageCtcLpa) {
        this.branch = branch;
        this.totalStudents = totalStudents;
        this.placedStudents = placedStudents;
        this.placementPercentage = placementPercentage;
        this.averageCtcLpa = averageCtcLpa;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public Long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(Long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public Long getPlacedStudents() {
        return placedStudents;
    }

    public void setPlacedStudents(Long placedStudents) {
        this.placedStudents = placedStudents;
    }

    public Double getPlacementPercentage() {
        return placementPercentage;
    }

    public void setPlacementPercentage(Double placementPercentage) {
        this.placementPercentage = placementPercentage;
    }

    public Double getAverageCtcLpa() {
        return averageCtcLpa;
    }

    public void setAverageCtcLpa(Double averageCtcLpa) {
        this.averageCtcLpa = averageCtcLpa;
    }
}
