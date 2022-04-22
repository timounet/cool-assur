package org.acme.resources

import org.acme.services.ContractService
import org.acme.vo.Premium
import org.eclipse.microprofile.metrics.MetricUnits
import org.eclipse.microprofile.metrics.annotation.Counted
import org.eclipse.microprofile.metrics.annotation.Timed
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import java.net.URI
import javax.enterprise.inject.Default
import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Tag(name = "Gestion des cotisations", description = "tout ce qui touche aux cotisations")
@Path("/v1/")
@Produces(MediaType.APPLICATION_JSON)
@SecurityRequirement(name = "CoolAssurAuthentication")
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
    @Counted(
        name = "ContractPremiumsListCount",
        description = "Compte le nombre d'appel à la liste de primes par contrats"
    )
    @Timed(
        name = "ContractPremiumsListTime",
        description = "Mesure le temps de réponse de la liste de primes par contrats",
        unit = MetricUnits.MILLISECONDS
    )
    fun listContractPremiums(
        @Parameter(
            description = "numero de contrat",
            example = "c1",
            required = true
        )
        @PathParam("number") number: String,
        @DefaultValue("0")
        @Parameter(description = "débuter la liste à cette valeur offset")
        @QueryParam("offset") offset: Int,
        @Parameter(description = "nombre max d’élément dans la liste")
        @DefaultValue("20")
        @QueryParam("limit") limit: Int = 20,
        @Parameter(description = "trier la liste sur un champ")
        @DefaultValue("asc(dueDate)")
        @QueryParam("sortBy") orderBy: String?
    ): Response {
        service.getContracts().find { it.number == number } ?: return Response.status(404).build()
        val tmp = service.getContracts().first { it.number == number }.premiums
        return Response.ok(
            tmp.subList(offset, (offset + limit).coerceAtMost(tmp.size))
        ).build()
    }

    @POST
    @Path("/contracts/{number}/premiums")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Ajout un nouvel appel de prime au contrat")
    @APIResponses(
        APIResponse(
            responseCode = "201", description = "creation réussie",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = Schema(implementation = URI::class), example = "http://server:port/v1/contracts/c1/premiums"
            )]
        ),
        APIResponse(responseCode = "404", description = "Not found")
    )
    @Counted(
        name = "ContractPremiumsAddCount",
        description = "Compte le nombre d'appel à la creation de primes par contrats"
    )
    @Timed(
        name = "ContractPremiumsAddTime",
        description = "Mesure le temps de réponse de la creation de primes par contrats",
        unit = MetricUnits.MILLISECONDS
    )
    fun addContractPremium(
        @RequestBody(
            required = true,
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON, schema =
                Schema(implementation = Premium::class)
            )]
        ) p: Premium,
        @Parameter(
            description = "numero de contrat",
            example = "c1",
            required = true
        ) @PathParam("number") number: String
    ): Response {
        val contract = service.getContracts().find { it.number == number } ?: return Response.status(404).build()
        service.delContract(contract)
        contract.premiums = contract.premiums.plusElement(p)
        service.addContract(contract)
        return Response.created(URI.create("/v1/contracts/$number/premiums")).build()
    }


    @DELETE
    @Path("/contracts/{number}/premiums")
    @Operation(summary = "Supprime un appel de prime au contract")
    @APIResponses(
        APIResponse(responseCode = "204", description = "suppression réussie"),
        APIResponse(responseCode = "404", description = "Not found")
    )
    @Counted(
        name = "ContractPremiumsDelCount",
        description = "Compte le nombre d'appel à la suppression de primes par contrats"
    )
    @Timed(
        name = "ContractPremiumsDelTime",
        description = "Mesure le temps de réponse de la suppression de primes par contrats",
        unit = MetricUnits.MILLISECONDS
    )
    fun delContractPremium(
        @RequestBody(
            required = true,
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON, schema =
                Schema(implementation = Premium::class)
            )]
        ) p: Premium,
        @Parameter(
            description = "numero de contrat",
            example = "c1",
            required = true
        ) @PathParam("number") number: String
    ): Response {
        val contract = service.getContracts().find { it.number == number } ?: return Response.status(404).build()
        val premium =
            contract.premiums.find { it.amount == p.amount && it.dueDate == p.dueDate } ?: return Response.status(
                404
            )
                .build()
        service.delContract(contract)
        contract.premiums = contract.premiums.minusElement(premium)
        service.addContract(contract)
        return Response.noContent().build()
    }
}