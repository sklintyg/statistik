/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.KonDataRow;
import se.inera.statistics.service.report.model.KonField;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.warehouse.ResponseUtil;
import se.inera.statistics.service.warehouse.message.CountDTOAmne;
import se.inera.statistics.service.warehouse.message.MessageWidelineLoader;
import se.inera.statistics.service.warehouse.message.MsgAmne;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MessagesQuery {

    @Autowired
    private MessageWidelineLoader messageWidelineLoader;

    public KonDataResponse getMeddelandenPerAmneAggregated(HsaIdVardgivare vgid, KonDataResponse resultToAggregateIn,
                                                           Range range, int cutoff, Collection<HsaIdEnhet> enheter) {
        final int perioder = range.getNumberOfMonths();
        final LocalDate from = range.getFrom();
        final KonDataResponse messagesTvarsnittPerAmne = getMessagesPerAmne(vgid, enheter, from, perioder);
        final KonDataResponse resultToAggregate = resultToAggregateIn != null
                ? resultToAggregateIn : ResponseUtil.createEmptyKonDataResponse(messagesTvarsnittPerAmne);
        Iterator<KonDataRow> rowsNew = messagesTvarsnittPerAmne.getRows().iterator();
        Iterator<KonDataRow> rowsOld = resultToAggregate.getRows().iterator();
        List<KonDataRow> list = ResponseUtil.getKonDataRows(perioder, rowsNew, rowsOld, cutoff);
        return new KonDataResponse(messagesTvarsnittPerAmne.getGroups(), list);
    }

    public SimpleKonResponse<SimpleKonDataRow> getMessages(HsaIdVardgivare vardgivare, Collection<HsaIdEnhet> enheter, LocalDate start,
            int perioder) {
        LocalDate to = start.plusMonths(perioder);
        List<MessageWidelineLoader.CountDTO> rows = messageWidelineLoader.getAntalMeddelandenPerMonth(start, to, vardgivare, enheter);
        return convertToSimpleResponse(rows, start, perioder);
    }

    public SimpleKonResponse<SimpleKonDataRow> getMessagesTvarsnitt(HsaIdVardgivare vardgivare, Collection<HsaIdEnhet> enheter,
            LocalDate start, int perioder) {
        LocalDate to = start.plusMonths(perioder);
        List<MessageWidelineLoader.CountDTO> rows = messageWidelineLoader.getAntalMeddelandenPerMonth(start, to, vardgivare, enheter);
        return convertToSimpleResponseTvarsnitt(rows);
    }

    public KonDataResponse getMessagesPerAmne(HsaIdVardgivare vardgivare, Collection<HsaIdEnhet> enheter, LocalDate start,
                                                                int perioder) {
        LocalDate to = start.plusMonths(perioder);
        List<CountDTOAmne> rows = messageWidelineLoader.getAntalMeddelandenPerAmne(start, to, vardgivare, enheter);
        return convertToMessagesPerAmne(rows, start, perioder);
    }

    public SimpleKonResponse<SimpleKonDataRow> getMessagesTvarsnittPerAmne(HsaIdVardgivare vardgivare, Collection<HsaIdEnhet> enheter,
            LocalDate start, int perioder) {
        LocalDate to = start.plusMonths(perioder);
        List<CountDTOAmne> rows = messageWidelineLoader.getAntalMeddelandenPerAmne(start, to, vardgivare, enheter);
        return convertToSimpleResponseTvarsnittPerAmne(rows);
    }

    public SimpleKonResponse<SimpleKonDataRow> getAntalMeddelanden(LocalDate start, int perioder) {
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
            groups.add(msgAmne.getText());
        }
        return new KonDataResponse(groups, result);
    }

    private SimpleKonResponse<SimpleKonDataRow> convertToSimpleResponse(List<MessageWidelineLoader.CountDTO> rows, LocalDate start,
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

        return new SimpleKonResponse<>(result);
    }

    private SimpleKonResponse<SimpleKonDataRow> convertToSimpleResponseTvarsnitt(List<MessageWidelineLoader.CountDTO> rows) {
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

        return new SimpleKonResponse<>(result);
    }

    private SimpleKonResponse<SimpleKonDataRow> convertToSimpleResponseTvarsnittPerAmne(List<CountDTOAmne> rows) {
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
            result.add(new SimpleKonDataRow(text, femaleSeries[i], maleSeries[i]));
        }

        return new SimpleKonResponse<>(result);
    }

}
