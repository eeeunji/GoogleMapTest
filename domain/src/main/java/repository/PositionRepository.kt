package repository

import kotlinx.coroutines.flow.Flow
import model.PositionItem

interface PositionRepository {

    fun getPosition(): Flow<List<PositionItem>>

}