package se.inera.statistics.fileservice;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ListGetHsaUnitsResponseType {
    protected LocalDateTime startDate;
    protected LocalDateTime endDate;
    protected List<ListHsaUnitType> hsaUnits;
}
