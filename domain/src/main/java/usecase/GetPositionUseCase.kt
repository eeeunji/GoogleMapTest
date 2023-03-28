package usecase

import kotlinx.coroutines.flow.Flow
import model.PositionItem
import repository.PositionRepository
import javax.inject.Inject

class GetPositionUseCase @Inject constructor(private val repository: PositionRepository) {
    fun execute(): Flow<List<PositionItem>> = repository.getPosition()
}