package org.acme

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition
import org.eclipse.microprofile.openapi.annotations.info.Contact
import org.eclipse.microprofile.openapi.annotations.info.Info
import org.eclipse.microprofile.openapi.annotations.servers.Server
import javax.ws.rs.ApplicationPath
import javax.ws.rs.core.Application

@ApplicationPath("/")
@OpenAPIDefinition(
    info = Info(
        title = "CoolAssur Application",
        description = "Cette Api permet d'interagir avec l'application coolAssur",
        version = "1.0",
        contact = Contact(name = "cool Assur fake contact", url = "https://cool.assur", email = "support@example.com")
    ),
    servers = [
        Server(url = "https://dev-france.apis.allianz/aq/formation/coolassur"),
        Server(url = "http://localhost:8080"),
        Server(url = "https://recette-host"),
        Server(url = "https://prod-host"),
        Server(url = "https://coolassur.adho.fr")
    ],
    tags = [

    ]
)
class WebApplication : Application() {
}