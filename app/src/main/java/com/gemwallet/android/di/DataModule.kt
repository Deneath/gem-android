package com.gemwallet.android.di

import com.gemwallet.android.blockchain.RpcClientAdapter
import com.gemwallet.android.blockchain.clients.BroadcastClientProxy
import com.gemwallet.android.blockchain.clients.NodeStatusClient
import com.gemwallet.android.blockchain.clients.NodeStatusClientProxy
import com.gemwallet.android.blockchain.clients.SignClient
import com.gemwallet.android.blockchain.clients.SignClientProxy
import com.gemwallet.android.blockchain.clients.SignerPreload
import com.gemwallet.android.blockchain.clients.SignerPreloaderProxy
import com.gemwallet.android.blockchain.clients.aptos.AptosBroadcastClient
import com.gemwallet.android.blockchain.clients.aptos.AptosNodeStatusClient
import com.gemwallet.android.blockchain.clients.aptos.AptosSignClient
import com.gemwallet.android.blockchain.clients.aptos.AptosSignerPreloader
import com.gemwallet.android.blockchain.clients.bitcoin.BitcoinBroadcastClient
import com.gemwallet.android.blockchain.clients.bitcoin.BitcoinNodeStatusClient
import com.gemwallet.android.blockchain.clients.bitcoin.BitcoinSignClient
import com.gemwallet.android.blockchain.clients.bitcoin.BitcoinSignerPreloader
import com.gemwallet.android.blockchain.clients.cosmos.CosmosBroadcastClient
import com.gemwallet.android.blockchain.clients.cosmos.CosmosNodeStatusClient
import com.gemwallet.android.blockchain.clients.cosmos.CosmosSignClient
import com.gemwallet.android.blockchain.clients.cosmos.CosmosSignerPreloader
import com.gemwallet.android.blockchain.clients.ethereum.EvmBroadcastClient
import com.gemwallet.android.blockchain.clients.ethereum.EvmNodeStatusClient
import com.gemwallet.android.blockchain.clients.ethereum.EvmSignClient
import com.gemwallet.android.blockchain.clients.ethereum.EvmSignerPreloader
import com.gemwallet.android.blockchain.clients.near.NearBroadcastClient
import com.gemwallet.android.blockchain.clients.near.NearNodeStatusClient
import com.gemwallet.android.blockchain.clients.near.NearSignClient
import com.gemwallet.android.blockchain.clients.near.NearSignerPreloader
import com.gemwallet.android.blockchain.clients.solana.SolanaBroadcastClient
import com.gemwallet.android.blockchain.clients.solana.SolanaNodeStatusClient
import com.gemwallet.android.blockchain.clients.solana.SolanaSignClient
import com.gemwallet.android.blockchain.clients.solana.SolanaSignerPreloader
import com.gemwallet.android.blockchain.clients.sui.SuiBroadcastClient
import com.gemwallet.android.blockchain.clients.sui.SuiNodeStatusClient
import com.gemwallet.android.blockchain.clients.sui.SuiSignClient
import com.gemwallet.android.blockchain.clients.sui.SuiSignerPreloader
import com.gemwallet.android.blockchain.clients.ton.TonBroadcastClient
import com.gemwallet.android.blockchain.clients.ton.TonNodeStatusClient
import com.gemwallet.android.blockchain.clients.ton.TonSignClient
import com.gemwallet.android.blockchain.clients.ton.TonSignerPreloader
import com.gemwallet.android.blockchain.clients.tron.TronBroadcastClient
import com.gemwallet.android.blockchain.clients.tron.TronNodeStatusClient
import com.gemwallet.android.blockchain.clients.tron.TronSignClient
import com.gemwallet.android.blockchain.clients.tron.TronSignerPreloader
import com.gemwallet.android.blockchain.clients.xrp.XrpBroadcastClient
import com.gemwallet.android.blockchain.clients.xrp.XrpNodeStatusClient
import com.gemwallet.android.blockchain.clients.xrp.XrpSignClient
import com.gemwallet.android.blockchain.clients.xrp.XrpSignerPreloader
import com.gemwallet.android.cases.device.SyncSubscriptionCase
import com.gemwallet.android.data.repositoreis.assets.AssetsRepository
import com.gemwallet.android.data.repositoreis.buy.BuyRepository
import com.gemwallet.android.data.repositoreis.session.SessionRepository
import com.gemwallet.android.data.repositoreis.wallets.WalletsRepository
import com.gemwallet.android.ext.available
import com.gemwallet.android.ext.toChainType
import com.gemwallet.android.interactors.sync.SyncTransactions
import com.gemwallet.android.services.SyncService
import com.wallet.core.primitives.Chain
import com.wallet.core.primitives.ChainType
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DataModule {

    @Provides
    @Singleton
    fun providesBroadcastProxy(
        rpcClients: RpcClientAdapter,
    ): BroadcastClientProxy = BroadcastClientProxy(
        Chain.available().map {
            when (it.toChainType()) {
                ChainType.Bitcoin -> BitcoinBroadcastClient(it, rpcClients.getClient(it))
                ChainType.Ethereum -> EvmBroadcastClient(it, rpcClients.getClient(it))
                ChainType.Solana -> SolanaBroadcastClient(it, rpcClients.getClient(Chain.Solana))
                ChainType.Cosmos -> CosmosBroadcastClient(it, rpcClients.getClient(it))
                ChainType.Ton -> TonBroadcastClient(it, rpcClients.getClient(it))
                ChainType.Tron -> TronBroadcastClient(it, rpcClients.getClient(Chain.Tron))
                ChainType.Aptos -> AptosBroadcastClient(it, rpcClients.getClient(it))
                ChainType.Sui -> SuiBroadcastClient(it, rpcClients.getClient(it))
                ChainType.Xrp -> XrpBroadcastClient(it, rpcClients.getClient(it))
                ChainType.Near -> NearBroadcastClient(it, rpcClients.getClient(it))
            }
        },
    )

    @Provides
    @Singleton
    fun provideSignerPreloader(
        rpcClients: RpcClientAdapter,
    ): SignerPreload = SignerPreloaderProxy(
        Chain.available().map {
            when (it.toChainType()) {
                ChainType.Bitcoin -> BitcoinSignerPreloader(it, rpcClients.getClient(it))
                ChainType.Ethereum -> EvmSignerPreloader(it, rpcClients.getClient(it))
                ChainType.Solana -> SolanaSignerPreloader(it, rpcClients.getClient(Chain.Solana))
                ChainType.Cosmos -> CosmosSignerPreloader(it, rpcClients.getClient(it))
                ChainType.Ton -> TonSignerPreloader(it, rpcClients.getClient(it))
                ChainType.Tron -> TronSignerPreloader(it, rpcClients.getClient(Chain.Tron))
                ChainType.Aptos -> AptosSignerPreloader(it, rpcClients.getClient(it))
                ChainType.Sui -> SuiSignerPreloader(it, rpcClients.getClient(it))
                ChainType.Xrp -> XrpSignerPreloader(it, rpcClients.getClient(it))
                ChainType.Near -> NearSignerPreloader(it, rpcClients.getClient(it))
            }
        },
    )

    @Provides
    @Singleton
    fun provideSignService(
        assetsRepository: AssetsRepository,
    ): SignClientProxy = SignClientProxy(
        clients = Chain.available().map {
            when (it.toChainType()) {
                ChainType.Ethereum -> EvmSignClient(it)
                ChainType.Bitcoin -> BitcoinSignClient(it)
                ChainType.Solana -> SolanaSignClient(it, assetsRepository)
                ChainType.Cosmos -> CosmosSignClient(it)
                ChainType.Ton -> TonSignClient(it)
                ChainType.Tron -> TronSignClient(it)
                ChainType.Aptos -> AptosSignClient(it)
                ChainType.Sui -> SuiSignClient(it)
                ChainType.Xrp -> XrpSignClient(it)
                ChainType.Near -> NearSignClient(it)
            }
        },
    )

    @Singleton
    @Provides
    fun provideNodeStatusClient(
        rpcClients: RpcClientAdapter,
    ): NodeStatusClientProxy {
        return NodeStatusClientProxy(
            Chain.available().map {
                when (it.toChainType()) {
                    ChainType.Ethereum -> EvmNodeStatusClient(it, rpcClients.getClient(it))
                    ChainType.Bitcoin -> BitcoinNodeStatusClient(it, rpcClients.getClient(it))
                    ChainType.Solana -> SolanaNodeStatusClient(it, rpcClients.getClient(it))
                    ChainType.Cosmos -> CosmosNodeStatusClient(it, rpcClients.getClient(it))
                    ChainType.Ton -> TonNodeStatusClient(it, rpcClients.getClient(it))
                    ChainType.Tron -> TronNodeStatusClient(it, rpcClients.getClient(it))
                    ChainType.Aptos -> AptosNodeStatusClient(it, rpcClients.getClient(it))
                    ChainType.Sui -> SuiNodeStatusClient(it, rpcClients.getClient(it))
                    ChainType.Xrp -> XrpNodeStatusClient(it, rpcClients.getClient(it))
                    ChainType.Near -> NearNodeStatusClient(it, rpcClients.getClient(it))
                }
            }
        )
    }

    @Singleton
    @Provides
    fun provideSyncService(
        sessionRepository: SessionRepository,
        walletsRepository: WalletsRepository,
        buyRepository: BuyRepository,
        syncTransactions: SyncTransactions,
        syncSubscriptionCase: SyncSubscriptionCase,
    ): SyncService {
        return SyncService(
            sessionRepository = sessionRepository,
            walletsRepository = walletsRepository,
            syncTransactions = syncTransactions,
            buyRepository = buyRepository,
            syncSubscriptionCase = syncSubscriptionCase,
        )
    }
}