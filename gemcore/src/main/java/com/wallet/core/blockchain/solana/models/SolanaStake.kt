/**
 * Generated by typeshare 1.9.2
 */

@file:NoLiveLiterals

package com.wallet.core.blockchain.solana.models

import androidx.compose.runtime.NoLiveLiterals
import kotlinx.serialization.*

@Serializable
data class SolanaValidator (
	val votePubkey: String,
	val commission: Int,
	val epochVoteAccount: Boolean
)

@Serializable
data class SolanaValidators (
	val current: List<SolanaValidator>
)

@Serializable
data class SolanaEpoch (
	val epoch: Int,
	val slotIndex: Int,
	val slotsInEpoch: Int
)

