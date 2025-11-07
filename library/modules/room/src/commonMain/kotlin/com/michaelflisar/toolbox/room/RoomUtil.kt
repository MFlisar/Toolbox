package com.michaelflisar.toolbox.room

import androidx.room.RoomDatabase
import androidx.room.Transactor
import androidx.room.immediateTransaction
import androidx.room.useWriterConnection


object RoomUtil {

    val DEFAULT_DB_FILE = "data.db"

    suspend fun <S, T> runQueryInChunks(list: List<S>, chunkSize: Int = 999, block: suspend (List<S>) -> List<T>): List<T> {
        val chunks = list.chunked(chunkSize)
        val result = ArrayList<T>()
        for (chunk in chunks) {
            result += block(chunk)
        }
        return result
    }

    internal suspend fun <S> runQueryInChunksAndReturnCount(list: List<S>, chunkSize: Int = 999, block: suspend (List<S>) -> Int): Int {
        var count = 0
        runQueryInChunks(list, chunkSize) {
            count += block(it)
            it
        }
        return count
    }

    /**
     * Runs the given block in a transaction (for write operations)
     *
     * as documented here: https://developer.android.com/kotlin/multiplatform/room#transactions
     */
    suspend fun <R> runInTransaction(database: RoomDatabase, block: suspend (transactor: Transactor) -> R) : R {
        return database.useWriterConnection { transactor ->
            transactor.immediateTransaction {
                // perform database operations in transaction
                block(transactor)
            }
        }
    }

    // -------------------
    // Transactor
    // -------------------

    suspend fun selectChanges(transactor: Transactor) : Int {
        return transactor.usePrepared("SELECT changes()") {
            if (it.step()) {
                it.getLong(0).toInt()
            } else {
                0
            }
        }
    }

}

suspend fun <R> RoomDatabase.withTransaction(block: suspend (transactor: Transactor) -> R): R =  RoomUtil.runInTransaction(this, block)