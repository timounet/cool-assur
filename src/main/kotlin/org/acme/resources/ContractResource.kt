package org.acme.resources

import org.acme.services.ContractService
import org.acme.vo.Contract
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.enums.ParameterStyle
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeIn
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import java.io.FileInputStream
import java.net.URI
import java.util.*
import javax.enterprise.inject.Default
import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Tag(name = "Gestion des contrats", description = "tout ce qui touche aux contrats")
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
class ContractResource {

    @Inject
    @field: Default
    lateinit var service: ContractService

    @GET
    @Path("/contracts")
    @Operation(summary = "liste les contrats")
    @APIResponse(
        responseCode = "200", description = "liste avec succes",
        content = [Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = Schema(implementation = Array<Contract>::class)
        )]
    )
    fun list(
        @Parameter(description = "débuter la liste à cette valeur offset, default 0", example = "0")
        @QueryParam("offset") offset: Int = 0,
        @Parameter(description = "nombre max d’élément dans la liste, default 20", example = "20")
        @QueryParam("limit") limit: Int = 20,
        @Parameter(description = "filtrer par status") @QueryParam("status") status: String?,
        @Parameter(
            description = "filtrer par numéros de contrats séparé par un pipe |",
            style = ParameterStyle.PIPEDELIMITED
        )
        @QueryParam("numbers") numbers: String?,
        @Parameter(description = "trier la liste sur un champ, defaut 'asc(number)'")
        @QueryParam("sortBy") orderBy: String?,
        @Parameter(description = "Recherche plain text", example = "c1 big risk")
        @QueryParam("query") query: String?
    ): Response = Response.ok(
        service.getContracts().sortedBy { it.number }.toList()
            .subList(offset, (offset + limit).coerceAtMost(service.getContracts().size))
    ).build()

    @POST
    @Path("/contracts")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Ajout un nouveau contrat")
    @APIResponse(
        responseCode = "201", description = "creation réussie",
        content = [Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = Schema(implementation = URI::class), example = "http://server:port/v1/contracts/c1"
        )]
    )
    fun add(
        @RequestBody(
            required = true,
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON, schema =
                Schema(implementation = Contract::class)
            )]
        ) c: Contract
    ): Response {
        service.addContract(c)
        return Response.created(URI.create("/v1/contracts/" + c.number)).build()
    }

    @PUT
    @Path("/contracts")
    @Operation(summary = "Modifie un contrat existant", deprecated = true)
    @Consumes(MediaType.APPLICATION_JSON)
    @APIResponses(
        APIResponse(
            responseCode = "201", description = "modification reussie", content = [Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = Schema(implementation = URI::class), example = "http://server:port/v1/contracts/c1"
            )]
        ),
        APIResponse(responseCode = "404", description = "Not found")
    )
    fun updateContract(
        @RequestBody(
            required = true,
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON, schema =
                Schema(implementation = Contract::class)
            )]
        ) c: Contract
    ): Response {
        val contrat = service.getContracts().find { it.number == c.number } ?: return Response.status(404).build()
        service.delContract(contrat)
        service.addContract(c)
        return Response.created(URI.create("/v1/contracts/" + c.number)).build()
    }

    @DELETE
    @Path("/contracts")
    @Operation(summary = "Supprime un contract", deprecated = true)
    @APIResponses(
        APIResponse(responseCode = "204", description = "suppression réussie"),
        APIResponse(responseCode = "404", description = "Not found")
    )
    fun del(
        @RequestBody(
            required = true,
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON, schema =
                Schema(implementation = Contract::class)
            )]
        ) c: Contract
    ): Response {
        val contrat = service.getContracts().find { it.number == c.number } ?: return Response.status(404).build()
        service.delContract(contrat)
        return Response.noContent().build()
    }


    @GET
    @Path("/contracts/{number}")
    @Operation(summary = "Recupere le contrat par son numéro")
    @APIResponses(
        APIResponse(
            responseCode = "200", description = "succes", content = [Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = Schema(implementation = Contract::class)
            )]
        ),
        APIResponse(responseCode = "404", description = "Not found")
    )
    fun getContract(
        @Parameter(description = "retrouver un contrat par son numéro", example = "c1")
        @PathParam("number") number: String
    ): Response {
        val contrat = service.getContracts().find { it.number == number } ?: return Response.status(404).build()
        return Response.ok(contrat).build()
    }


    @PUT
    @Path("/contracts/{number}")
    @Operation(summary = "Modifie un contrat existant par son numéro")
    @Consumes(MediaType.APPLICATION_JSON)
    @APIResponses(
        APIResponse(
            responseCode = "201", description = "modification reussie", content = [Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = Schema(implementation = URI::class), example = "http://server:port/v1/contracts/c1"
            )]
        ),
        APIResponse(responseCode = "404", description = "Not found")
    )
    fun updateContractByNum(
        @RequestBody(
            required = true,
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON, schema =
                Schema(implementation = Contract::class)
            )]
        ) c: Contract,
        @Parameter(description = "modifie un contrat par son numéro", example = "c1")
        @PathParam("number") number: String
    ): Response {
        val contrat = service.getContracts().find { it.number == number } ?: return Response.status(404).build()
        service.delContract(contrat)
        service.addContract(c)
        return Response.created(URI.create("/v1/contracts/$number")).build()
    }

    @DELETE
    @Path("/contracts/{number}")
    @Operation(summary = "Supprime un contract par son numéro")
    @APIResponses(
        APIResponse(responseCode = "204", description = "suppression réussie"),
        APIResponse(responseCode = "404", description = "Not found")
    )
    fun del(
        @Parameter(description = "retrouver un contrat par son numéro", example = "c1")
        @PathParam("number") number: String
    ): Response {
        val contrat = service.getContracts().find { it.number == number } ?: return Response.status(404).build()
        service.delContract(contrat)
        return Response.noContent().build()
    }


    @GET
    @Path("/contracts/{number}/image-preview")
    @Operation(summary = "Génere une image aperçu du contrat")
    @Produces("image/png")
    @APIResponse(
        responseCode = "200", description = "binaire: une image d'aperçu du contrat", content =
        [Content(
            mediaType = "image/png",
            schema = Schema(type = SchemaType.STRING, format = "binary")
        )]
    )
    fun contractImgPreview(
        @Parameter(description = "retrouver un contrat par son numéro", example = "c1")
        @PathParam("number") number: String
    ): Response = Response.ok(
        FileInputStream(
            ShowcaseResource::class.java.getResource("/samples/contract-preview.png")!!.file
        )
    ).header("Content-Disposition", "attachment; filename=contract-${number}-preview.png").build()


    @GET
    @Path("/persons/{id}/contracts")
    @Operation(summary = "Retrouve les contrats d'une personne")
    @APIResponses(
        APIResponse(
            responseCode = "200", description = "liste avec succes",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = Schema(implementation = Array<Contract>::class)
            )]
        ), APIResponse(responseCode = "404", description = "Person Not found")
    )
    fun listUsersContracts(
        @Parameter(
            description = "id de la personne",
            example = "1",
            required = true
        )
        @PathParam("id") id: Int,
        @Parameter(description = "débuter la liste à cette valeur offset, default 0", example = "0")
        @QueryParam("offset") offset: Int = 0,
        @Parameter(description = "nombre max d’élément dans la liste, default 20", example = "20")
        @QueryParam("limit") limit: Int = 20,
        @Parameter(description = "filtrer par status", deprecated = true) @QueryParam("status") status: String?,
        @Parameter(
            description = "filtrer par numéros de contrats séparé par un pipe |",
            style = ParameterStyle.PIPEDELIMITED
        )
        @QueryParam("numbers") numbers: String?,
        @Parameter(description = "trier la liste sur un champ, defaut 'asc(number)'")
        @QueryParam("sortBy") orderBy: String?,
        @Parameter(description = "Recherche plain text", example = "c1 big risk")
        @QueryParam("query") query: String?
    ): Response {
        val tmp = service.getContracts().filter { it.personId == id }
        return Response.ok(
            tmp.sortedBy { it.number }.toList()
                .subList(offset, (offset + limit).coerceAtMost(tmp.size))
        ).build()
    }

}