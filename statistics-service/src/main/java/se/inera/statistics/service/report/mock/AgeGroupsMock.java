package se.inera.statistics.service.report.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.joda.time.LocalDate;

import se.inera.statistics.service.report.api.AgeGroups;
import se.inera.statistics.service.report.model.AgeGroupsResponse;
import se.inera.statistics.service.report.model.db.AgeGroupsRow;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.service.report.repository.RollingLength;
import se.inera.statistics.service.report.util.AldersgroupUtil;
import se.inera.statistics.service.report.util.Ranges.Range;
import se.inera.statistics.service.report.util.Verksamhet;

public class AgeGroupsMock implements AgeGroups {

    private Random random = new Random();

    // CHECKSTYLE:OFF MagicNumber
    public AgeGroupsResponse getAgeGroups(int periods) {
        final List<AgeGroupsRow> rows = new ArrayList<>();
        for (Range group : AldersgroupUtil.RANGES) {
            int women = (int) (random.nextGaussian() * 2000 + 10000);
            int men = (int) (random.nextGaussian() * 2000 + 10000);
            rows.add(new AgeGroupsRow(null, group.getName(), periods, women, men));
        }
        return new AgeGroupsResponse(rows, 12);
    }

    @Override
    public void count(String period, String hsaId, String group, RollingLength length, Verksamhet typ, Sex sex) {
    }

    @Override
    public AgeGroupsResponse getCurrentAgeGroups(String hsaId) {
        return getAgeGroups(1);
    }

    @Override
    public AgeGroupsResponse getHistoricalAgeGroups(String hsaId, LocalDate when, RollingLength rollignLength) {
        return getAgeGroups(12);
    }

    // CHECKSTYLE:ON

}
