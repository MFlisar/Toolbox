package com.michaelflisar.toolbox.room

class SQLLiteVersion(
    private val major: Int,
    private val minor: Int,
    private val patch: Int,
) {

    companion object {

        private const val TEST_NOTHING_IS_SUPPORTED = false

        val MIN_VERSION_WITH_CTE = create("3.8.3")
        val MIN_VERSION_WITH_ROW_NUMBER = create("3.25.0")

        suspend fun load(dao: SQLLiteVersionDao): SQLLiteVersion {
            val version = dao.getSQLiteVersion()
            return create(version)
        }

        fun create(version: String): SQLLiteVersion {
            val versionParts = version.split(".").map { it.toInt() }
            return SQLLiteVersion(versionParts[0], versionParts[1], versionParts[2])
        }
    }

    fun supportsWithCTE(): Boolean {
        return !TEST_NOTHING_IS_SUPPORTED && this >= MIN_VERSION_WITH_CTE
    }

    fun supportsRowNumber(): Boolean {
        return !TEST_NOTHING_IS_SUPPORTED && this >= MIN_VERSION_WITH_ROW_NUMBER
    }

    operator fun compareTo(other: SQLLiteVersion): Int {
        return when {
            this.major != other.major -> this.major - other.major
            this.minor != other.minor -> this.minor - other.minor
            else -> this.patch - other.patch
        }
    }
}