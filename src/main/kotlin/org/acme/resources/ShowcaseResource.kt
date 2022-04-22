package org.acme.resources

import org.acme.sample.SampleValues
import org.acme.vo.Person
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.metrics.MetricUnits
import org.eclipse.microprofile.metrics.annotation.Counted
import org.eclipse.microprofile.metrics.annotation.Timed
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import java.net.URI
import java.util.*
import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Tag(name = "z Autres demonstrations", description = "une ressource pour montrer d'autres comportements")
@Path("/showcase")
class ShowcaseResource {

    @Inject
    @field: ConfigProperty(name = "quarkus.application.version", defaultValue = "defaultVersion")
    lateinit var version: String

    private var persons: Set<Person> = Collections.synchronizedSet(LinkedHashSet())

    init {
        persons = persons.plusElement(SampleValues.jason).plusElement(SampleValues.ripley)
    }

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
    @Counted(name = "randomErrorCount", description = "Compte le nombre d'appel à randomError")
    @Timed(
        name = "randomErrorTime",
        description = "Mesure le temps de réponse de la liste de randomError",
        unit = MetricUnits.MILLISECONDS
    )
    @Produces(MediaType.TEXT_PLAIN)
    fun randomError(): Response {
        val rnds = (0..1).random()
        return if (rnds > 0) Response.ok("Success: tout est ok").build()
        else
            Response.serverError().entity("Failure: db est ko").build()

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
    @Counted(name = "versionCount", description = "Compte le nombre d'appel à version")
    @Timed(
        name = "versionTime",
        description = "Mesure le temps de réponse de la liste de version",
        unit = MetricUnits.MILLISECONDS
    )
    @Produces(MediaType.TEXT_PLAIN)
    fun showVersion() = version


    @GET
    @Path("/persons")
    @Operation(summary = "liste les personnes. A UTILISER DANS LE WORKSHOP PARTIE 2 de la formation")
    @APIResponse(
        responseCode = "200", description = "liste réussie",
        content = [Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = Schema(implementation = Array<Person>::class)
        )]
    )
    fun list(
        @Parameter(description = "débuter la liste à cette valeur offset")
        @DefaultValue("0")
        @QueryParam("offset") offset: Int,
        @Parameter(description = "nombre max d’élément dans la liste")
        @DefaultValue("20")
        @QueryParam("limit") limit: Int,
        @Parameter(description = "filtrer par prénom")
        @QueryParam("firstname") firstName: String?,
        @Parameter(description = "filtrer par nom")
        @QueryParam("lastname") lastName: String?,
        @Parameter(description = "trier la liste sur un champ")
        @DefaultValue("asc(id)")
        @QueryParam("sortBy") orderBy: String?,
        @Parameter(description = "Recherche plain text", example = "Bruce Ouillis Paris")
        @QueryParam("query") query: String?
    ): Response =
        Response.ok(persons.sortedBy { it.id }.toList().subList(offset, (offset + limit).coerceAtMost(persons.size)))
            .build()

    @POST
    @Path("/persons")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Ajout une nouvelle personne. POUR DEMO A UTILISER DANS LE WORKSHOP PARTIE 2 de la formation")
    @APIResponse(
        responseCode = "201", description = "ajout réussi",
        content = [Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = Schema(implementation = URI::class), example = "http://server:port/v1/persons/1"
        )]
    )
    fun add(
        @RequestBody(
            required = true,
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON, schema =
                Schema(implementation = Person::class)
            )]
        ) p: Person
    ): Response {
        persons = persons.plus(p)
        return Response.created(URI.create("/v1/persons/" + p.id)).build()
    }


    @GET
    @Path("/persons/{id}")
    @Operation(summary = "liste les personnes. POUR DEMO A UTILISER DANS LE WORKSHOP PARTIE 2 de la formation")
    @APIResponses(
        APIResponse(
            responseCode = "200", description = "listing reussi", content = [Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = Schema(implementation = Person::class)
            )]
        ),
        APIResponse(responseCode = "404", description = "Not found")
    )
    fun getById(
        @Parameter(
            description = "retrouver une personne par id",
            example = "1",
            required = true
        )
        @PathParam("id") id: Int
    ): Response {
        persons.find { it.id == id } ?: return Response.status(404).build()
        return Response.ok(persons.first { it.id == id }).build()
    }

    @PUT
    @Path("/persons/{id}")
    @Operation(summary = "Modifie une personne existante. POUR DEMO A UTILISER DANS LE WORKSHOP PARTIE 2 de la formation")
    @Consumes(MediaType.APPLICATION_JSON)
    @APIResponses(
        APIResponse(
            responseCode = "201", description = "Modification réussie", content = [Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = Schema(implementation = URI::class), example = "http://server:port/v1/persons/1"
            )]
        ),
        APIResponse(responseCode = "404", description = "Not found")
    )
    fun updatePersonbyId(
        @Parameter(
            description = "retrouver une personne par id",
            example = "1",
            required = true
        )
        @PathParam("id") id: Int,
        @RequestBody(
            required = true,
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON, schema =
                Schema(implementation = Person::class)
            )]
        ) p: Person
    ): Response {
        val person = persons.find { it.id == id } ?: return Response.status(404).build()
        p.id = person.id
        persons = persons.minusElement(person).plusElement(p)
        return Response.created(URI.create("/v1/persons/" + person.id)).build()
    }

    @DELETE
    @Path("/persons/{id}")
    @Operation(summary = "Supprime une personne par id. POUR DEMO A UTILISER DANS LE WORKSHOP PARTIE 2 de la formation")
    @APIResponses(
        APIResponse(responseCode = "204", description = "Suppression réussie"),
        APIResponse(responseCode = "404", description = "Not found")
    )

    fun delById(
        @Parameter(
            description = "retrouver une personne par id",
            example = "1",
            required = true
        )
        @PathParam("id") id: Int
    ): Response {
        val person = persons.find { it.id == id } ?: return Response.status(404).build()
        persons = persons.minusElement(person)
        return Response.noContent().build()
    }

}
