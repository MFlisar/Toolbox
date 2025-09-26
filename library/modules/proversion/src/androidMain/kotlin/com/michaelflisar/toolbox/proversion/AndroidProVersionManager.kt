package com.michaelflisar.toolbox.proversion

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import com.michaelflisar.composedialogs.core.DialogStateNoData
import com.michaelflisar.composedialogs.dialogs.billing.DialogBilling
import com.michaelflisar.composedialogs.dialogs.billing.DialogBillingDefaults
import com.michaelflisar.composedialogs.dialogs.info.DialogInfo
import com.michaelflisar.kotbilling.KotBilling
import com.michaelflisar.kotbilling.classes.Product
import com.michaelflisar.kotbilling.results.KBError
import com.michaelflisar.kotbilling.results.KBPurchaseQuery
import com.michaelflisar.kotpreferences.core.interfaces.StorageSetting
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.lumberjack.core.classes.Level
import com.michaelflisar.lumberjack.core.classes.priority
import com.michaelflisar.toolbox.core.resources.Res
import com.michaelflisar.toolbox.core.resources.dlg_getting_pro_error_info2
import com.michaelflisar.toolbox.core.resources.dlg_pro_version_title
import com.michaelflisar.toolbox.drawables.Crown
import com.michaelflisar.toolbox.features.proversion.BaseAppProVersionManager
import com.michaelflisar.toolbox.features.proversion.ProState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

class AndroidProVersionManager(
    scope: CoroutineScope,
    private val products: List<Product>,
    private val forceIsProInDebug: StorageSetting<Boolean>,
    val isDebug: Boolean,
    initialState: ProState = ProState.Unknown,
    val log: Boolean = false
) : BaseAppProVersionManager {

    init {
        if (log) {
            KotBilling.logger = { level, info, exception ->
                val lvl = Level.entries.find { it.priority == level } ?: Level.NONE
                if (exception != null) {
                    L.callStackCorrection(2).tag("KOTBILLING-LOG").log(lvl, exception) { info }
                } else {
                    L.callStackCorrection(2).tag("KOTBILLING-LOG").log(lvl) { info }
                }
            }
        }

        scope.launch {
            forceIsProInDebug.flow.collect {
                checkProVersion()
            }
        }
    }

    private val _proState = MutableStateFlow(initialState)
    override val proState: StateFlow<ProState> = _proState

    private val mutex = Mutex()

    override suspend fun checkProVersion(): ProState {
        val newIsPro = withContext(Dispatchers.IO) {
            L.logIf { log }?.v { "checking pro state..." }
            if (isDebug && forceIsProInDebug.read()) {
                ProState.Yes
            } else {
                try {
                    val productTypes = products.map { it.type }.distinct()
                    // queriny purchases darf nur einmal gleichzeitig passieren sonst gibt es folgenden Fehler:
                    // responseCode=5, error=Client is already in the process of connecting to billing service
                    val purchases = mutex.withLock {
                        productTypes.map {
                            KotBilling.queryPurchases(it)
                        }
                    }
                    val results = purchases.map {
                        when (it) {
                            is KBError -> false
                            is KBPurchaseQuery -> it.isAnyPurchaseOwned(*products.toTypedArray())
                        }
                    }
                    val owned = results.contains(true)
                    L.logIf { log && !owned }?.v { "Pro Version purchases: $purchases" }
                    L.logIf { log }?.v { "Pro Version: owned = $owned" }
                    if (owned) ProState.Yes else ProState.No
                } catch (e: Exception) {
                    // scheint hier irgendwo ein NPE zu geben in manchen seltenen Fällen... => ich will den Crash loggen um mehr Details zu sehen
                    L.e(e)
                    throw e
                }
            }
        }
        L.logIf { log }?.v { "checking pro state: $newIsPro" }
        _proState.update { newIsPro }
        return newIsPro
    }

    @Composable
    override fun ProVersionDialog(
        dialogState: DialogStateNoData,
        infoTextResource: StringResource
    ) {
        val proState = proState.collectAsState()
        if (proState.value == ProState.Yes) {
            DialogInfo(
                state = dialogState,
                title = { Text(stringResource(Res.string.dlg_pro_version_title)) },
                info = stringResource(Res.string.dlg_pro_version_title)
            )
        } else {
            DialogBilling(
                state = dialogState,
                title = { Text(stringResource(Res.string.dlg_pro_version_title)) },
                texts = DialogBillingDefaults.texts(
                    info = stringResource(infoTextResource),
                    textErrorQueringProducts = stringResource(Res.string.dlg_getting_pro_error_info2),
                    //textItemBought = stringResource(com.michaelflisar.swissarmy.basebaseapp.R.string.dlg_pro_version_info_bought),
                    //textItemBought = stringResource(com.michaelflisar.swissarmy.basebaseapp.R.string.dlg_pro_version_info_bought),
                    //textNoProductFound = stringResource(com.michaelflisar.swissarmy.basebaseapp.R.string.dlg_pro_version_info_bought),
                ),
                products = products.map {
                    DialogBilling.BillingProduct(it) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = rememberVectorPainter(Crown),
                            contentDescription = null
                        )
                    }
                }
            ) {
                // Pro Version nochmals prüfen
                GlobalScope.launch {
                    checkProVersion()
                }
            }
        }
    }
}