package com.michaelflisar.toolbox.room

object RoomUtil {

    suspend fun <S, T> runQueryInChuncks(list: List<S>, chunkSize: Int = 999, block: suspend (List<S>) -> List<T>): List<T> {
        val chunks = list.chunked(chunkSize)
        val result = ArrayList<T>()
        for (chunk in chunks) {
            result += block(chunk)
        }
        return result
    }

}