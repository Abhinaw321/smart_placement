package com.smartplacement.dto.analytics;

/**
 * Key performance indicators for recruiter hiring pipeline.
 */
public class RecruiterDashboardDto {

    private String companyName;
    private Long totalJobsPosted = 0L;
    private Long activeDrivesCount = 0L;
    private Long totalApplicationsReceived = 0L;
    private Long interviewsScheduled = 0L;
    private Long offersExtended = 0L;
    private Long offersAccepted = 0L;

    public RecruiterDashboardDto() {
    }

    public RecruiterDashboardDto(String companyName, Long totalJobsPosted, Long activeDrivesCount,
                                 Long totalApplicationsReceived, Long interviewsScheduled,
                                 Long offersExtended, Long offersAccepted) {
        this.companyName = companyName;
        this.totalJobsPosted = totalJobsPosted != null ? totalJobsPosted : 0L;
        this.activeDrivesCount = activeDrivesCount != null ? activeDrivesCount : 0L;
        this.totalApplicationsReceived = totalApplicationsReceived != null ? totalApplicationsReceived : 0L;
        this.interviewsScheduled = interviewsScheduled != null ? interviewsScheduled : 0L;
        this.offersExtended = offersExtended != null ? offersExtended : 0L;
        this.offersAccepted = offersAccepted != null ? offersAccepted : 0L;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Long getTotalJobsPosted() {
        return totalJobsPosted;
    }

    public void setTotalJobsPosted(Long totalJobsPosted) {
        this.totalJobsPosted = totalJobsPosted;
    }

    public Long getActiveDrivesCount() {
        return activeDrivesCount;
    }

    public void setActiveDrivesCount(Long activeDrivesCount) {
        this.activeDrivesCount = activeDrivesCount;
    }

    public Long getTotalApplicationsReceived() {
        return totalApplicationsReceived;
    }

    public void setTotalApplicationsReceived(Long totalApplicationsReceived) {
        this.totalApplicationsReceived = totalApplicationsReceived;
    }

    public Long getInterviewsScheduled() {
        return interviewsScheduled;
    }

    public void setInterviewsScheduled(Long interviewsScheduled) {
        this.interviewsScheduled = interviewsScheduled;
    }

    public Long getOffersExtended() {
        return offersExtended;
    }

    public void setOffersExtended(Long offersExtended) {
        this.offersExtended = offersExtended;
    }

    public Long getOffersAccepted() {
        return offersAccepted;
    }

    public void setOffersAccepted(Long offersAccepted) {
        this.offersAccepted = offersAccepted;
    }
}
