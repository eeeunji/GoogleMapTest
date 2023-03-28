package repository

import android.content.Context
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import model.PositionItem

class PositionRepositoryImpl(
    private val context: Context

) : PositionRepository {

    override fun getPosition(): Flow<List<PositionItem>> {
        val inputStream = context.assets.open("position.json")
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        val jsonString = String(buffer, Charsets.UTF_8)
        val positionItems = Gson().fromJson(jsonString, Array<PositionItem>::class.java)
        return flow {
            emit(positionItems.toList())
        }
    }

}