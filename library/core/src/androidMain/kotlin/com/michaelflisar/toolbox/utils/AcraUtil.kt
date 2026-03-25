package com.michaelflisar.toolbox.utils

import android.app.Application
import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import com.michaelflisar.toolbox.Toolbox
import com.michaelflisar.toolbox.acra.AcraManager
import com.michaelflisar.toolbox.acra.AcraSetup

object AcraUtil {

    fun initAcra(
        app: Application,
        acraSetup: AcraSetup,
        fileLoggerSetup: IFileLoggingSetup?,
        buildConfigClass: Class<*>,
        isDebugBuild: Boolean,
    ) {
        AcraManager.init(
            app = app,
            fileLoggingSetup = fileLoggerSetup,
            setup = AcraManager.Setup(
                appendLogFile = true,
                mail = Toolbox.MAIL,
            ),
            acraSetup = acraSetup,
            buildConfigClass = buildConfigClass,
            isDebugBuild = isDebugBuild
        )
    }

}