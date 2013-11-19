package se.inera.statistics.service.report.repository;

import org.springframework.transaction.annotation.Transactional;

public interface NationellUpdater {

    @Transactional
    void updateCasesPerMonth();

    @Transactional
    void updateAldersgrupp();

    @Transactional
    void updateDiagnosgrupp();

    @Transactional
    void updateDiagnosundergrupp();

    @Transactional
    void updateSjukfallslangd();

    @Transactional
    void updateSjukskrivningsgrad();

    void setCutoff(int cutoff);
}
