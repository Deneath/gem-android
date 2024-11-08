/**
 * Generated by typeshare 1.11.0
 */

package com.wallet.core.primitives

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class FiatBuyRequest (
	val assetId: String,
	val fiatCurrency: String,
	val fiatAmount: Double,
	val walletAddress: String
)

@Serializable
data class FiatSellRequest (
	val assetId: String,
	val fiatCurrency: String,
	val cryptoAmount: Double,
	val walletAddress: String
)

