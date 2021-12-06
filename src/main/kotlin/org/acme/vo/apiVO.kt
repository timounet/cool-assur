package org.acme.vo

import io.quarkus.runtime.annotations.RegisterForReflection
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject
import javax.json.bind.annotation.JsonbProperty

@RegisterForReflection
data class Person(
    @JsonbProperty("address") var address: String = "",
    @JsonbProperty("firstName") var firstName: String = "",
    @JsonbProperty("job") var job: String = "",
    @JsonbProperty("lastName") var lastName: String = "",
    @JsonbProperty("mail") var mail: String = "",
    @JsonbProperty("nationality") var nationality: String = "",
    @JsonbProperty("phone") var phone: String = ""
)