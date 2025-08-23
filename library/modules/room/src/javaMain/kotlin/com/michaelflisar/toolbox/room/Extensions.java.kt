package com.michaelflisar.toolbox.room

import androidx.room.RoomDatabase

fun RoomUtil.getRegisteredDAOsViaReflection(
    database: RoomDatabase,
): List<Any> {
    val daos = mutableListOf<Any>()
    for (method in database.javaClass.declaredMethods) {
        if (method.returnType.isInterface && method.parameterCount == 0) {
            try {
                val dao = method.invoke(database)
                if (dao != null && dao::class.simpleName?.endsWith("_Impl") == true) {
                    daos.add(dao)
                }
            } catch (_: Exception) {

            }
        }
    }
    return daos
}