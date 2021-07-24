package de.thb.core.data.sources.rules.local

import de.thb.core.domain.rule.RuleEntity
import de.thb.core.domain.rule.RuleWithCategoryEntity
import de.thb.core.util.CoroutinesDispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RulesLocalDataSourceImpl(
    private val applicationScope: CoroutineScope,
    private val rulesRoomDao: RulesRoomDao,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
) : RulesLocalDataSource {

    override fun getByPlaceId(placeId: String): Flow<List<RuleWithCategoryEntity>> {
        return rulesRoomDao.getByPlaceId(placeId)
            .flowOn(dispatcherProvider.database())
    }

    override suspend fun insert(rule: RuleEntity) {
        withContext(dispatcherProvider.database()) {
            applicationScope.launch {
                rulesRoomDao.insert(rule)
            }.join()
        }
    }

    override suspend fun insert(rules: List<RuleEntity>) {
        withContext(dispatcherProvider.database()) {
            applicationScope.launch {
                rulesRoomDao.insert(rules)
            }.join()
        }
    }
}
