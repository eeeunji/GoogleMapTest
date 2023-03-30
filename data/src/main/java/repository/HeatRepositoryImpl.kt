package repository

import android.content.Context
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import model.HeatItem

class HeatRepositoryImpl(
    private val context: Context

) : HeatRepository {

    override fun getHeatData(): Flow<List<HeatItem>> {
        val inputStream = context.assets.open("heat.json")
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        val jsonString = String(buffer, Charsets.UTF_8)
        val heatItems = Gson().fromJson(jsonString, Array<HeatItem>::class.java)
        return flow {
            emit(heatItems.toList())
        }
    }

}