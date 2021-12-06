package org.acme.sample

import org.acme.vo.Person
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject

class SampleClient{
companion object {

    val jason : Person = Person(
        lastName = "Statham",
        firstName = "Jason",
        nationality = "british",
        job = "actor",
        address = "Street 1",
        mail = "jason@statham.win",
        phone = "+44 555",

    )
    val ripley = Person(
        lastName = "Weaver",
        firstName = "Sigourney",
        phone = "secret",
        mail = "rip@l.ey",
        address = "mother ship",
        job = "actress",
        nationality = "www"
    )
    val persons = arrayOf(jason, ripley)
}
}