package com.example.googlemaptest.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import model.HeatItem
import model.PositionItem
import usecase.GetHeatUseCase
import usecase.GetPositionUseCase
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(

    private val getAllPositionUseCase: GetPositionUseCase,
    private val getHeatUseCase: GetHeatUseCase

) : ViewModel() {

    var livePosition = MutableLiveData<List<PositionItem>>()
    var liveHeat = MutableLiveData<List<HeatItem>>()

    fun getPosition() {
        viewModelScope.launch {
            getAllPositionUseCase.execute().catch {
                Log.d("sband", "catch-getPosition")
            }.collect {
                livePosition.value = it
            }
        }
    }

    fun getHeatData() {
        viewModelScope.launch {
            getHeatUseCase.execute().catch {
                Log.d("sband", "catch-getHeatData")
            }.collect {
                liveHeat.value = it
            }
        }
    }
}
