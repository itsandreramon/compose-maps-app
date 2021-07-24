package de.thb.core.di

import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val networkModule = module {

    fun provideRetrofit(): Retrofit.Builder {
        return Retrofit.Builder()
            .baseUrl("https://dev-backend-rulona.ci.beilich.de")
            .addConverterFactory(MoshiConverterFactory.create())
    }

    single { provideRetrofit() }
}
