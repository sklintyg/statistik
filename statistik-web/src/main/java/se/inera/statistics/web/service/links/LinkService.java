package se.inera.statistics.web.service.links;

/**
 * Created by eriklupander on 2017-05-31.
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.inera.intyg.infra.dynamiclink.model.DynamicLink;
import se.inera.intyg.infra.dynamiclink.service.DynamicLinkService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.Map;

@Path("/links")
@Component
public class LinkService {

    @Autowired
    private DynamicLinkService dynamicLinkService;

    @GET
    @Path("/")
    @Produces("application/json;charset=UTF-8")
    public Map<String, DynamicLink> getLinks() {
        return dynamicLinkService.getAllAsMap();
    }

}
