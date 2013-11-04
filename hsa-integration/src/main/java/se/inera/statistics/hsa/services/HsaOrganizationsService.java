package se.inera.statistics.hsa.services;

import java.util.List;

import se.inera.statistics.hsa.model.Vardgivare;

/**
 * @author andreaskaltenbach
 */
public interface HsaOrganizationsService {

    /**
     * Returns a list of Vardgivare and authorized enheter where the HoS person is authorized to work at.
     */
    List<Vardgivare> getAuthorizedEnheterForHosPerson(String hosPersonHsaId);
}
