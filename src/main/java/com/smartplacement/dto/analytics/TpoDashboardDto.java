package com.smartplacement.dto.analytics;

import java.util.ArrayList;
import java.util.List;

/**
 * Institutional placement KPIs and executive metrics for TPO Administrator.
 */
public class TpoDashboardDto {

    private Long totalRegisteredStudents = 0L;
    private Long totalPlacedStudents = 0L;
    private Double overallPlacementPercentage = 0.0;

    private Long totalRegisteredCompanies = 0L;
    private Long totalVerifiedCompanies = 0L;
    private Long totalJobsPublished = 0L;

    private Long totalOffersExtended = 0L;
    private Long totalOffersAccepted = 0L;

    private Double highestPackageLpa = 0.0;
    private Double averagePackageLpa = 0.0;
    private Double medianPackageLpa = 0.0;

    private List<DepartmentPlacementStatDto> departmentStats = new ArrayList<>();
    private SalaryDistributionDto salaryDistribution = new SalaryDistributionDto();

    public TpoDashboardDto() {
    }

    public Long getTotalRegisteredStudents() {
        return totalRegisteredStudents;
    }

    public void setTotalRegisteredStudents(Long totalRegisteredStudents) {
        this.totalRegisteredStudents = totalRegisteredStudents;
    }

    public Long getTotalPlacedStudents() {
        return totalPlacedStudents;
    }

    public void setTotalPlacedStudents(Long totalPlacedStudents) {
        this.totalPlacedStudents = totalPlacedStudents;
    }

    public Double getOverallPlacementPercentage() {
        return overallPlacementPercentage;
    }

    public void setOverallPlacementPercentage(Double overallPlacementPercentage) {
        this.overallPlacementPercentage = overallPlacementPercentage;
    }

    public Long getTotalRegisteredCompanies() {
        return totalRegisteredCompanies;
    }

    public void setTotalRegisteredCompanies(Long totalRegisteredCompanies) {
        this.totalRegisteredCompanies = totalRegisteredCompanies;
    }

    public Long getTotalVerifiedCompanies() {
        return totalVerifiedCompanies;
    }

    public void setTotalVerifiedCompanies(Long totalVerifiedCompanies) {
        this.totalVerifiedCompanies = totalVerifiedCompanies;
    }

    public Long getTotalJobsPublished() {
        return totalJobsPublished;
    }

    public void setTotalJobsPublished(Long totalJobsPublished) {
        this.totalJobsPublished = totalJobsPublished;
    }

    public Long getTotalOffersExtended() {
        return totalOffersExtended;
    }

    public void setTotalOffersExtended(Long totalOffersExtended) {
        this.totalOffersExtended = totalOffersExtended;
    }

    public Long getTotalOffersAccepted() {
        return totalOffersAccepted;
    }

    public void setTotalOffersAccepted(Long totalOffersAccepted) {
        this.totalOffersAccepted = totalOffersAccepted;
    }

    public Double getHighestPackageLpa() {
        return highestPackageLpa;
    }

    public void setHighestPackageLpa(Double highestPackageLpa) {
        this.highestPackageLpa = highestPackageLpa;
    }

    public Double getAveragePackageLpa() {
        return averagePackageLpa;
    }

    public void setAveragePackageLpa(Double averagePackageLpa) {
        this.averagePackageLpa = averagePackageLpa;
    }

    public Double getMedianPackageLpa() {
        return medianPackageLpa;
    }

    public void setMedianPackageLpa(Double medianPackageLpa) {
        this.medianPackageLpa = medianPackageLpa;
    }

    public List<DepartmentPlacementStatDto> getDepartmentStats() {
        return departmentStats;
    }

    public void setDepartmentStats(List<DepartmentPlacementStatDto> departmentStats) {
        this.departmentStats = departmentStats;
    }

    public SalaryDistributionDto getSalaryDistribution() {
        return salaryDistribution;
    }

    public void setSalaryDistribution(SalaryDistributionDto salaryDistribution) {
        this.salaryDistribution = salaryDistribution;
    }
}
