package de.thb.ui.di

import com.google.maps.GeoApiContext
import de.thb.ui.BuildConfig
import org.koin.dsl.module

val mapsModule = module {
    single<GeoApiContext> {
        GeoApiContext.Builder()
            .apiKey(BuildConfig.MAPS_API_KEY)
            .build()
    }
}
