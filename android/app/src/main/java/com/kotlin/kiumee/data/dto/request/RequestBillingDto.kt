package com.kotlin.kiumee.data.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestBillingDto(
    @SerialName("items")
    val items: List<RequestBillingItemsDto>
)

@Serializable
data class RequestBillingItemsDto(
    @SerialName("id")
    val id: Int,
    @SerialName("quantity")
    val quantity: Int
)
