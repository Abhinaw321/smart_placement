package com.smartplacement.dto.analytics;

/**
 * Key performance indicators and placement drive overview for candidate dashboard.
 */
public class StudentDashboardDto {

    private Long totalApplicationsSubmitted = 0L;
    private Long shortlistedCount = 0L;
    private Long interviewsScheduled = 0L;
    private Long offersReceived = 0L;
    private Boolean isPlaced = false;
    private Integer profileCompletionPercentage = 0;
    private Long eligibleJobsCount = 0L;

    public StudentDashboardDto() {
    }

    public StudentDashboardDto(Long totalApplicationsSubmitted, Long shortlistedCount,
                               Long interviewsScheduled, Long offersReceived,
                               Boolean isPlaced, Integer profileCompletionPercentage,
                               Long eligibleJobsCount) {
        this.totalApplicationsSubmitted = totalApplicationsSubmitted != null ? totalApplicationsSubmitted : 0L;
        this.shortlistedCount = shortlistedCount != null ? shortlistedCount : 0L;
        this.interviewsScheduled = interviewsScheduled != null ? interviewsScheduled : 0L;
        this.offersReceived = offersReceived != null ? offersReceived : 0L;
        this.isPlaced = isPlaced != null ? isPlaced : false;
        this.profileCompletionPercentage = profileCompletionPercentage != null ? profileCompletionPercentage : 0;
        this.eligibleJobsCount = eligibleJobsCount != null ? eligibleJobsCount : 0L;
    }

    public Long getTotalApplicationsSubmitted() {
        return totalApplicationsSubmitted;
    }

    public void setTotalApplicationsSubmitted(Long totalApplicationsSubmitted) {
        this.totalApplicationsSubmitted = totalApplicationsSubmitted;
    }

    public Long getShortlistedCount() {
        return shortlistedCount;
    }

    public void setShortlistedCount(Long shortlistedCount) {
        this.shortlistedCount = shortlistedCount;
    }

    public Long getInterviewsScheduled() {
        return interviewsScheduled;
    }

    public void setInterviewsScheduled(Long interviewsScheduled) {
        this.interviewsScheduled = interviewsScheduled;
    }

    public Long getOffersReceived() {
        return offersReceived;
    }

    public void setOffersReceived(Long offersReceived) {
        this.offersReceived = offersReceived;
    }

    public Boolean getIsPlaced() {
        return isPlaced;
    }

    public void setIsPlaced(Boolean isPlaced) {
        this.isPlaced = isPlaced;
    }

    public Integer getProfileCompletionPercentage() {
        return profileCompletionPercentage;
    }

    public void setProfileCompletionPercentage(Integer profileCompletionPercentage) {
        this.profileCompletionPercentage = profileCompletionPercentage;
    }

    public Long getEligibleJobsCount() {
        return eligibleJobsCount;
    }

    public void setEligibleJobsCount(Long eligibleJobsCount) {
        this.eligibleJobsCount = eligibleJobsCount;
    }
}
