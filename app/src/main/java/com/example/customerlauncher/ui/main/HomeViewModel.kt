package com.example.customerlauncher.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.customerlauncher.domain.model.IpInfo
import com.example.customerlauncher.domain.model.WeatherInfo
import com.example.customerlauncher.domain.repostority.IpinfoRepository
import com.example.customerlauncher.domain.repostority.WeatherRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(private val weatherRepository: WeatherRepository, private val ipinfoRepository: IpinfoRepository) : ViewModel(){

    private val _locationInformationStateFlow = MutableStateFlow<IpInfo?>(null)
    val locationInformationStateFlow= _locationInformationStateFlow.asStateFlow()

    private val _weatherInformationStateFlow = MutableStateFlow<WeatherInfo?>(null)
    val weatherInformationStateFlow= _weatherInformationStateFlow.asStateFlow()

    init {
        observeLocationInformation()
    }

    fun loadLocationInformation(){
        viewModelScope.launch {
            _locationInformationStateFlow.emit(ipinfoRepository.getIpInfo())
        }
    }

    fun loadWeatherInformation(latitude: Double, longitude:Double){
        viewModelScope.launch {
            _weatherInformationStateFlow.emit(weatherRepository.getWeatherInfo(latitude,longitude))
        }
    }


    private fun observeLocationInformation() {
        // location 정보가 업데이트될 때마다 날씨 정보를 로드
        locationInformationStateFlow
            .filterNotNull() // null 값을 필터링 (위치 정보가 있을 때만)
            .onEach { ipInfo ->
                // 위도와 경도 가져오기

                val loc = ipInfo.loc.split(",")
                val latitude = loc[0].toDoubleOrNull()!!
                val longitude = loc[1].toDoubleOrNull()!!
                loadWeatherInformation(latitude, longitude)
            }
            .launchIn(viewModelScope)
    }

}