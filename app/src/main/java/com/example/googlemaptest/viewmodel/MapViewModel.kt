package com.example.googlemaptest.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import model.PositionItem
import usecase.GetPositionUseCase
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(

    private val getAllPositionUseCase: GetPositionUseCase

) : ViewModel() {

    var livePosition = MutableLiveData<List<PositionItem>>()

    fun getPosition() {
        viewModelScope.launch {
            getAllPositionUseCase.execute().catch {
                Log.d("sband", "catch")
            }.collect {
                livePosition.value = it
            }
        }
    }
}
