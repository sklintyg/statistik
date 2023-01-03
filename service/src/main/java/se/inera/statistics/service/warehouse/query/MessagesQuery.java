/*
 * Copyright (C) 2023 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.service.warehouse.query;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.inera.statistics.integration.hsa.model.HsaIdAny;
import se.inera.statistics.integration.hsa.model.HsaIdEnhet;
import se.inera.statistics.integration.hsa.model.HsaIdLakare;
import se.inera.statistics.integration.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.processlog.Lakare;
import se.inera.statistics.service.processlog.LakareManager;
import se.inera.statistics.service.report.common.ReportColor;
import se.inera.statistics.service.report.model.AvailableFilters;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.KonDataRow;
import se.inera.statistics.service.report.model.KonField;
import se.inera.statistics.service.report.model.OverviewChartRowExtended;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.warehouse.IntygType;
import se.inera.statistics.service.warehouse.ResponseUtil;
import se.inera.statistics.service.warehouse.message.CountDTOAmne;
import se.inera.statistics.service.warehouse.message.MessageWidelineLoader;
import se.inera.statistics.service.warehouse.message.MsgAmne;

@Component
public class MessagesQuery {

    private static final Logger LOG = LoggerFactory.getLogger(MessagesQuery.class);
    public static final String GROUP_NAME_SEPARATOR = " : ";

    @Autowired
    private MessageWidelineLoader messageWidelineLoader;

    @Autowired
    private LakareManager lakareManager;
    public static final int MAX_PERCENTAGE = 100;

    public KonDataResponse getMeddelandenPerAmneAggregated(KonDataResponse resultToAggregateIn, MessagesFilter filter, int cutoff) {
        final KonDataResponse messagesTvarsnittPerAmne = getMessagesPerAmne(filter);
        return getKonDataResponseAggregated(resultToAggregateIn, filter, cutoff, messagesTvarsnittPerAmne);
    }

    public KonDataResponse getMeddelandenPerAmneOchEnhetAggregated(KonDataResponse resultToAggregateIn, MessagesFilter filter,
        int cutoff, Map<HsaIdEnhet, String> idToNameMap) {
        final KonDataResponse messagesTvarsnittPerAmne = getMessagesTvarsnittPerAmnePerEnhet(filter, idToNameMap, true);
        return getKonDataResponseAggregated(resultToAggregateIn, filter, cutoff, messagesTvarsnittPerAmne);
    }

    public SimpleKonResponse getMessages(HsaIdVardgivare vardgivare, Collection<HsaIdEnhet> enheter, LocalDate start,
        int perioder) {
        LocalDate to = start.plusMonths(perioder);
        List<MessageWidelineLoader.CountDTO> rows = messageWidelineLoader.getAntalMeddelandenPerMonth(start, to, vardgivare, enheter);
        return convertToSimpleResponse(rows, start, perioder);
    }

    public SimpleKonResponse getMessagesTvarsnitt(HsaIdVardgivare vardgivare, Collection<HsaIdEnhet> enheter,
        LocalDate start, int perioder) {
        LocalDate to = start.plusMonths(perioder);
        List<MessageWidelineLoader.CountDTO> rows = messageWidelineLoader.getAntalMeddelandenPerMonth(start, to, vardgivare, enheter);
        return convertToSimpleResponseTvarsnitt(rows);
    }

    public KonDataResponse getMessagesPerAmne(MessagesFilter filter) {
        List<CountDTOAmne> rows = messageWidelineLoader.getAntalMeddelandenPerAmne(filter, true);
        return convertToMessagesPerAmne(rows, filter.getFrom(), filter.getNumberOfMonths());
    }

    public SimpleKonResponse getMessagesTvarsnittPerAmne(MessagesFilter filter) {
        List<CountDTOAmne> rows = messageWidelineLoader.getAntalMeddelandenPerAmne(filter, true);
        return convertToSimpleResponseTvarsnittPerAmne(rows);
    }

    public KonDataResponse getAndelKompletteringar(MessagesFilter filter, int cutoff) {
        List<CountDTOAmne> rows = messageWidelineLoader.getKompletteringarPerIntyg(filter);
        return convertToAndelKompletteringar(rows, filter.getFrom(), filter.getNumberOfMonths(), cutoff);
    }

    public KonDataResponse getAndelKompletteringarAggregated(KonDataResponse resultToAggregateIn, MessagesFilter filter, int cutoff) {
        final KonDataResponse andelKompletteringar = getAndelKompletteringar(filter, cutoff);
        return getKonDataResponseAggregated(resultToAggregateIn, filter, cutoff, andelKompletteringar);
    }

    public KonDataResponse getKompletteringarPerFraga(MessagesFilter filter, int cutoff) {
        List<CountDTOAmne> rows = messageWidelineLoader.getKompletteringar(getMessagesFilterForKompletteringarPerFraga(filter));
        return convertToKompletteringarPerFraga(rows, filter.getFrom(), filter.getNumberOfMonths(), cutoff);
    }

    public SimpleKonResponse getKompletteringarPerFragaTvarsnittAggregated(
        SimpleKonResponse resultToAggregateIn, MessagesFilter filter, int cutoff) {
        final SimpleKonResponse kompletteringar = getKompletteringarPerFragaTvarsnitt(filter, cutoff);
        AvailableFilters availableFilters = resultToAggregateIn != null
            ? resultToAggregateIn.getAvailableFilters() : AvailableFilters.getForMeddelanden();
        final List<SimpleKonResponse> collect = Stream.of(resultToAggregateIn, kompletteringar)
            .filter(Objects::nonNull).collect(Collectors.toList());
        return SimpleKonResponse.merge(collect, true, availableFilters);
    }

    private KonDataResponse getKonDataResponseAggregated(KonDataResponse resultToAggregateIn, MessagesFilter filter,
        int cutoff, KonDataResponse andelKompletteringar) {
        final KonDataResponse resultToAggregate = resultToAggregateIn != null
            ? resultToAggregateIn : ResponseUtil.createEmptyKonDataResponse(andelKompletteringar);
        Iterator<KonDataRow> rowsNew = andelKompletteringar.getRows().iterator();
        Iterator<KonDataRow> rowsOld = resultToAggregate.getRows().iterator();
        List<KonDataRow> list = ResponseUtil.getKonDataRows(filter.getNumberOfMonths(), rowsNew, rowsOld, cutoff);
        AvailableFilters availableFilters = resultToAggregateIn != null
            ? resultToAggregateIn.getAvailableFilters() : AvailableFilters.getForMeddelanden();

        return new KonDataResponse(availableFilters, andelKompletteringar.getGroups(), list);
    }

    public SimpleKonResponse getAndelKompletteringarTvarsnitt(MessagesFilter filter) {
        List<CountDTOAmne> rows = messageWidelineLoader.getKompletteringarPerIntyg(filter);
        return convertToAndelKompletteringarTvarsnitt(rows);
    }

    public SimpleKonResponse getKompletteringarPerFragaTvarsnitt(MessagesFilter filter, int cutoff) {
        List<CountDTOAmne> rows = messageWidelineLoader.getKompletteringar(getMessagesFilterForKompletteringarPerFraga(filter));
        return convertToKompletteringarPerFragaTvarsnitt(rows, cutoff);
    }

    //Handles the requirement that it should be possible to apply intygsfilter
    // on the report even though Lisjp is the only valid type.
    private MessagesFilter getMessagesFilterForKompletteringarPerFraga(MessagesFilter filter) {
        final MessagesFilter messageFilterForSjukpenning = getMessageFilterForSjukpenningWithRange(filter,
            new Range(filter.getFrom(), filter.getTo()));
        return new MessagesFilter(messageFilterForSjukpenning, Collections.singletonList(MsgAmne.KOMPLT.name()));
    }

    public KonDataResponse getMessagesPerAmnePerEnhet(MessagesFilter filter, Map<HsaIdEnhet, String> idToNameMap, boolean vardenhetdepth) {
        List<CountDTOAmne> rows = messageWidelineLoader.getAntalMeddelandenPerAmne(filter, vardenhetdepth);
        return convertToMessagesPerAmnePerEnhet(rows, filter.getFrom(), filter.getNumberOfMonths(), idToNameMap);
    }

    public KonDataResponse getMessagesTvarsnittPerAmnePerEnhet(MessagesFilter filter, Map<HsaIdEnhet, String> idToNameMap,
                                                               boolean vardenhetdepth) {
        List<CountDTOAmne> rows = messageWidelineLoader.getAntalMeddelandenPerAmne(filter, vardenhetdepth);
        return convertToSimpleResponseTvarsnittPerAmnePerEnhet(rows, idToNameMap);
    }

    public KonDataResponse getMessagesPerAmnePerLakare(MessagesFilter filter) {
        List<CountDTOAmne> rows = messageWidelineLoader.getAntalMeddelandenPerAmne(filter, true);
        return convertToMessagesPerAmnePerLakare(rows, filter.getFrom(), filter.getNumberOfMonths());
    }

    public KonDataResponse getMessagesTvarsnittPerAmnePerLakare(MessagesFilter filter) {
        List<CountDTOAmne> rows = messageWidelineLoader.getAntalMeddelandenPerAmne(filter, true);
        return convertToSimpleResponseTvarsnittPerAmnePerLakare(rows);
    }

    private KonDataResponse convertToMessagesPerAmne(List<CountDTOAmne> rows, LocalDate start, int perioder) {
        Map<LocalDate, List<CountDTOAmne>> map = mapList(rows, CountDTOAmne::getDate);
        final MsgAmne[] msgAmnes = MsgAmne.values();
        final int seriesLength = msgAmnes.length;
        List<KonDataRow> result = getResult(start, perioder, 0, map, seriesLength, this::addCountOnAmne);
        return getKonDataResponse(result, msgAmnes);
    }

    private KonDataResponse getKonDataResponse(List<KonDataRow> result, MsgAmne[] msgAmnes) {
        final ArrayList<String> groups = new ArrayList<>();
        for (MsgAmne msgAmne : msgAmnes) {
            groups.add(msgAmne.name());
        }
        return new KonDataResponse(AvailableFilters.getForMeddelanden(), groups, result);
    }

    private void addCountOnAmne(int[] maleSeries, int[] femaleSeries, List<CountDTOAmne> dtos) {
        for (CountDTOAmne dto : dtos) {
            if (dto.getKon().equals(Kon.FEMALE)) {
                femaleSeries[dto.getAmne().ordinal()] += dto.getCount();
            } else {
                maleSeries[dto.getAmne().ordinal()] += dto.getCount();
            }
        }
    }

    private SimpleKonResponse convertToAndelKompletteringarTvarsnitt(List<CountDTOAmne> rows) {
        Map<String, List<CountDTOAmne>> map = mapList(rows, CountDTOAmne::getIntygTyp);
        final IntygType[] intygTypes = IntygType.values();
        final int seriesLength = intygTypes.length;
        AndelKompletteringarCount male = new AndelKompletteringarCount(seriesLength);
        AndelKompletteringarCount female = new AndelKompletteringarCount(seriesLength);

        for (IntygType intygType : intygTypes) {
            List<CountDTOAmne> allDtos = map.get(intygType.name());
            addCountAndelKompletteringar(male, female, allDtos);
        }

        final List<SimpleKonDataRow> result = new ArrayList<>();
        for (int j = 0, intygTypesLength = intygTypes.length; j < intygTypesLength; j++) {
            final IntygType intygType = intygTypes[j];
            if (intygType.isIncludedInKompletteringReport()) {
                int f = female.intyg[j] == 0 ? 0 : MAX_PERCENTAGE * female.kompl[j] / female.intyg[j];
                int m = male.intyg[j] == 0 ? 0 : MAX_PERCENTAGE * male.kompl[j] / male.intyg[j];
                final AndelExtras extras = new AndelExtras(female.intyg[j], female.kompl[j], male.intyg[j], male.kompl[j]);
                result.add(new SimpleKonDataRow(intygType.name(), f, m, extras));
            }
        }

        return new SimpleKonResponse(AvailableFilters.getForMeddelanden(), result);
    }

    private void addCountAndelKompletteringar(AndelKompletteringarCount male, AndelKompletteringarCount female,
        List<CountDTOAmne> allDtos) {
        if (allDtos != null) {
            final Map<String, List<CountDTOAmne>> dtosPerIntygid = allDtos.stream()
                .collect(Collectors.groupingBy(CountDTOAmne::getIntygid));
            for (List<CountDTOAmne> dtos : dtosPerIntygid.values()) {
                final CountDTOAmne aDto = dtos.get(0);
                final boolean isKomplt = dtos.stream().anyMatch(dto -> MsgAmne.KOMPLT.equals(dto.getAmne()));
                final int ordinal = IntygType.parseString(aDto.getIntygTyp()).ordinal();
                if (aDto.getKon().equals(Kon.FEMALE)) {
                    female.intyg[ordinal] += 1;
                    female.kompl[ordinal] += isKomplt ? 1 : 0;
                } else {
                    male.intyg[ordinal] += 1;
                    male.kompl[ordinal] += isKomplt ? 1 : 0;
                }
            }
        }
    }

    private static class AndelKompletteringarCount {

        int[] kompl, intyg;

        AndelKompletteringarCount(int size) {
            kompl = new int[size];
            intyg = new int[size];
        }
    }

    private static class FinalCountAndelKompletteringar {

        int kompl, intyg, andel;
    }

    private KonDataResponse convertToAndelKompletteringar(List<CountDTOAmne> rows, LocalDate start, int perioder, int cutoff) {
        List<KonDataRow> result = new ArrayList<>();
        Map<LocalDate, List<CountDTOAmne>> map = mapList(rows, CountDTOAmne::getDate);

        final IntygType[] intygTypes = IntygType.values();
        final int seriesLength = intygTypes.length;

        for (int i = 0; i < perioder; i++) {
            AndelKompletteringarCount male = new AndelKompletteringarCount(seriesLength);
            AndelKompletteringarCount female = new AndelKompletteringarCount(seriesLength);

            LocalDate temp = start.plusMonths(i);
            String displayDate = ReportUtil.toDiagramPeriod(temp);
            List<CountDTOAmne> allDtos = map.get(temp);
            addCountAndelKompletteringar(male, female, allDtos);
            final ArrayList<KonField> data = new ArrayList<>();
            for (int j = 0; j < seriesLength; j++) {
                if (intygTypes[j].isIncludedInKompletteringReport()) {
                    final FinalCountAndelKompletteringar femaleJ = getFinalCountAndelKompletteringar(cutoff, female, j);
                    final FinalCountAndelKompletteringar maleJ = getFinalCountAndelKompletteringar(cutoff, male, j);
                    final AndelExtras extras = new AndelExtras(femaleJ.intyg, femaleJ.kompl, maleJ.intyg, maleJ.kompl);
                    data.add(new KonField(femaleJ.andel, maleJ.andel, extras));
                }
            }
            result.add(new KonDataRow(displayDate, data));
        }

        final ArrayList<String> groups = new ArrayList<>();
        for (IntygType intygType : intygTypes) {
            if (intygType.isIncludedInKompletteringReport()) {
                groups.add(intygType.name());
            }
        }
        return new KonDataResponse(AvailableFilters.getForMeddelanden(), groups, result);
    }

    private FinalCountAndelKompletteringar getFinalCountAndelKompletteringar(int cutoff, AndelKompletteringarCount femaleCount, int index) {
        final FinalCountAndelKompletteringar female = new FinalCountAndelKompletteringar();
        boolean includeFemale = femaleCount.kompl[index] >= cutoff;
        female.kompl = includeFemale ? femaleCount.kompl[index] : 0;
        female.intyg = includeFemale ? femaleCount.intyg[index] : 0;
        female.andel = female.intyg == 0 ? 0 : MAX_PERCENTAGE * female.kompl / female.intyg;
        return female;
    }

    private SimpleKonResponse convertToKompletteringarPerFragaTvarsnitt(List<CountDTOAmne> rows, int cutoff) {
        final Intygsfraga[] intygsfraga = Intygsfraga.values();
        final int seriesLength = intygsfraga.length;
        int[] maleKompl = new int[seriesLength];
        int[] femaleKompl = new int[seriesLength];

        final List<CountDTOAmne> nullSafeRows = rows == null ? Collections.emptyList() : rows;
        for (CountDTOAmne row : nullSafeRows) {
            final String svarIdsString = row.getSvarIds();
            final String[] svarsIds = svarIdsString == null ? new String[0] : svarIdsString.split(",");
            for (String svarsId : svarsIds) {
                try {
                    final int[] genderData = row.getKon().equals(Kon.FEMALE) ? femaleKompl : maleKompl;
                    addFrageCount(genderData, svarsId);
                } catch (NumberFormatException e) {
                    LOG.warn("Unknown frageid: " + svarsId);
                }
            }
        }

        final List<SimpleKonDataRow> result = new ArrayList<>();
        for (int j = 0, intygTypesLength = intygsfraga.length; j < intygTypesLength; j++) {
            final Intygsfraga intygType = intygsfraga[j];
            final int female = handleCutoff(femaleKompl[j], cutoff);
            final int male = handleCutoff(maleKompl[j], cutoff);
            result.add(new SimpleKonDataRow(intygType.getText(), female, male));
        }
        return new SimpleKonResponse(AvailableFilters.getForMeddelanden(), result);
    }

    private void addFrageCount(int[] count, String frageIdString) {
        try {
            final int frageId = Integer.parseInt(frageIdString.trim());
            final Optional<Intygsfraga> intygsfraga = Intygsfraga.getByFrageid(frageId);
            intygsfraga.ifPresent(fraga -> count[fraga.ordinal()] += 1);
        } catch (Exception e) {
            LOG.warn("Could not parse id to int: '" + frageIdString + "'");
        }
    }

    private int handleCutoff(int value, int cutoff) {
        return value >= cutoff ? value : 0;
    }

    private interface CountAdder {

        void addCount(int[] male, int[] female, List<CountDTOAmne> dtos);
    }

    private KonDataResponse convertToKompletteringarPerFraga(List<CountDTOAmne> rows, LocalDate start, int perioder, int cutoff) {
        Map<LocalDate, List<CountDTOAmne>> map = mapList(rows, CountDTOAmne::getDate);
        final Intygsfraga[] intygsfragas = Intygsfraga.values();
        final int seriesLength = intygsfragas.length;
        List<KonDataRow> result = getResult(start, perioder, cutoff, map, seriesLength, this::addCountOnFrageid);

        final ArrayList<String> groups = new ArrayList<>();
        for (int j = 0; j < seriesLength; j++) {
            groups.add(intygsfragas[j].getText());
        }
        return new KonDataResponse(AvailableFilters.getForMeddelanden(), groups, result);
    }

    private List<KonDataRow> getResult(LocalDate start, int perioder, int cutoff, Map<LocalDate, List<CountDTOAmne>> map,
        int seriesLength, CountAdder countAdder) {
        List<KonDataRow> result = new ArrayList<>();
        for (int i = 0; i < perioder; i++) {
            int[] maleIntyg = new int[seriesLength];
            int[] femaleIntyg = new int[seriesLength];

            LocalDate currentMonth = start.plusMonths(i);
            String displayDate = ReportUtil.toDiagramPeriod(currentMonth);

            List<CountDTOAmne> allDtos = map.get(currentMonth);

            if (allDtos != null) {
                countAdder.addCount(maleIntyg, femaleIntyg, allDtos);
            }

            final ArrayList<KonField> data = new ArrayList<>();
            for (int j = 0; j < seriesLength; j++) {
                final int femaleIntygJ = handleCutoff(femaleIntyg[j], cutoff);
                final int maleIntygJ = handleCutoff(maleIntyg[j], cutoff);
                data.add(new KonField(femaleIntygJ, maleIntygJ));
            }
            result.add(new KonDataRow(displayDate, data));
        }
        return result;
    }

    private void addCountOnFrageid(int[] maleIntyg, int[] femaleIntyg, List<CountDTOAmne> allDtos) {
        final Map<String, List<CountDTOAmne>> dtosPerIntygid = allDtos.stream()
            .collect(Collectors.groupingBy(CountDTOAmne::getIntygid));
        for (List<CountDTOAmne> dtos : dtosPerIntygid.values()) {
            for (CountDTOAmne aDto : dtos) {
                final String svarIdsString = aDto.getSvarIds();
                final Iterable<String> svarIds = Splitter.on(",").trimResults().omitEmptyStrings().split(svarIdsString);
                for (String svarId : svarIds) {
                    final int[] genderData = aDto.getKon().equals(Kon.FEMALE) ? femaleIntyg : maleIntyg;
                    addFrageCount(genderData, svarId);
                }
            }
        }
    }

    private <T, R> Map<T, List<R>> mapList(List<R> rows, Function<R, T> groupByFunction) {
        Map<T, List<R>> map;
        if (rows != null) {
            map = rows.stream().collect(Collectors.groupingBy(groupByFunction));
        } else {
            map = new HashMap<>();
        }
        return map;
    }

    private KonDataResponse convertToMessagesPerAmnePerLakare(List<CountDTOAmne> rows, LocalDate start, int perioder) {
        List<HsaIdLakare> lakares = getLakareFromRows(rows);
        final Map<HsaIdLakare, String> lakareNames = getLakareNames(lakares);
        return convertToMessagesPerAmnePerType(rows, start, perioder, lakares, CountDTOAmne::getLakareId, lakareNames::get);
    }

    private Map<HsaIdLakare, String> getLakareNames(List<HsaIdLakare> lakares) {
        final List<Lakare> allLakareInResponse = lakareManager.getAllSpecifiedLakares(lakares);
        final List<String> allLakarNames = allLakareInResponse.stream()
            .map(lakare -> getLakareName(lakare, false)).collect(Collectors.toList());
        final Set<String> duplicateNames = findUpperCaseDuplicates(allLakarNames);
        final Map<HsaIdLakare, String> mappedLakarnames = allLakareInResponse.stream().collect(
            Collectors.toMap(Lakare::getLakareId, lakare -> {
                final String lakareName = getLakareName(lakare, false);
                if (duplicateNames.contains(lakareName.toUpperCase())) {
                    return getLakareName(lakare, true);
                }
                return lakareName;
            }));
        return includeMissingLakare(mappedLakarnames, lakares);
    }

    private Map<HsaIdLakare, String> includeMissingLakare(Map<HsaIdLakare, String> mappedLakarnames, List<HsaIdLakare> allLakares) {
        final HashMap<HsaIdLakare, String> lakares = new HashMap<>(mappedLakarnames);
        allLakares.forEach(hsaIdLakare -> lakares.putIfAbsent(hsaIdLakare, hsaIdLakare.getId()));
        return lakares;
    }

    private Set<String> findUpperCaseDuplicates(Collection<String> list) {
        Set<String> duplicates = new HashSet<>();
        Set<String> uniques = new HashSet<>();

        for (String current : list) {
            final String currentUpper = current.toUpperCase();
            if (!uniques.add(currentUpper)) {
                duplicates.add(currentUpper);
            }
        }

        return duplicates;
    }

    private String getLakareName(Lakare lakare, boolean includeHsaId) {
        final String hsaid = lakare.getLakareId().getId();
        final String tilltalsNamn = lakare.getTilltalsNamn();
        final String efterNamn = lakare.getEfterNamn();
        if (Strings.isNullOrEmpty(tilltalsNamn) && Strings.isNullOrEmpty(efterNamn)) {
            return hsaid;
        }
        final String nameSuffix = includeHsaId ? " " + hsaid : "";
        return (tilltalsNamn + " " + efterNamn + nameSuffix).trim();
    }

    private List<HsaIdLakare> getLakareFromRows(List<CountDTOAmne> rows) {
        return rows != null
            ? rows.stream().map(CountDTOAmne::getLakareId).distinct().collect(Collectors.toList())
            : Collections.emptyList();
    }

    private KonDataResponse convertToMessagesPerAmnePerEnhet(List<CountDTOAmne> rows, LocalDate start, int perioder,
        Map<HsaIdEnhet, String> idToNameMap) {
        final Function<String, String> idToNameFunction = enhet -> idToNameMap.get(new HsaIdEnhet(enhet));
        final List<String> hsaIdEnhets = idToNameMap.keySet().stream().map(HsaIdAny::getId).collect(Collectors.toList());
        final Function<CountDTOAmne, String> typeInDto = dtoAmne -> new HsaIdEnhet(dtoAmne.getEnhet()).getId();
        return convertToMessagesPerAmnePerType(rows, start, perioder, hsaIdEnhets, typeInDto, idToNameFunction);
    }

    private <T> KonDataResponse convertToMessagesPerAmnePerType(List<CountDTOAmne> rows, LocalDate start, int perioder, List<T> types,
        Function<CountDTOAmne, T> typeInDto, Function<T, String> typeToName) {
        Map<LocalDate, List<CountDTOAmne>> map = mapList(rows, CountDTOAmne::getDate);
        final MsgAmne[] msgAmnes = MsgAmne.values();
        final int seriesLength = msgAmnes.length;

        List<KonDataRow> result = new ArrayList<>();
        for (int i = 0; i < perioder; i++) {
            LocalDate temp = start.plusMonths(i);
            String displayDate = ReportUtil.toDiagramPeriod(temp);
            List<CountDTOAmne> dtos = map.get(temp);
            final int typesSize = types.size();
            int[][][] series = getSeries(dtos, types, typeInDto, seriesLength, typesSize);

            final ArrayList<KonField> data = new ArrayList<>();
            for (int k = 0; k < typesSize; k++) {
                for (int j = 0; j < seriesLength; j++) {
                    data.add(new KonField(series[0][k][j], series[1][k][j]));
                }
            }
            result.add(new KonDataRow(displayDate, data));
        }

        final ArrayList<String> groups = new ArrayList<>();
        for (T type : types) {
            for (MsgAmne msgAmne : msgAmnes) {
                groups.add(typeToName.apply(type) + GROUP_NAME_SEPARATOR + msgAmne.name());
            }
        }
        return new KonDataResponse(AvailableFilters.getForMeddelanden(), groups, result);
    }

    private List<String> getEnhets(List<CountDTOAmne> rows) {
        return rows != null
            ? rows.stream().map(CountDTOAmne::getEnhet).distinct().collect(Collectors.toList())
            : Collections.emptyList();
    }

    private SimpleKonResponse convertToSimpleResponse(List<MessageWidelineLoader.CountDTO> rows, LocalDate start,
        int perioder) {
        List<SimpleKonDataRow> result = new ArrayList<>();
        Map<LocalDate, List<MessageWidelineLoader.CountDTO>> map;
        if (rows != null) {
            map = rows.stream().collect(Collectors.groupingBy(MessageWidelineLoader.CountDTO::getDate));
        } else {
            map = new HashMap<>();
        }

        for (int i = 0; i < perioder; i++) {
            LocalDate temp = start.plusMonths(i);
            String displayDate = ReportUtil.toDiagramPeriod(temp);

            List<MessageWidelineLoader.CountDTO> dtos = map.get(temp);
            int male = 0;
            int female = 0;

            if (dtos != null) {
                for (MessageWidelineLoader.CountDTO dto : dtos) {
                    if (dto.getKon().equals(Kon.FEMALE)) {
                        female += dto.getCount();
                    } else {
                        male += dto.getCount();
                    }
                }
            }

            result.add(new SimpleKonDataRow(displayDate, female, male));
        }

        return new SimpleKonResponse(AvailableFilters.getForMeddelanden(), result);
    }

    private SimpleKonResponse convertToSimpleResponseTvarsnitt(List<MessageWidelineLoader.CountDTO> rows) {
        List<SimpleKonDataRow> result = new ArrayList<>();

        int male = 0;
        int female = 0;

        for (MessageWidelineLoader.CountDTO row : rows) {
            if (row.getKon().equals(Kon.FEMALE)) {
                female += row.getCount();
            } else {
                male += row.getCount();
            }
        }

        result.add(new SimpleKonDataRow("Totalt", female, male));

        return new SimpleKonResponse(AvailableFilters.getForMeddelanden(), result);
    }

    private SimpleKonResponse convertToSimpleResponseTvarsnittPerAmne(List<CountDTOAmne> rows) {
        List<SimpleKonDataRow> result = new ArrayList<>();

        final MsgAmne[] msgAmnes = MsgAmne.values();
        final int seriesLength = msgAmnes.length;
        int[] maleSeries = new int[seriesLength];
        int[] femaleSeries = new int[seriesLength];

        addCountOnAmne(maleSeries, femaleSeries, rows);

        for (int i = 0; i < seriesLength; i++) {
            final MsgAmne msgAmne = msgAmnes[i];
            final String text = msgAmne.getText();
            result.add(new SimpleKonDataRow(text, femaleSeries[i], maleSeries[i], msgAmne));
        }

        return new SimpleKonResponse(AvailableFilters.getForMeddelanden(), result);
    }

    private KonDataResponse convertToSimpleResponseTvarsnittPerAmnePerLakare(List<CountDTOAmne> rows) {
        List<HsaIdLakare> lakares = getLakareFromRows(rows);
        final Map<HsaIdLakare, String> lakareNames = getLakareNames(lakares);
        return convertToSimpleResponseTvarsnittPerAmnePerType(rows, lakares, CountDTOAmne::getLakareId, lakareNames::get);
    }

    private KonDataResponse convertToSimpleResponseTvarsnittPerAmnePerEnhet(List<CountDTOAmne> rows, Map<HsaIdEnhet, String> idToNameMap) {
        final Function<String, String> idToNameFunction = enhet -> idToNameMap.get(new HsaIdEnhet(enhet));
        return convertToSimpleResponseTvarsnittPerAmnePerType(rows, getEnhets(rows), CountDTOAmne::getEnhet, idToNameFunction);
    }

    private <T> KonDataResponse convertToSimpleResponseTvarsnittPerAmnePerType(List<CountDTOAmne> rows, List<T> types,
        Function<CountDTOAmne, T> typeInDto,
        Function<T, String> typeToName) {
        final MsgAmne[] msgAmnes = MsgAmne.values();
        final int seriesLength = msgAmnes.length;
        List<KonDataRow> result = new ArrayList<>();

        final int typesSize = types.size();

        int[][][] series = getSeries(rows, types, typeInDto, seriesLength, typesSize);

        for (int k = 0; k < typesSize; k++) {
            final ArrayList<KonField> data = new ArrayList<>();
            for (int j = 0; j < seriesLength; j++) {
                data.add(new KonField(series[0][k][j], series[1][k][j]));
            }
            final String name = typeToName.apply(types.get(k));
            if (name != null) {
                result.add(new KonDataRow(name, data));
            }
        }

        return getKonDataResponse(result, msgAmnes);
    }

    private <T> int[][][] getSeries(List<CountDTOAmne> rows, List<T> types, Function<CountDTOAmne, T> typeInDto,
        int seriesLength, int typesSize) {
        int[][][] series = new int[2][][]; //First order is gender, second is type and third is amne
        series[0] = new int[typesSize][];
        series[1] = new int[typesSize][];
        for (int j = 0; j < typesSize; j++) {
            series[0][j] = new int[seriesLength];
            series[1][j] = new int[seriesLength];
        }
        if (rows != null) {
            for (CountDTOAmne dto : rows) {
                final int index = types.indexOf(typeInDto.apply(dto));
                if (index > -1) {
                    series[dto.getKon().equals(Kon.FEMALE) ? 0 : 1][index][dto.getAmne().ordinal()] += dto.getCount();
                }
            }
        }
        return series;
    }

    List<OverviewChartRowExtended> getOverviewKompletteringar(MessagesFilter messagesFilterWithoutRange,
        Range currentPeriod, Range previousPeriod) {
        MessagesFilter messageFilterCurrentRange = getMessageFilterForSjukpenningWithRange(messagesFilterWithoutRange, currentPeriod);
        MessagesFilter messageFilterPreviousRange = getMessageFilterForSjukpenningWithRange(messagesFilterWithoutRange, previousPeriod);
        final SimpleKonResponse andelKompletteringarCurrent = getAndelKompletteringarTvarsnitt(messageFilterCurrentRange);
        final SimpleKonResponse andelKompletteringarPrevious = getAndelKompletteringarTvarsnitt(messageFilterPreviousRange);
        final int current = getAndel(andelKompletteringarCurrent);
        final int previous = getAndel(andelKompletteringarPrevious);
        List<OverviewChartRowExtended> resp = new ArrayList<>();
        final int alternation = current - previous;
        resp.add(new OverviewChartRowExtended("Sjukpenningintyg med komplettering", current, alternation,
            ReportColor.ST_COLOR_01.getColor(), false));
        resp.add(new OverviewChartRowExtended("Sjukpenningintyg utan komplettering", MAX_PERCENTAGE - current, -1 * alternation,
            ReportColor.ST_COLOR_02.getColor(), false));
        return resp;
    }

    private int getAndel(SimpleKonResponse andelKompletteringar) {
        final AndelExtras andelExtras = (AndelExtras) andelKompletteringar.getRows().stream()
            .filter(p -> IntygType.parseString(p.getName()).isSjukpenningintyg()).findAny()
            .orElse(new SimpleKonDataRow(null, 0, 0)).getExtras();
        return andelExtras.getWhole() != 0 ? MAX_PERCENTAGE * andelExtras.getPart() / andelExtras.getWhole() : 0;
    }

    private MessagesFilter getMessageFilterForSjukpenningWithRange(MessagesFilter filter, Range range) {
        final Collection<String> intygstyper = filter.getIntygstyper() == null || filter.getIntygstyper().isEmpty()
            ? Collections.singleton(IntygType.SJUKPENNING.getText())
            : Stream.concat(
                filter.getIntygstyper().stream().filter(s -> IntygType.SJUKPENNING.getText().equals(s)),
                Stream.of("NoMatchingIntygTypeWithThisText")) //Make sure the filter is never empty (same as "all selected")
                .collect(Collectors.toSet());
        final HsaIdVardgivare vgid = filter.getVardgivarId();
        final LocalDate from = range.getFrom();
        final LocalDate to = range.getTo();
        final Collection<HsaIdEnhet> enheter = filter.getEnheter();
        final Collection<String> aldersgrupp = filter.getAldersgrupp();
        final Collection<String> diagnoser = filter.getDiagnoser();
        return new MessagesFilter(vgid, from, to, enheter, aldersgrupp, diagnoser, intygstyper);
    }

}
