package org.acme.resources

import org.acme.services.ContractService
import org.acme.vo.Payment
import org.eclipse.microprofile.metrics.MetricUnits
import org.eclipse.microprofile.metrics.annotation.Counted
import org.eclipse.microprofile.metrics.annotation.Timed
import org.eclipse.microprofile.openapi.annotations.Operation
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
import java.net.URI
import javax.enterprise.inject.Default
import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Tag(name = "Gestion des paiements", description = "tout ce qui touche aux paiements")
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@SecurityScheme(
    securitySchemeName = "Authentication",
    description = "JWT token",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    `in` = SecuritySchemeIn.HEADER
)
class PaymentResource {

    @Inject
    @field: Default
    lateinit var service: ContractService

    @GET
    @Path("/contracts/{number}/payments")
    @Operation(summary = "Retrouve les paiements d'un contrat donné")
    @APIResponses(
        APIResponse(
            responseCode = "200", description = "liste avec succes",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = Schema(implementation = Array<Payment>::class)
            )]
        ), APIResponse(responseCode = "404", description = "contract Not found")
    )
    @Counted(name = "ContractPaymentsListCount", description = "Compte le nombre d'appel à la liste de paiments par contrats")
    @Timed(
        name = "ContractPaymentsListTime",
        description = "Mesure le temps de réponse de la liste de paiments par contrats",
        unit = MetricUnits.MILLISECONDS
    )
    fun listContractPayments(
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
        val tmp = service.getContracts().first { it.number == number }.payments
        return Response.ok(
            tmp.subList(offset, (offset + limit).coerceAtMost(tmp.size))
        ).build()
    }

    @POST
    @Path("/contracts/{number}/payments")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Ajout un nouveau paiement de prime au contrat")
    @APIResponses(
        APIResponse(
            responseCode = "201", description = "creation réussie",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = Schema(implementation = URI::class), example = "http://server:port/v1/contracts/c1/payments"
            )]
        ),
        APIResponse(responseCode = "404", description = "Not found")
    )
    @Counted(name = "ContractPaymentsAddCount", description = "Compte le nombre d'appel à la creation de paiments par contrats")
    @Timed(
        name = "ContractPaymentsAddTime",
        description = "Mesure le temps de réponse de la creation de paiments par contrats",
        unit = MetricUnits.MILLISECONDS
    )
    fun addContractPayment(
        @RequestBody(
            required = true,
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON, schema =
                Schema(implementation = Payment::class)
            )]
        ) p: Payment,
        @Parameter(
            description = "numero de contrat",
            example = "c1",
            required = true
        ) @PathParam("number") number: String
    ): Response {
        val contract = service.getContracts().find { it.number == number } ?: return Response.status(404).build()
        service.delContract(contract)
        contract.payments = contract.payments.plusElement(p)
        service.addContract(contract)
        return Response.created(URI.create("/v1/contracts/$number/payments")).build()
    }


    @DELETE
    @Path("/contracts/{number}/payments")
    @Operation(summary = "Supprime un paiement au contract")
    @APIResponses(
        APIResponse(responseCode = "204", description = "suppression réussie"),
        APIResponse(responseCode = "404", description = "Not found")
    )
    @Counted(name = "ContractPaymentsDelCount", description = "Compte le nombre d'appel à la suppression de paiments par contrats")
    @Timed(
        name = "ContractPaymentsDelTime",
        description = "Mesure le temps de réponse de la suppression de paiments par contrats",
        unit = MetricUnits.MILLISECONDS
    )
    fun delContractPayment(
        @RequestBody(
            required = true,
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON, schema =
                Schema(implementation = Payment::class)
            )]
        ) p: Payment,
        @Parameter(
            description = "numero de contrat",
            example = "c1",
            required = true
        ) @PathParam("number") number: String
    ): Response {
        val contract = service.getContracts().find { it.number == number } ?: return Response.status(404).build()
        val payment =
            contract.payments.find { it.amount == p.amount && it.date == p.date } ?: return Response.status(
                404
            )
                .build()
        service.delContract(contract)
        contract.payments = contract.payments.minusElement(payment)
        service.addContract(contract)
        return Response.noContent().build()
    }
}