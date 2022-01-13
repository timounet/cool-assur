package org.acme.resources

import org.acme.sample.SampleValues
import org.acme.vo.Person
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
import java.util.*
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response


@Tag(name = "Gestion des personnes", description = "Une ressource orienté sur la gestion des personnes")
@Path("/v1/persons")
@Produces(MediaType.APPLICATION_JSON)
@SecurityRequirement(name = "CoolAssurAuthentication")
class PersonResouce {

    private var persons: Set<Person> = Collections.synchronizedSet(LinkedHashSet())

    init {
        persons = persons.plusElement(SampleValues.jason).plusElement(SampleValues.ripley)
    }

    @GET
    @Path("/")
    @Operation(summary = "liste les personnes")
    @APIResponse(
        responseCode = "200", description = "liste réussie",
        content = [Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = Schema(implementation = Array<Person>::class)
        )]
    )
    @Counted(name = "PersonsListCount", description = "Compte le nombre d'appel à la liste de personnes")
    @Timed(
        name = "PersonsListTime",
        description = "Mesure le temps de réponse de la liste de personnes",
        unit = MetricUnits.MILLISECONDS
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
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Ajout une nouvelle personne")
    @APIResponse(
        responseCode = "201", description = "ajout réussi",
        content = [Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = Schema(implementation = URI::class), example = "http://server:port/v1/persons/1"
        )]
    )
    @Counted(name = "PersonsAddCount", description = "Compte le nombre d'appel à la creation de personnes")
    @Timed(
        name = "PersonsAddTime",
        description = "Mesure le temps de réponse de la creation de personnes",
        unit = MetricUnits.MILLISECONDS
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

    @PUT
    @Path("/")
    @Operation(summary = "Modifie une personne existante")
    @Consumes(MediaType.APPLICATION_JSON)
    @APIResponses(
        APIResponse(
            responseCode = "201", description = "modification réussie", content = [Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = Schema(implementation = URI::class), example = "http://server:port/v1/persons/1"
            )]
        ),
        APIResponse(responseCode = "404", description = "Not found")
    )
    @Counted(name = "PersonsModifyCount", description = "Compte le nombre d'appel à la modification de personnes")
    @Timed(
        name = "PersonsModifyTime",
        description = "Mesure le temps de réponse de la modification de personnes",
        unit = MetricUnits.MILLISECONDS
    )
    fun updatePerson(
        @RequestBody(
            required = true,
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON, schema =
                Schema(implementation = Person::class)
            )]
        ) p: Person
    ): Response {
        val person = persons.find { it.id == p.id } ?: return Response.status(404).build()
        persons = persons.minusElement(person).plusElement(p)
        return Response.created(URI.create("/v1/persons/" + p.id)).build()
    }

    @DELETE
    @Path("/")
    @Operation(summary = "Supprime une personne")
    @Consumes(MediaType.APPLICATION_JSON)
    @APIResponses(
        APIResponse(responseCode = "204", description = "Suppression réussie"),
        APIResponse(responseCode = "404", description = "Not found")
    )
    @Counted(name = "PersonsDelCount", description = "Compte le nombre d'appel à la suppression de personnes")
    @Timed(
        name = "PersonsDelTime",
        description = "Mesure le temps de réponse de la suppression de personnes",
        unit = MetricUnits.MILLISECONDS
    )
    fun del(
        @RequestBody(
            required = true,
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON, schema =
                Schema(implementation = Person::class)
            )]
        ) p: Person
    ): Response {
        val person = persons.find { it.id == p.id } ?: return Response.status(404).build()
        persons = persons.minus(person)
        return Response.noContent().build()
    }


    @GET
    @Path("/{id}")
    @Operation(summary = "liste les personnes")
    @APIResponses(
        APIResponse(
            responseCode = "200", description = "listing reussi", content = [Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = Schema(implementation = Person::class)
            )]
        ),
        APIResponse(responseCode = "404", description = "Not found")
    )
    @Counted(name = "PersonsListByIDCount", description = "Compte le nombre d'appel à la liste de personnes par ID")
    @Timed(
        name = "PersonsListByIDTime",
        description = "Mesure le temps de réponse de la liste de personnes par ID",
        unit = MetricUnits.MILLISECONDS
    )
    fun getById(
        @Parameter(
            description = "retrouver une personne par id",
            example = "1",
            required = true
        )
        @PathParam("id") id: Int
    ): Response = Response.ok(persons.first { it.id == id }).build()

    @PUT
    @Path("/{id}")
    @Operation(summary = "Modifie une personne existante")
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
    @Counted(
        name = "PersonsModifyByIDCount",
        description = "Compte le nombre d'appel à la modification de personnes par ID"
    )
    @Timed(
        name = "PersonsModifyByIDTime",
        description = "Mesure le temps de réponse de la modification de personnes par ID",
        unit = MetricUnits.MILLISECONDS
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

    @Counted(
        name = "PersonsDelByIDCount",
        description = "Compte le nombre d'appel à la suppression de personnes par ID"
    )
    @Timed(
        name = "PersonsDelByIDTime",
        description = "Mesure le temps de réponse de la suppression de personnes par ID",
        unit = MetricUnits.MILLISECONDS
    )
    @DELETE
    @Path("/{id}")
    @Operation(summary = "Supprime une personne par id")
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