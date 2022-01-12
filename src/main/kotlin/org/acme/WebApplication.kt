package org.acme

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeIn
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType
import org.eclipse.microprofile.openapi.annotations.info.Contact
import org.eclipse.microprofile.openapi.annotations.info.Info
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme
import org.eclipse.microprofile.openapi.annotations.servers.Server
import javax.ws.rs.ApplicationPath
import javax.ws.rs.core.Application

@SecurityScheme(
    securitySchemeName = "CoolAssurAuthentication",
    description = "Authentication with APIKEY token in header",
    type = SecuritySchemeType.APIKEY,
    //bearerFormat = "JWT",
    apiKeyName = "token",
    `in` = SecuritySchemeIn.HEADER
)
@ApplicationPath("/")
@OpenAPIDefinition(
    info = Info(
        title = "CoolAssur Application",
        description = "Cette Api permet d'interagir avec l'application coolAssur",
        version = "1.0",
        contact = Contact(name = "cool Assur fake contact", url = "https://cool.assur", email = "support@example.com")
    ),
    servers = [
        Server(url = "https://uat-france.apis.allianz.com/aq/formation/coolassur", description = "uat avec ApiGee"),
        /*Server(url = "https://dev-france.apis.allianz/aq/formation/coolassur"),*/
        Server(url = "/", description = "local"),
        Server(url = "https://recette-host", description = "recette"),
        Server(url = "https://prod-host", description = "prod"),
        Server(url = "https://coolassur.adho.fr", description = "uat sans ApiGee")
    ],
    tags = [

    ]
)
class WebApplication : Application() {
}