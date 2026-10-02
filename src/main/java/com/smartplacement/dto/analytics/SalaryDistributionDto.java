package com.smartplacement.dto.analytics;

/**
 * Compensation package tier distribution across confirmed student placements.
 */
public class SalaryDistributionDto {

    private Long tier1Below6Lpa = 0L;
    private Long tier2Between6And12Lpa = 0L;
    private Long tier3Between12And20Lpa = 0L;
    private Long tier4Above20Lpa = 0L;

    public SalaryDistributionDto() {
    }

    public SalaryDistributionDto(Long tier1Below6Lpa, Long tier2Between6And12Lpa,
                                 Long tier3Between12And20Lpa, Long tier4Above20Lpa) {
        this.tier1Below6Lpa = tier1Below6Lpa != null ? tier1Below6Lpa : 0L;
        this.tier2Between6And12Lpa = tier2Between6And12Lpa != null ? tier2Between6And12Lpa : 0L;
        this.tier3Between12And20Lpa = tier3Between12And20Lpa != null ? tier3Between12And20Lpa : 0L;
        this.tier4Above20Lpa = tier4Above20Lpa != null ? tier4Above20Lpa : 0L;
    }

    public Long getTier1Below6Lpa() {
        return tier1Below6Lpa;
    }

    public void setTier1Below6Lpa(Long tier1Below6Lpa) {
        this.tier1Below6Lpa = tier1Below6Lpa;
    }

    public Long getTier2Between6And12Lpa() {
        return tier2Between6And12Lpa;
    }

    public void setTier2Between6And12Lpa(Long tier2Between6And12Lpa) {
        this.tier2Between6And12Lpa = tier2Between6And12Lpa;
    }

    public Long getTier3Between12And20Lpa() {
        return tier3Between12And20Lpa;
    }

    public void setTier3Between12And20Lpa(Long tier3Between12And20Lpa) {
        this.tier3Between12And20Lpa = tier3Between12And20Lpa;
    }

    public Long getTier4Above20Lpa() {
        return tier4Above20Lpa;
    }

    public void setTier4Above20Lpa(Long tier4Above20Lpa) {
        this.tier4Above20Lpa = tier4Above20Lpa;
    }
}
