package org.acme.resources

import org.acme.sample.SampleClient
import org.acme.vo.Person
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Tag(name = "contrat")
@Path("/v1/contrat")
class ContractResource {


    /* Methods for Demo purpose only */
    @Operation(summary = "Run a demo: generate fill a demo pdf with static values")
    @APIResponse(responseCode = "200",
        content = [Content(mediaType = MediaType.APPLICATION_JSON,
            schema = Schema(implementation = Array<Person>::class ), examples = arrayOf(ExampleObject(name = "jason" , value = "SampleClient.jason")))])

    /*
      @APIResponse(responseCode = "200",
        content = [Content(mediaType = MediaType.APPLICATION_JSON,
            schema = Schema(implementation = Person::class))])
     */
    @GET
    @Path("/demo")
    @Produces(MediaType.APPLICATION_JSON)
    fun demo() = listOf(Person())
}