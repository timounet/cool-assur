package org.acme.resources

import org.acme.vo.Token
import org.acme.vo.User
import org.eclipse.microprofile.metrics.MetricUnits
import org.eclipse.microprofile.metrics.annotation.Counted
import org.eclipse.microprofile.metrics.annotation.Timed
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeIn
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Tag(name = "Gestion des utilisateur", description = "une ressource pour les utilisateurs de l'application")
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Counted(name = "authenticateCount", description = "Compte le nombre d'appel à l'authentification")
@Timed(
    name = "authenticateTime",
    description = "Mesure le temps de réponse de l'authentification",
    unit = MetricUnits.MILLISECONDS
)
class UserResource {
    @GET
    @Path("/authenticate")
    @Operation(summary = "Authentifie et recupère un nouveau jeton jwt de l'utilisateur connecté")
    @APIResponse(
        responseCode = "200", description = "liste réussie",
        content = [Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = Schema(implementation = Token::class)
        )]
    )
    fun auth(): Response = Response.ok(Token()).build()


    @GET
    @Path("/me")
    @Operation(summary = "Information sur l'utilisateur connecté")
    @APIResponse(
        responseCode = "200", description = "liste réussie",
        content = [Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = Schema(implementation = User::class)
        )]
    )
    @Counted(name = "meCount", description = "Compte le nombre d'appel à me: mes infos")
    @Timed(
        name = "meTime",
        description = "Mesure le temps de réponse de à me: mes infos",
        unit = MetricUnits.MILLISECONDS
    )
    @SecurityScheme(
        securitySchemeName = "Authentication",
        description = "JWT token",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        `in` = SecuritySchemeIn.HEADER
    )
    fun me(): Response = Response.ok(User()).build()
}