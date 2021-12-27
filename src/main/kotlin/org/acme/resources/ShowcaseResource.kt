package org.acme.resources

import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Tag(name = "z Autres demonstrations", description = "une resource pour montrer d'autres comportements")
@Path("/v1/showcase")
class ShowcaseResource {

    @Inject
    @field: ConfigProperty(name = "quarkus.application.version", defaultValue = "defaultVersion")
    lateinit var version: String

    @GET
    @Path("/randomError")
    @Operation(summary = "Génere parfois une erreur aléatoire")
    @APIResponse(
        responseCode = "200", description = "random", content =
        [Content(
            mediaType = MediaType.TEXT_PLAIN,
            schema = Schema(type = SchemaType.STRING)
        )]
    )
    @Produces(MediaType.TEXT_PLAIN)
    fun randomError(): Response {
        val rnds = (0..1).random()
        if (rnds > 0) return Response.ok("Success: tout est ok").build()
        else
            return Response.serverError().entity("Failure: db est ko").build()

    }

    @GET
    @Path("/version")
    @Operation(summary = "Récupère la version logicielle")
    @APIResponse(
        responseCode = "200", description = "random", content =
        [Content(
            mediaType = MediaType.TEXT_PLAIN,
            schema = Schema(type = SchemaType.STRING)
        )]
    )
    @Produces(MediaType.TEXT_PLAIN)
    fun showVersion() = version

}
