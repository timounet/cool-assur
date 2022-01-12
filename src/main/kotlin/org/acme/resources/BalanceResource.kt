package org.acme.resources

import org.acme.services.ContractService
import org.eclipse.microprofile.metrics.MetricUnits
import org.eclipse.microprofile.metrics.annotation.Counted
import org.eclipse.microprofile.metrics.annotation.Timed
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import javax.enterprise.inject.Default
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.QueryParam
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Tag(name = "Gestion du solde", description = "tout ce qui touche aux soldes pour les comptes")
@Path("/v1/")
@SecurityRequirement(name = "CoolAssurAuthentication")
class BalanceResource {

    @Inject
    @field: Default
    lateinit var service: ContractService

    @GET
    @Path("/balance")
    @Operation(summary = "retrouve le solde")
    @APIResponse(
        responseCode = "200", description = "calcul le solde avec succes",
        content = [Content(
            mediaType = MediaType.TEXT_PLAIN,
            schema = Schema(implementation = Float::class)
        )]
    )
    @Counted(name = "balanceCount", description = "Compte le nombre d'appel au solde")
    @Timed(
        name = "balanceTime",
        description = "Mesure le temps de réponse du solde",
        unit = MetricUnits.MILLISECONDS
    )
    fun balance(
        @Parameter(description = "filtrer par numéro de contrat") @QueryParam("number") number: String?,
        @Parameter(description = "filtrer par id d'assuré") @QueryParam("id") id: Int = 0,
    ): Response {
        var contracts = service.getContracts().toList()
        if (!number.isNullOrEmpty()) contracts = contracts.filter { it.number == number }
        if (id != 0) contracts = contracts.filter { it.personId == id }
        var payments = 0F
        var premiums = 0F
        contracts.forEach { it.payments.forEach { it2 -> payments += it2.amount } }
        contracts.forEach { it.premiums.forEach { it2 -> premiums += it2.amount } }
        return Response.ok(payments - premiums).build()
    }

}