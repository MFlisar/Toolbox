package com.michaelflisar.toolbox.backup.classes

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

/**
 * Required metadata for newly created backups. The manager sets [createdAt] when exporting.
 */
@Immutable
@Serializable
data class BackupMetadata(
    /** App ID associated with the build that created the backup. */
    val appId: String,
    /** Version of the app data format, independent of [metadataVersion]. */
    val dataVersion: Int = 1,
    val appVersion: String? = null,
    val createdAt: String? = null,
    val custom: Map<String, String> = emptyMap(),
    val metadataVersion: Int = CURRENT_METADATA_VERSION,
) {
    init {
        require(appId.isNotBlank()) { "Backup app ID must not be blank" }
        require(dataVersion > 0) { "Backup data version must be positive" }
    }

    companion object {
        const val CURRENT_METADATA_VERSION = 1
        const val ZIP_ENTRY = "META-INF/toolbox-backup.json"
        const val MAX_ENTRY_SIZE_BYTES = 64 * 1024
    }
}

@Immutable
data class BackupInspection(
    val metadata: BackupMetadata?,
    val isValid: Boolean,
) {
    val isLegacy: Boolean
        get() = metadata == null

    val isCompatible: Boolean
        get() = isValid
}

internal fun BackupConfig.inspect(metadata: BackupMetadata?): BackupInspection {
    return BackupInspection(
        metadata = metadata,
        isValid = backupValidator(metadata)
    )
}

class BackupValidationException : IllegalStateException("Backup did not pass validation")
