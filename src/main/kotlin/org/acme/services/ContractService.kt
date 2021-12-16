package org.acme.services

import org.acme.sample.SampleValues
import org.acme.vo.Contract
import org.jboss.logging.Logger
import java.util.*
import javax.inject.Singleton

@Singleton
class ContractService {
    val log: Logger = Logger.getLogger(this::class.java)

    private var contracts: Set<Contract> = Collections.synchronizedSet(LinkedHashSet())

    init {
        contracts = contracts.plusElement(SampleValues.contract1).plusElement(SampleValues.contract2)
            .plusElement(SampleValues.contract3)
    }

    fun getContracts() = contracts
    fun addContract(c: Contract) = run { contracts = contracts.plusElement(c) }
    fun delContract(c: Contract) = run { contracts = contracts.minusElement(c) }
}