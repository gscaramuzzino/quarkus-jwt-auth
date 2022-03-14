package org.gs;

import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@SecurityScheme(
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT"
)
@Path("/cart")
@ApplicationScoped
public class AmazonCartResource {

    List<Item> items = new ArrayList();

    @GET
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Response getItems() {
        return Response.ok(items).build();
    }

    @POST
    @RolesAllowed("writer")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addItem(Item item) {
        items.add(item);
        return Response.ok(items).build();
    }

    @DELETE
    @RolesAllowed("admin")
    @Path("{id}")
    public Response deleteItem(@PathParam("id") Long id) {
        items.stream().filter(item -> item.getId().equals(id))
                .findFirst()
                .ifPresent(item -> items.remove(item));
        return Response.noContent().build();
    }
}
