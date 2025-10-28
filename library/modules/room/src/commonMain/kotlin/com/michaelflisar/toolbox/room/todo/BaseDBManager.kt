package com.michaelflisar.toolbox.room.todo

import androidx.room.RoomDatabase
import com.michaelflisar.toolbox.Platform
import com.michaelflisar.toolbox.room.interfaces.IIDProvider
import com.michaelflisar.toolbox.room.interfaces.IRoomEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

@Deprecated("")
abstract class BaseDBManager<Database : RoomDatabase, ID : Number> {

    abstract fun createRoomDatabase(): Database

    protected val DB: Database by lazy {
        createRoomDatabase()
    }

    // -----------------
    // DAOs
    // -----------------

    val DAOS: List<IBaseDao<*, *>> by lazy {
        getAllDaos()
    }

    /**
     * use RoomUtil.getRegisteredDAOsViaReflection(DB) on JVM/Android to get all DAOs via reflection automatically
     * or return them manually alternatively
     */
    protected abstract fun getAllDaos(): List<IBaseDao<*, *>>

    @Suppress("UNCHECKED_CAST")
    inline fun <Dao, reified Entity> dao(): Dao where
            Dao : IDefaultDao<*, Entity>,
            Dao : IBaseFlowDao<*, Entity>,
            Dao : IBaseLoadDao<*, Entity>,
            Entity : IRoomEntity<ID, Entity> {
        return DAOS.first { it.entityClass == Entity::class } as Dao
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <Dao, reified FullEntity : IIDProvider<ID>> daoItem(): Dao where
            Dao : IBaseFullFlowDao<*, FullEntity>,
            Dao : IBaseDaoFullEntity<*, FullEntity>,
            Dao : IBaseFullLoadDao<*, FullEntity> {
        return DAOS.first { it is IBaseDaoFullEntity<*, *> && it.fullEntityClass == FullEntity::class } as Dao
    }

    // -----------------
    // functions
    // -----------------

    suspend fun clearAllTables() = withContext(Platform.DispatcherIO) {
        DAOS.forEach { (it as? IBaseDaoClear)?.clear() }
    }

    // -----------------
    // Flows
    // -----------------

    inline fun <Dao, reified Entity> flow(): Flow<List<Entity>> where
            Dao : IDefaultDao<*, Entity>,
            Entity : IRoomEntity<ID, Entity> {
        return dao<Dao, Entity>().flow()
    }

    inline fun <Dao, reified Entity> flowSingle(id: Long): Flow<Entity> where
            Dao : IDefaultDao<*, Entity>,
            Entity : IRoomEntity<ID, Entity> {
        return dao<Dao, Entity>().flowSingle(id)
    }

    inline fun <Dao, reified FullEntity> flowFull(): Flow<List<FullEntity>> where
            Dao : IBaseFullFlowDao<*, FullEntity>,
            Dao : IBaseDaoFullEntity<*, FullEntity>,
            Dao : IBaseFullLoadDao<*, FullEntity>,
            FullEntity : IIDProvider<ID> {
        return daoItem<Dao, FullEntity>().flowFull()
    }

    inline fun <Dao, reified FullEntity> flowFullSingle(id: Long): Flow<FullEntity> where
            Dao : IBaseFullFlowDao<*, FullEntity>,
            Dao : IBaseDaoFullEntity<*, FullEntity>,
            Dao : IBaseFullLoadDao<*, FullEntity>,
            FullEntity : IIDProvider<ID> {
        return daoItem<Dao, FullEntity>().flowFullSingle(id)
    }

    // -----------------
    // Load
    // -----------------

    suspend inline fun <reified Entity : IRoomEntity<ID, Entity>> loadAll() = dao<_, Entity>().loadAll()
    suspend inline fun <reified Entity : IRoomEntity<ID, Entity>> load(id: Long) =
        dao<_, Entity>().loadSingle(id)

    suspend inline fun <reified Entity : IRoomEntity<ID, Entity>> find(id: Long) =
        dao<_, Entity>().find(id)

    suspend inline fun <reified FullEntity : IIDProvider<ID>> loadAllFull() =
        daoItem<_, FullEntity>().loadAllFull()

    suspend inline fun <reified FullEntity : IIDProvider<ID>> loadFull(id: Long) =
        daoItem<_, FullEntity>().loadSingleFull(id)

    suspend inline fun <reified FullEntity : IIDProvider<ID>> findFull(id: Long) =
        daoItem<_, FullEntity>().findFull(id)

    // -----------------
    // Create/Update/Delete
    // -----------------

    suspend inline fun <reified Entity : IRoomEntity<ID, Entity>> insert(item: Entity) =
        withContext(Platform.DispatcherIO) {
            dao<_, Entity>().insert(item)
        }

    suspend inline fun <reified Entity : IRoomEntity<ID, Entity>> insert(items: List<Entity>) =
        withContext(Platform.DispatcherIO) {
            dao<_, Entity>().insert(items)
        }

    suspend inline fun <reified Entity : IRoomEntity<ID, Entity>> update(item: Entity) =
        withContext(Platform.DispatcherIO) {
            dao<_, Entity>().update(item)
        }

    suspend inline fun <reified Entity : IRoomEntity<ID, Entity>> update(items: List<Entity>) =
        withContext(Platform.DispatcherIO) {
            dao<_, Entity>().update(items)
        }

    suspend inline fun <reified Entity : IRoomEntity<ID, Entity>> delete(item: Entity) =
        withContext(Platform.DispatcherIO) {
            dao<_, Entity>().delete(item)
        }

    suspend inline fun <reified Entity : IRoomEntity<ID, Entity>> delete(items: List<Entity>) =
        withContext(Platform.DispatcherIO) {
            dao<_, Entity>().delete(items)
        }

}