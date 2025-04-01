package com.example.customerlauncher.di

import com.example.customerlauncher.common.Constants
import com.example.customerlauncher.data.remote.ipinfo.IpinfoApi
import com.example.customerlauncher.data.remote.ipinfo.IpinfoDataSource
import com.example.customerlauncher.data.remote.ipinfo.IpinfoRemoeteDataSource
import com.example.customerlauncher.data.remote.weather.WeatherAPI
import com.example.customerlauncher.data.remote.weather.WeatherDataSource
import com.example.customerlauncher.data.remote.weather.WeatherRemoteDatasource
import com.example.customerlauncher.data.repostiory.IpinfoRepostiroyImpl
import com.example.customerlauncher.data.repostiory.WeatherRepositoryImpl
import com.example.customerlauncher.domain.repostority.IpinfoRepository
import com.example.customerlauncher.domain.repostority.WeatherRepository
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val NetWorkModule = module {


    //Retrofit Module
    single<OkHttpClient>() {
        OkHttpClient.Builder()
            .addInterceptor(get<Interceptor>(named("Interceptor")))
            .build()
    }

    single<Interceptor>(named("Interceptor")) {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    //Weather API
    single<Retrofit>(named("OpenWeather")) {
        Retrofit.Builder()
            .baseUrl(Constants.OPEN_WEATHER_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .client(get())
            .build()
    }



    single<WeatherAPI> {
        get<Retrofit>(named("OpenWeather")).create(WeatherAPI::class.java)
    }

    single<WeatherRepository> { WeatherRepositoryImpl(get()) }
    single<WeatherDataSource> { WeatherRemoteDatasource(get()) }

    //IpInfo Api

    single<Retrofit>(named("Ipinfo")) {
        Retrofit.Builder()
            .baseUrl(Constants.IP_INFO_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .client(get())
            .build()
    }

    single<IpinfoApi>{
        get<Retrofit>(named("Ipinfo")).create(IpinfoApi::class.java)
    }
    single<IpinfoRepository> { IpinfoRepostiroyImpl(get()) }
    single<IpinfoDataSource> { IpinfoRemoeteDataSource(get()) }



}