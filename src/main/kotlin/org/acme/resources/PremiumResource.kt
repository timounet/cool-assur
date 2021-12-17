package org.acme.resources

import org.acme.services.ContractService
import org.acme.vo.Premium
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeIn
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import javax.enterprise.inject.Default
import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Tag(name = "Gestion des cotisations", description = "tout ce qui touche aux cotisations")
@Path("/v1/")
@Produces(MediaType.APPLICATION_JSON)
@SecurityScheme(
    securitySchemeName = "Authentication",
    description = "JWT token",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    `in` = SecuritySchemeIn.HEADER
)
class PremiumResource {

    @Inject
    @field: Default
    lateinit var service: ContractService

    @GET
    @Path("/contracts/{number}/premiums")
    @Operation(summary = "Retrouve les cotisation d'un contrat donné")
    @APIResponses(
        APIResponse(
            responseCode = "200", description = "liste avec succes",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = Schema(implementation = Array<Premium>::class)
            )]
        ), APIResponse(responseCode = "404", description = "contract Not found")
    )
    fun listUsersContracts(
        @Parameter(
            description = "numero de contrat",
            example = "c1",
            required = true
        )
        @PathParam("number") number: String,
        @Parameter(description = "débuter la liste à cette valeur offset, default 0", example = "0")
        @QueryParam("offset") offset: Int = 0,
        @Parameter(description = "nombre max d’élément dans la liste, default 20", example = "20")
        @QueryParam("limit") limit: Int = 20,
        @Parameter(description = "trier la liste sur un champ, defaut 'asc(dueDate)'")
        @QueryParam("sortBy") orderBy: String?
    ): Response {
        val tmp = service.getContracts().first { it.number == number }.premiums
        return Response.ok(
            tmp.subList(offset, (offset + limit).coerceAtMost(tmp.size))
        ).build()
    }
}