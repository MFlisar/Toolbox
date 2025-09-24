package com.michaelflisar.toolbox.room

import androidx.room.RoomDatabase
import androidx.room.immediateTransaction
import androidx.room.useWriterConnection

object RoomUtil {

    suspend fun <S, T> runQueryInChunks(list: List<S>, chunkSize: Int = 999, block: suspend (List<S>) -> List<T>): List<T> {
        val chunks = list.chunked(chunkSize)
        val result = ArrayList<T>()
        for (chunk in chunks) {
            result += block(chunk)
        }
        return result
    }

    /**
     * Runs the given block in a transaction (for write operations)
     *
     * as documented here: https://developer.android.com/kotlin/multiplatform/room#transactions
     */
    suspend fun runInTransaction(database: RoomDatabase, block: suspend () -> Unit) {
        database.useWriterConnection { transactor ->
            transactor.immediateTransaction {
                // perform database operations in transaction
                block()
            }
        }
    }

}