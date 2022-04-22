package org.acme.sample

import org.acme.vo.Contract
import org.acme.vo.Payment
import org.acme.vo.Person
import org.acme.vo.Premium

class SampleValues {
    companion object {

        val patrick: Person = Person(
            lastName = "B Ferguson",
            firstName = "Patrick",
            nationality = "british",
            job = "Associate ",
            address = "497 Pritchard Court",
            mail = "davonte1981@gmail.com",
            phone = "507-822-1675",
            id = 1

        )
        val janet = Person(
            lastName = "M Flippen",
            firstName = "Janet",
            phone = "516-382-1621",
            mail = "pietro1985@hotmail.com",
            address = "788 Stanley Avenue, Garden City, New York(NY), 11530",
            job = "actress",
            nationality = "USA",
            id = 2
        )

        val sarah = Person(
            lastName = "Merlino",
            firstName = "Sarah",
            phone = "609-969-8157",
            mail = "evie2004@yahoo.com",
            address = "3437 Williams Mine Road, Sayreville, New Jersey(NJ), 08872",
            job = "student",
            nationality = "USA",
            id = 3
        )

        val jason: Person = Person(
            lastName = "Statham",
            firstName = "Jason",
            nationality = "british",
            job = "actor",
            address = "Street 1",
            mail = "jason@statham.win",
            phone = "+44 555",
            id = 1

        )
        val ripley = Person(
            lastName = "Weaver",
            firstName = "Sigourney",
            phone = "secret",
            mail = "rip@l.ey",
            address = "mother ship",
            job = "actress",
            nationality = "www",
            id = 2
        )

        var premiumQ1 = Premium(
            amount = 100F,
            dueDate = "2021-04-01 00:00:00"
        )
        var premiumQ2 = Premium(
            amount = 150F,
            dueDate = "2021-08-01 00:00:00"
        )
        var paymentQ1 = Payment(
            amount = 50F,
            type = "vir",
            date = "2021-03-15 10:12:13"
        )
        var paymentQ2 = Payment(
            amount = 200F,
            type = "chq",
            date = "2021-06-15 12:12:13"
        )

        var contract1 = Contract(
            number = "c1",
            personId = 1,
            premiums = listOf(premiumQ1, premiumQ2),
            payments = listOf(paymentQ1, paymentQ2)
        )
        var contract2 = Contract(
            number = "c2",
            personId = 2,
            premiums = listOf(premiumQ1, premiumQ2),
            payments = listOf(paymentQ1)
        )
        var contract3 = Contract(
            number = "c3",
            personId = 1,
            risk = "another big risk",
            premiums = listOf(premiumQ1, premiumQ2),
            payments = listOf(paymentQ1)
        )

    }
}