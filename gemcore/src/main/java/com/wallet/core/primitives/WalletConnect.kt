/**
 * Generated by typeshare 1.12.0
 */

package com.wallet.core.primitives

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class WCEthereumTransaction (
	val chainId: String? = null,
	val from: String,
	val to: String,
	val value: String? = null,
	val gas: String? = null,
	val gasLimit: String? = null,
	val gasPrice: String? = null,
	val maxFeePerGas: String? = null,
	val maxPriorityFeePerGas: String? = null,
	val nonce: String? = null,
	val data: String? = null
)

@Serializable
data class WCSolanaSignMessage (
	val message: String,
	val pubkey: String
)

@Serializable
data class WCSolanaSignMessageResult (
	val signature: String
)

@Serializable
data class WCSolanaTransaction (
	val transaction: String
)

@Serializable
enum class WallletConnectCAIP2(val string: String) {
	@SerialName("eip155")
	Eip155("eip155"),
	@SerialName("solana")
	Solana("solana"),
	@SerialName("cosmos")
	Cosmos("cosmos"),
	@SerialName("algorand")
	Algorand("algorand"),
}

