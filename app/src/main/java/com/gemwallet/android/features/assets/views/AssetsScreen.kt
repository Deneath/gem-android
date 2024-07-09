package com.gemwallet.android.features.assets.views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gemwallet.android.R
import com.gemwallet.android.ext.toIdentifier
import com.gemwallet.android.features.assets.model.AssetUIState
import com.gemwallet.android.features.assets.model.WalletInfoUIState
import com.gemwallet.android.features.assets.viewmodel.AssetsViewModel
import com.gemwallet.android.features.transactions.components.transactionsList
import com.gemwallet.android.interactors.getIconUrl
import com.gemwallet.android.model.SyncState
import com.gemwallet.android.ui.components.AmountListHead
import com.gemwallet.android.ui.components.AssetHeadActions
import com.gemwallet.android.ui.components.AssetListItem
import com.gemwallet.android.ui.components.AsyncImage
import com.gemwallet.android.ui.theme.Spacer16
import com.wallet.core.primitives.AssetId
import com.wallet.core.primitives.TransactionExtended

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetsScreen(
    onShowWallets: () -> Unit,
    onShowAssetManage: () -> Unit,
    onSendClick: () -> Unit,
    onReceiveClick: () -> Unit,
    onBuyClick: () -> Unit,
    onSwapClick: () -> Unit,
    onTransactionClick: (String) -> Unit,
    onAssetClick: (AssetId) -> Unit,
    listState: LazyListState = rememberLazyListState(),
    viewModel: AssetsViewModel = hiltViewModel(),
) {
    val assets by viewModel.assets.collectAsStateWithLifecycle()
    val walletInfo by viewModel.walletInfo.collectAsStateWithLifecycle()
    val swapEnabled by viewModel.swapEnabled.collectAsStateWithLifecycle()
    val transactionsState by viewModel.txsState.collectAsStateWithLifecycle()
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { AssetsTopBar(walletInfo, onShowWallets, onShowAssetManage) }
    ) {
        val pullToRefreshState = rememberPullToRefreshState()
        PullToRefreshBox(
            modifier = Modifier.padding(top = it.calculateTopPadding()),
            isRefreshing = screenState == SyncState.InSync,
            onRefresh = viewModel::onRefresh,
            state = pullToRefreshState,
            indicator = {
                Indicator(
                    modifier = Modifier.align(Alignment.TopCenter),
                    isRefreshing = screenState == SyncState.InSync,
                    state = pullToRefreshState,
                    containerColor = MaterialTheme.colorScheme.background
                )
            }
        ) {
            val longPressedAsset = remember { mutableStateOf<AssetId?>(null) }
            LazyColumn(state = listState) {
                assetsHead(walletInfo, swapEnabled, onSendClick, onReceiveClick, onBuyClick, onSwapClick)
                pendingTransactions(transactionsState, onTransactionClick)
                assets(assets, longPressedAsset, onAssetClick, viewModel::hideAsset)
                assetsListFooter(onShowAssetManage)
            }
        }
    }
}

private fun LazyListScope.assetsHead(
    walletInfo: WalletInfoUIState,
    swapEnabled: Boolean,
    onSendClick: () -> Unit,
    onReceiveClick: () -> Unit,
    onBuyClick: () -> Unit,
    onSwapClick: () -> Unit,
) {
    item {
        AmountListHead(
            amount = walletInfo.totalValue,
            actions = {
                AssetHeadActions(
                    walletType = walletInfo.type,
                    onTransfer = onSendClick,
                    transferEnabled = true,
                    onReceive = onReceiveClick,
                    onBuy = onBuyClick,
                    onSwap = if (swapEnabled) onSwapClick else null,
                )
            }
        )
    }
}

private fun LazyListScope.assetsListFooter(
    onShowAssetManage: () -> Unit,
) {
    item {
        Box(modifier = Modifier
            .clickable(onClick = onShowAssetManage)
            .fillMaxWidth()) {
            Row(modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp)) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    tint = MaterialTheme.colorScheme.onSurface,
                    contentDescription = "asset_manager",
                )
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSize))
                Text(
                    text = stringResource(id = R.string.wallet_manage_token_list),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
        Spacer16()
    }
}

private fun LazyListScope.pendingTransactions(
    transactions: List<TransactionExtended>,
    onTransactionClick: (String) -> Unit,
) {
    transactionsList(transactions) { onTransactionClick(it) }
    if (transactions.isNotEmpty()) {
        item {
            Spacer16()
            HorizontalDivider(thickness = 0.4.dp)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.assets(
    assets: List<AssetUIState>,
    longPressState: MutableState<AssetId?>,
    onAssetClick: (AssetId) -> Unit,
    onAssetHide: (AssetId) -> Unit,
) {

    items(items = assets, key = { it.asset.id.toIdentifier() }) { item->
        var itemWidth by remember { mutableIntStateOf(0) }
        Box(
            modifier = Modifier.onSizeChanged { itemWidth = it.width }
        ) {
            AssetListItem(
                chain = item.asset.id.chain,
                title = item.asset.name,
                iconUrl = item.asset.getIconUrl(),
                value = item.value,
                assetType = item.asset.type,
                isZeroValue = item.isZeroValue,
                fiatAmount = item.fiat,
                price = item.price,
                modifier = Modifier.combinedClickable(
                    onClick = { onAssetClick(item.asset.id) },
                    onLongClick = { longPressState.value = item.asset.id },
                )
            )
            AssetItemMenu(item, longPressState.value == item.asset.id, itemWidth, onAssetHide) {
                longPressState.value = null
            }
        }
    }
}

@Composable
private fun AssetItemMenu(
    item: AssetUIState,
    showed: Boolean,
    containerWidth: Int,
    onAssetHide: (AssetId) -> Unit,
    onCancel: () -> Unit,
) {
    val clipboardManager = LocalClipboardManager.current
    DropdownMenu(
        expanded = showed,
        offset = DpOffset((with(LocalDensity.current) { containerWidth.toDp() } / 2), 8.dp),
        onDismissRequest = onCancel,
    ) {
        DropdownMenuItem(
            text = { Text( text = stringResource(id = R.string.wallet_copy_address)) },
            trailingIcon = { Icon(Icons.Default.ContentCopy, "copy") },
            onClick = {
                clipboardManager.setText(AnnotatedString(item.owner))
                onCancel()
            },
        )
        DropdownMenuItem(
            text = { Text(stringResource(id = R.string.common_hide)) },
            trailingIcon = { Icon(Icons.Default.VisibilityOff, "wallet_config") },
            onClick = {
                onAssetHide(item.asset.id)
                onCancel()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AssetsTopBar(
    walletInfo: WalletInfoUIState,
    onShowWallets: () -> Unit,
    onShowAssetManage: () -> Unit,
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        title = {
            Box {
                TextButton(onClick = onShowWallets) {
                    Row(verticalAlignment = Alignment.CenterVertically ) {
                        AsyncImage(
                            model = walletInfo.icon.ifEmpty {
                                "android.resource://com.gemwallet.android/drawable/ic_splash"
                            },
                            contentDescription = "icon",
                            placeholderText = null,
                            modifier = Modifier.size(24.dp),
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = walletInfo.name,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.titleLarge,
                        )
                        Icon(
                            imageVector = Icons.Default.ExpandMore,
                            tint = MaterialTheme.colorScheme.onSurface,
                            contentDescription = "select_wallet",
                        )
                    }
                }
            }
        },
        actions = {
            IconButton(onClick = onShowAssetManage) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    tint = MaterialTheme.colorScheme.onSurface,
                    contentDescription = "asset_manager",
                )
            }
        }
    )
}