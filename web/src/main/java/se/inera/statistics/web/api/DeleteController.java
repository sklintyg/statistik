/*
 * Copyright (C) 2022 Inera AB (http://www.inera.se)
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

package se.inera.statistics.web.api;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import se.inera.statistics.service.DeleteCustomerData;
import se.inera.statistics.service.DeleteCustomerDataByIntygsIdDao;
import se.inera.statistics.service.DeletedEnhetDao;
import se.inera.statistics.service.DeletedVardgivare;

@Service
@Path("/internal/v1")//DO NOT CHANGE THIS URL!
public class DeleteController {

    private final DeleteCustomerData deleteCustomerData;

    @Autowired
    public DeleteController(DeleteCustomerData deleteCustomerData) {
        this.deleteCustomerData = deleteCustomerData;
    }

    @DELETE
    @Path("intygsidlist")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public List<DeleteCustomerDataByIntygsIdDao> deleteIntygsIdList(@RequestBody List<String> intygsIdList){
        return deleteCustomerData.deleteCustomerDataByIntygsId(intygsIdList);
    }

    @DELETE
    @Path("enhetsidlist")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public List<DeletedEnhetDao> deleteEnhetsIdList(@RequestBody List<String> enhetsIdList){
        return deleteCustomerData.deleteCustomerDataByEnhetsId(enhetsIdList);
    }

    @DELETE
    @Path("vardgivareidlist")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public List<DeletedVardgivare> deleteVardgivareIdList(@RequestBody List<String> vardgivareIdList){
        return deleteCustomerData.deleteCustomerDataByVardgivarId(vardgivareIdList);
    }
}
