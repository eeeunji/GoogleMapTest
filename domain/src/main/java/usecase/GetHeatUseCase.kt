package usecase

import kotlinx.coroutines.flow.Flow
import model.HeatItem
import repository.HeatRepository
import javax.inject.Inject

class GetHeatUseCase @Inject constructor(private val repository: HeatRepository) {
    fun execute(): Flow<List<HeatItem>> = repository.getHeatData()
}