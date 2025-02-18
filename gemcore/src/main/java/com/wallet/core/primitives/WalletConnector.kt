/**
 * Generated by typeshare 1.12.0
 */

package com.wallet.core.primitives

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
enum class SignDigestType(val string: String) {
	@SerialName("sign")
	Sign("sign"),
	@SerialName("eip191")
	Eip191("eip191"),
	@SerialName("eip712")
	Eip712("eip712"),
	@SerialName("base58")
	Base58("base58"),
}

@Serializable
data class SignMessage (
	val type: SignDigestType,
	val data: ByteArray
)

@Serializable
enum class WalletConnectionState(val string: String) {
	@SerialName("started")
	Started("started"),
	@SerialName("active")
	Active("active"),
	@SerialName("expired")
	Expired("expired"),
}

@Serializable
data class WalletConnectionSessionAppMetadata (
	val name: String,
	val description: String,
	val url: String,
	val icon: String,
	val redirectNative: String? = null,
	val redirectUniversal: String? = null
)

@Serializable
data class WalletConnectionSession (
	val id: String,
	val sessionId: String,
	val state: WalletConnectionState,
	val chains: List<Chain>,
	val createdAt: Long,
	val expireAt: Long,
	val metadata: WalletConnectionSessionAppMetadata
)

@Serializable
data class WalletConnection (
	val session: WalletConnectionSession,
	val wallet: Wallet
)

@Serializable
data class WalletConnectionSessionProposal (
	val defaultWallet: Wallet,
	val wallets: List<Wallet>,
	val metadata: WalletConnectionSessionAppMetadata
)

@Serializable
enum class WalletConnectionEvents(val string: String) {
	@SerialName("connect")
	connect("connect"),
	@SerialName("disconnect")
	disconnect("disconnect"),
	@SerialName("accountsChanged")
	accounts_changed("accountsChanged"),
	@SerialName("chainChanged")
	chain_changed("chainChanged"),
}

@Serializable
enum class WalletConnectionMethods(val string: String) {
	@SerialName("eth_chainId")
	eth_chain_id("eth_chainId"),
	@SerialName("eth_sign")
	eth_sign("eth_sign"),
	@SerialName("personal_sign")
	personal_sign("personal_sign"),
	@SerialName("eth_signTypedData")
	eth_sign_typed_data("eth_signTypedData"),
	@SerialName("eth_signTypedData_v4")
	eth_sign_typed_data_v4("eth_signTypedData_v4"),
	@SerialName("eth_signTransaction")
	eth_sign_transaction("eth_signTransaction"),
	@SerialName("eth_sendTransaction")
	eth_send_transaction("eth_sendTransaction"),
	@SerialName("eth_sendRawTransaction")
	eth_send_raw_transaction("eth_sendRawTransaction"),
	@SerialName("wallet_switchEthereumChain")
	wallet_switch_ethereum_chain("wallet_switchEthereumChain"),
	@SerialName("wallet_addEthereumChain")
	wallet_add_ethereum_chain("wallet_addEthereumChain"),
	@SerialName("solana_signMessage")
	solana_sign_message("solana_signMessage"),
	@SerialName("solana_signTransaction")
	solana_sign_transaction("solana_signTransaction"),
	@SerialName("solana_signAndSendTransaction")
	solana_sign_and_send_transaction("solana_signAndSendTransaction"),
}

