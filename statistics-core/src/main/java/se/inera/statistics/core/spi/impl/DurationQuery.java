package se.inera.statistics.core.spi.impl;

import static se.inera.statistics.core.spi.impl.QueryUtil.isAllSelected;
import static se.inera.statistics.core.spi.impl.QueryUtil.MALE;
import static se.inera.statistics.core.spi.impl.QueryUtil.FEMALE;

import java.util.ArrayList;
import java.util.List;

import se.inera.statistics.core.api.RowResult;
import se.inera.statistics.core.repository.MedicalCertificateRepository;

public class DurationQuery {

    private static final int MAX_DURATION = 36000;
    private static final int[][] PERIODS = {{0,14},{15, 30}, {31, 90}, {91, 360}, {361, 36000}};
    
    private final MedicalCertificateRepository certificateRepository;
    
    public DurationQuery(MedicalCertificateRepository certificateRepository) {
        this.certificateRepository = certificateRepository;
                
        
    }

    public List<RowResult> getByDuration(long start, long end, String disability, String group) {
        List<RowResult> rowResults = new ArrayList<RowResult>();
        for (int[] period: PERIODS) {
            rowResults.add(getRowResultByDuration(period[0], period[1], start, end, disability, group));
        }
        return rowResults;
    }

    private RowResult getRowResultByDuration(long minDuration, long maxDuration, long start, long end, String disability, String group) {
        final long countMale;
        final long countFemale;

        if (isAllSelected(disability)) {
            if (isAllSelected(group)) {
                countMale = certificateRepository.findCountByDuration(minDuration, maxDuration, MALE, start, end);
                countFemale = certificateRepository.findCountByDuration(minDuration, maxDuration, FEMALE, start, end);
            } else {
                countMale = certificateRepository.findCountByDurationAndIcd10Group(minDuration, maxDuration, MALE, start, end, group);
                countFemale = certificateRepository.findCountByDurationAndIcd10Group(minDuration, maxDuration, FEMALE, start, end, group);
            }
        } else {
            int disabilityInt = Integer.parseInt(disability);
            if (isAllSelected(group)) {
                countMale = certificateRepository.findCountByDurationAndWorkDisability(minDuration, maxDuration, MALE, start, end, disabilityInt);
                countFemale = certificateRepository.findCountByDurationAndWorkDisability(minDuration, maxDuration, FEMALE, start, end, disabilityInt);
            } else {
                countMale = certificateRepository.findCountByDurationAndIcd10GroupAndWorkDisability(minDuration, maxDuration, MALE, start, end, group, disabilityInt);
                countFemale = certificateRepository.findCountByDurationAndIcd10GroupAndWorkDisability(minDuration, maxDuration, FEMALE, start, end, group, disabilityInt);
            }
        }
        return RowResult.newResult(formatDuration(minDuration, maxDuration), countMale, countFemale);
    }
    private static String formatDuration(long minDuration, long maxDuration) {
        if (0 == minDuration) {
            return "<" + maxDuration;
        } else if (maxDuration == MAX_DURATION){
            return ">" + minDuration;
        } else {
            return minDuration + "-" + maxDuration;
        }
    }

}
