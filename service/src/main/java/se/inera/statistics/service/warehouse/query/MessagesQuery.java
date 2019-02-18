/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdLakare;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
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

    public static final String GROUP_NAME_SEPARATOR = " : ";

    @Autowired
    private MessageWidelineLoader messageWidelineLoader;

    @Autowired
    private LakareManager lakareManager;

    public KonDataResponse getMeddelandenPerAmneAggregated(KonDataResponse resultToAggregateIn, MessagesFilter filter, int cutoff) {
        final KonDataResponse messagesTvarsnittPerAmne = getMessagesPerAmne(filter);
        return getKonDataResponseAggregated(resultToAggregateIn, filter, cutoff, messagesTvarsnittPerAmne);
    }

    public KonDataResponse getMeddelandenPerAmneOchEnhetAggregated(KonDataResponse resultToAggregateIn, MessagesFilter filter,
                                                                   int cutoff, Map<HsaIdEnhet, String> idToNameMap) {
        final KonDataResponse messagesTvarsnittPerAmne = getMessagesTvarsnittPerAmnePerEnhet(filter, idToNameMap);
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
        List<CountDTOAmne> rows = messageWidelineLoader.getAntalMeddelandenPerAmne(filter);
        return convertToMessagesPerAmne(rows, filter.getFrom(), filter.getNumberOfMonths());
    }

    public SimpleKonResponse getMessagesTvarsnittPerAmne(MessagesFilter filter) {
        List<CountDTOAmne> rows = messageWidelineLoader.getAntalMeddelandenPerAmne(filter);
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
        return convertToAndelKompletteringarTvarsnitt(rows, filter.getFrom(), filter.getNumberOfMonths());
    }

    public KonDataResponse getMessagesPerAmnePerEnhet(MessagesFilter filter, Map<HsaIdEnhet, String> idToNameMap) {
        List<CountDTOAmne> rows = messageWidelineLoader.getAntalMeddelandenPerAmne(filter);
        return convertToMessagesPerAmnePerEnhet(rows, filter.getFrom(), filter.getNumberOfMonths(), idToNameMap);
    }

    public KonDataResponse getMessagesTvarsnittPerAmnePerEnhet(MessagesFilter filter, Map<HsaIdEnhet, String> idToNameMap) {
        List<CountDTOAmne> rows = messageWidelineLoader.getAntalMeddelandenPerAmne(filter);
        return convertToSimpleResponseTvarsnittPerAmnePerEnhet(rows, idToNameMap);
    }

    public KonDataResponse getMessagesPerAmnePerLakare(MessagesFilter filter) {
        List<CountDTOAmne> rows = messageWidelineLoader.getAntalMeddelandenPerAmne(filter);
        return convertToMessagesPerAmnePerLakare(rows, filter.getFrom(), filter.getNumberOfMonths());
    }

    public KonDataResponse getMessagesTvarsnittPerAmnePerLakare(MessagesFilter filter) {
        List<CountDTOAmne> rows = messageWidelineLoader.getAntalMeddelandenPerAmne(filter);
        return convertToSimpleResponseTvarsnittPerAmnePerLakare(rows);
    }

    public SimpleKonResponse getAntalMeddelanden(LocalDate start, int perioder) {
        LocalDate to = start.plusMonths(perioder);
        List<MessageWidelineLoader.CountDTO> rows = messageWidelineLoader.getAntalMeddelandenPerMonth(start, to);

        return convertToSimpleResponse(rows, start, perioder);
    }

    private KonDataResponse convertToMessagesPerAmne(List<CountDTOAmne> rows, LocalDate start, int perioder) {
        List<KonDataRow> result = new ArrayList<>();
        Map<LocalDate, List<CountDTOAmne>> map;
        if (rows != null) {
            map = rows.stream().collect(Collectors.groupingBy(CountDTOAmne::getDate));
        } else {
            map = new HashMap<>();
        }

        final MsgAmne[] msgAmnes = MsgAmne.values();
        final int seriesLength = msgAmnes.length;

        for (int i = 0; i < perioder; i++) {
            int[] maleSeries = new int[seriesLength];
            int[] femaleSeries = new int[seriesLength];

            LocalDate temp = start.plusMonths(i);
            String displayDate = ReportUtil.toDiagramPeriod(temp);

            List<CountDTOAmne> dtos = map.get(temp);

            if (dtos != null) {
                for (CountDTOAmne dto : dtos) {
                    if (dto.getKon().equals(Kon.FEMALE)) {
                        femaleSeries[dto.getAmne().ordinal()] += dto.getCount();
                    } else {
                        maleSeries[dto.getAmne().ordinal()] += dto.getCount();
                    }
                }
            }

            final ArrayList<KonField> data = new ArrayList<>();
            for (int j = 0; j < seriesLength; j++) {
                data.add(new KonField(femaleSeries[j], maleSeries[j]));
            }
            result.add(new KonDataRow(displayDate, data));
        }

        final ArrayList<String> groups = new ArrayList<>();
        for (MsgAmne msgAmne : msgAmnes) {
            groups.add(msgAmne.name());
        }
        return new KonDataResponse(AvailableFilters.getForMeddelanden(), groups, result);
    }

    private SimpleKonResponse convertToAndelKompletteringarTvarsnitt(List<CountDTOAmne> rows, LocalDate from, int numberOfMonths) {
        Map<String, List<CountDTOAmne>> map;
        if (rows != null) {
            map = rows.stream().collect(Collectors.groupingBy(CountDTOAmne::getIntygTyp));
        } else {
            map = new HashMap<>();
        }

        final IntygType[] intygTypes = IntygType.values();
        final int seriesLength = intygTypes.length;


        int[] maleKompl = new int[seriesLength];
        int[] femaleKompl = new int[seriesLength];
        int[] maleIntyg = new int[seriesLength];
        int[] femaleIntyg = new int[seriesLength];

        for (IntygType intygType : intygTypes) {
            List<CountDTOAmne> allDtos = map.get(intygType.name());
            if (allDtos != null) {
                final Map<String, List<CountDTOAmne>> dtosPerIntygid = allDtos.stream()
                        .collect(Collectors.groupingBy(CountDTOAmne::getIntygid));
                for (List<CountDTOAmne> dtos : dtosPerIntygid.values()) {
                    final CountDTOAmne aDto = dtos.get(0);
                    final boolean isKomplt = dtos.stream().anyMatch(dto -> MsgAmne.KOMPLT.equals(dto.getAmne()));
                    final int ordinal = IntygType.parseString(aDto.getIntygTyp()).ordinal();
                    if (aDto.getKon().equals(Kon.FEMALE)) {
                        femaleIntyg[ordinal] += 1;
                        femaleKompl[ordinal] += isKomplt ? 1 : 0;
                    } else {
                        maleIntyg[ordinal] += 1;
                        maleKompl[ordinal] += isKomplt ? 1 : 0;
                    }
                }
            }
        }

        final int percentConvertion = 100;
        final List<SimpleKonDataRow> result = new ArrayList<>();
        for (int j = 0, intygTypesLength = intygTypes.length; j < intygTypesLength; j++) {
            final IntygType intygType = intygTypes[j];
            if (intygType.isIncludedInKompletteringReport()) {
                int f = femaleIntyg[j] == 0 ? 0 : percentConvertion * femaleKompl[j] / femaleIntyg[j];
                int m = maleIntyg[j] == 0 ? 0 : percentConvertion * maleKompl[j] / maleIntyg[j];
                final AndelExtras extras = new AndelExtras(femaleIntyg[j], femaleKompl[j], maleIntyg[j], maleKompl[j]);
                result.add(new SimpleKonDataRow(intygType.name(), f, m, extras));
            }
        }

        return new SimpleKonResponse(AvailableFilters.getForMeddelanden(), result);
    }

    private KonDataResponse convertToAndelKompletteringar(List<CountDTOAmne> rows, LocalDate start, int perioder, int cutoff) {
        List<KonDataRow> result = new ArrayList<>();
        Map<LocalDate, List<CountDTOAmne>> map;
        if (rows != null) {
            map = rows.stream().collect(Collectors.groupingBy(CountDTOAmne::getDate));
        } else {
            map = new HashMap<>();
        }

        final IntygType[] intygTypes = IntygType.values();
        final int seriesLength = intygTypes.length;

        for (int i = 0; i < perioder; i++) {
            int[] maleKompl = new int[seriesLength];
            int[] femaleKompl = new int[seriesLength];
            int[] maleIntyg = new int[seriesLength];
            int[] femaleIntyg = new int[seriesLength];

            LocalDate temp = start.plusMonths(i);
            String displayDate = ReportUtil.toDiagramPeriod(temp);

            List<CountDTOAmne> allDtos = map.get(temp);

            if (allDtos != null) {
                final Map<String, List<CountDTOAmne>> dtosPerIntygid = allDtos.stream()
                        .collect(Collectors.groupingBy(CountDTOAmne::getIntygid));
                for (List<CountDTOAmne> dtos : dtosPerIntygid.values()) {
                    final CountDTOAmne aDto = dtos.get(0);
                    final boolean isKomplt = dtos.stream().anyMatch(dto -> MsgAmne.KOMPLT.equals(dto.getAmne()));
                    final int ordinal = IntygType.parseString(aDto.getIntygTyp()).ordinal();
                    if (aDto.getKon().equals(Kon.FEMALE)) {
                        femaleIntyg[ordinal] += 1;
                        femaleKompl[ordinal] += isKomplt ? 1 : 0;
                    } else {
                        maleIntyg[ordinal] += 1;
                        maleKompl[ordinal] += isKomplt ? 1 : 0;
                    }
                }
            }

            final int percentConvertion = 100;
            final ArrayList<KonField> data = new ArrayList<>();
            for (int j = 0; j < seriesLength; j++) {
                if (intygTypes[j].isIncludedInKompletteringReport()) {
                    boolean includeFemale = femaleKompl[j] >= cutoff;
                    final int femaleKomplJ = includeFemale ? femaleKompl[j] : 0;
                    final int femaleIntygJ = includeFemale ? femaleIntyg[j] : 0;
                    int f = femaleIntygJ == 0 ? 0 : percentConvertion * femaleKomplJ / femaleIntygJ;

                    boolean includeMale = maleKompl[j] >= cutoff;
                    final int maleKomplJ = includeMale ? maleKompl[j] : 0;
                    final int maleIntygJ = includeMale ? maleIntyg[j] : 0;
                    int m = maleIntygJ == 0 ? 0 : percentConvertion * maleKomplJ / maleIntygJ;

                    data.add(new KonField(f, m, new AndelExtras(femaleIntygJ, femaleKomplJ, maleIntygJ, maleKomplJ)));
                }
            }
            result.add(new KonDataRow(displayDate, data));
        }

        final ArrayList<String> groups = new ArrayList<>();
        for (int j = 0; j < seriesLength; j++) {
            if (intygTypes[j].isIncludedInKompletteringReport()) {
                groups.add(intygTypes[j].name());
            }
        }
        return new KonDataResponse(AvailableFilters.getForMeddelanden(), groups, result);
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
        return convertToMessagesPerAmnePerType(rows, start, perioder, getEnhets(rows), CountDTOAmne::getEnhet, idToNameFunction);
    }

    private <T> KonDataResponse convertToMessagesPerAmnePerType(List<CountDTOAmne> rows, LocalDate start, int perioder, List<T> types,
                                                                Function<CountDTOAmne, T> typeInDto, Function<T, String> typeToName) {
        Map<LocalDate, List<CountDTOAmne>> map;
        if (rows != null) {
            map = rows.stream().collect(Collectors.groupingBy(CountDTOAmne::getDate));
        } else {
            map = new HashMap<>();
        }

        final MsgAmne[] msgAmnes = MsgAmne.values();
        final int seriesLength = msgAmnes.length;


        List<KonDataRow> result = new ArrayList<>();
        for (int i = 0; i < perioder; i++) {
            int[][][] series = new int[2][][]; //First order is gender, second is type and third is amne
            series[0] = new int[types.size()][];
            series[1] = new int[types.size()][];
            for (int j = 0; j < types.size(); j++) {
                series[0][j] = new int[seriesLength];
                series[1][j] = new int[seriesLength];
            }

            LocalDate temp = start.plusMonths(i);
            String displayDate = ReportUtil.toDiagramPeriod(temp);

            List<CountDTOAmne> dtos = map.get(temp);

            if (dtos != null) {
                for (CountDTOAmne dto : dtos) {
                    final int index = types.indexOf(typeInDto.apply(dto));
                    series[dto.getKon().equals(Kon.FEMALE) ? 0 : 1][index][dto.getAmne().ordinal()] += dto.getCount();
                }
            }

            final ArrayList<KonField> data = new ArrayList<>();
            for (int k = 0; k < types.size(); k++) {
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
            int male = 0;
            int female = 0;

            List<MessageWidelineLoader.CountDTO> dtos = map.get(temp);

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

        for (CountDTOAmne dto : rows) {
            if (dto.getKon().equals(Kon.FEMALE)) {
                femaleSeries[dto.getAmne().ordinal()] += dto.getCount();
            } else {
                maleSeries[dto.getAmne().ordinal()] += dto.getCount();
            }
        }

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
        int[][][] series = new int[2][][]; //First order is gender, second is type and third is amne
        series[0] = new int[typesSize][];
        series[1] = new int[typesSize][];
        for (int j = 0; j < typesSize; j++) {
            series[0][j] = new int[seriesLength];
            series[1][j] = new int[seriesLength];
        }
        for (CountDTOAmne dto : rows) {
            final int index = types.indexOf(typeInDto.apply(dto));
            series[dto.getKon().equals(Kon.FEMALE) ? 0 : 1][index][dto.getAmne().ordinal()] += dto.getCount();
        }

        for (int k = 0; k < typesSize; k++) {
            final ArrayList<KonField> data = new ArrayList<>();
            for (int j = 0; j < seriesLength; j++) {
                data.add(new KonField(series[0][k][j], series[1][k][j]));
            }
            result.add(new KonDataRow(typeToName.apply(types.get(k)), data));
        }

        final ArrayList<String> groups = new ArrayList<>();
        for (MsgAmne msgAmne : msgAmnes) {
            groups.add(msgAmne.name());
        }
        return new KonDataResponse(AvailableFilters.getForMeddelanden(), groups, result);
    }

    public List<OverviewChartRowExtended> getOverviewKompletteringar(MessagesFilter messagesFilterWithoutRange,
                                                                     Range currentPeriod, Range previousPeriod) {
        MessagesFilter messageFilterCurrentRange = getMessageFilterForSjukpenningWithRange(messagesFilterWithoutRange, currentPeriod);
        MessagesFilter messageFilterPreviousRange = getMessageFilterForSjukpenningWithRange(messagesFilterWithoutRange, previousPeriod);
        final SimpleKonResponse andelKompletteringarCurrent = getAndelKompletteringarTvarsnitt(messageFilterCurrentRange);
        final SimpleKonResponse andelKompletteringarPrevious = getAndelKompletteringarTvarsnitt(messageFilterPreviousRange);
        final AndelExtras currentExtras = (AndelExtras) andelKompletteringarCurrent.getRows().stream()
                .filter(p -> IntygType.parseString(p.getName()).isSjukpenningintyg()).findAny()
                .orElse(new SimpleKonDataRow(null, 0, 0)).getExtras();
        final int maxPercentage = 100;
        final int current = currentExtras.getWhole() != 0 ? maxPercentage * currentExtras.getPart() / currentExtras.getWhole() : 0;
        final AndelExtras previousExtras = (AndelExtras) andelKompletteringarPrevious.getRows().stream()
                .filter(p -> IntygType.parseString(p.getName()).isSjukpenningintyg()).findAny()
                .orElse(new SimpleKonDataRow(null, 0, 0)).getExtras();
        final int previous = previousExtras.getWhole() != 0 ? maxPercentage * previousExtras.getPart() / previousExtras.getWhole() : 0;
        List<OverviewChartRowExtended> resp = new ArrayList<>();
        final int alternation = current - previous;
        resp.add(new OverviewChartRowExtended("Sjukpenningintyg med komplettering", current, alternation,
                ReportColor.ST_COLOR_01.getColor(), false));
        resp.add(new OverviewChartRowExtended("Sjukpenningintyg utan komplettering", maxPercentage - current, -1 * alternation,
                ReportColor.ST_COLOR_02.getColor(), true));
        return resp;
    }

    private MessagesFilter getMessageFilterForSjukpenningWithRange(MessagesFilter filter, Range range) {
        final Collection<String> intygstyper = filter.getIntygstyper() == null || filter.getIntygstyper().isEmpty()
                ? Collections.singleton(IntygType.SJUKPENNING.name())
                : filter.getIntygstyper().stream().filter(s -> IntygType.SJUKPENNING.getText().equals(s)).collect(Collectors.toSet());
        final HsaIdVardgivare vgid = filter.getVardgivarId();
        final LocalDate from = range.getFrom();
        final LocalDate to = range.getTo();
        final Collection<HsaIdEnhet> enheter = filter.getEnheter();
        final Collection<String> aldersgrupp = filter.getAldersgrupp();
        final Collection<String> diagnoser = filter.getDiagnoser();
        return new MessagesFilter(vgid, from, to, enheter, aldersgrupp, diagnoser, intygstyper);
    }

}
