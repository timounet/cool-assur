package org.acme

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition
import org.eclipse.microprofile.openapi.annotations.info.Contact
import org.eclipse.microprofile.openapi.annotations.info.Info
import org.eclipse.microprofile.openapi.annotations.servers.Server
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import javax.ws.rs.ApplicationPath
import javax.ws.rs.core.Application

@ApplicationPath("/")
@OpenAPIDefinition(
    info = Info(title = "CoolAssur Application",
        description = "Cette Api permet d'interagir avec l'application coolAssur",
        version = "1.0",
        contact = Contact(name = "cool Assur fake contact", url = "https://cool.assur")
    ),
    servers = [
        Server(url = "https://localhost:8080"),
        Server(url = "http://recette-host"),
        Server(url = "http://prod-host")
    ],
    tags = [
        Tag(name = "contrat", description = "tout ce qui touche aux contrats")
    ]
)
class WebApplication: Application() {
}