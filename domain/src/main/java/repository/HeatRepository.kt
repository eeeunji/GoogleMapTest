package repository

import kotlinx.coroutines.flow.Flow
import model.HeatItem

interface HeatRepository {

    fun getHeatData(): Flow<List<HeatItem>>

}

