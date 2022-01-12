package org.acme.vo

import io.quarkus.runtime.annotations.RegisterForReflection
import javax.json.bind.annotation.JsonbProperty

@RegisterForReflection
data class Person(
    @JsonbProperty("address") var address: String = "",
    @JsonbProperty("firstName") var firstName: String = "",
    @JsonbProperty("job") var job: String = "",
    @JsonbProperty("lastName") var lastName: String = "",
    @JsonbProperty("mail") var mail: String = "",
    @JsonbProperty("nationality") var nationality: String = "",
    @JsonbProperty("phone") var phone: String = "",
    @JsonbProperty("id") var id: Int = 0
)

@RegisterForReflection
data class Contract(
    @JsonbProperty("number") var number: String = "",
    @JsonbProperty("risk") var risk: String = "big risk",
    @JsonbProperty("status") var status: String = "active",
    @JsonbProperty("personId") var personId: Int = 0,
    @JsonbProperty("premiums") var premiums: List<Premium> = listOf(),
    @JsonbProperty("payments") var payments: List<Payment> = listOf()
)

@RegisterForReflection
data class Premium(
    @JsonbProperty("amount") var amount: Float = 0F,
    @JsonbProperty("dueDate") var dueDate: String = "2021-11-29 10:51:53"
)

@RegisterForReflection
data class Payment(
    @JsonbProperty("amount") var amount: Float = 0F,
    @JsonbProperty("type") var type: String = "vir",
    @JsonbProperty("date") var date: String = "2021-11-29 10:51:53"
)

@RegisterForReflection
data class Token(
    @JsonbProperty("token") var token: String = "8sTTJAcA8rnoIoyOmnzqpeIv407nlssK",
)

@RegisterForReflection
data class User(
    @JsonbProperty("firstName") var firstName: String = "Chuck",
    @JsonbProperty("login") var job: String = "cns",
    @JsonbProperty("lastName") var lastName: String = "Nourrice",
    @JsonbProperty("mail") var mail: String = "cns@nourrice.com"
)
