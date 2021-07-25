package de.thb.core.di

import android.content.Context
import android.location.Geocoder
import com.google.maps.GeoApiContext
import de.thb.core.BuildConfig
import de.thb.core.manager.RouteManager
import de.thb.core.manager.RouteManagerImpl
import org.koin.dsl.module

val mapsModule = module {

    fun provideGeocoder(
        context: Context,
    ): Geocoder {
        return Geocoder(context)
    }

    single<GeoApiContext> {
        GeoApiContext.Builder()
            .apiKey(BuildConfig.MAPS_SERVICES_API_KEY)
            .build()
    }

    single { provideGeocoder(get()) }
    single<RouteManager> { RouteManagerImpl(get(), get()) }
}
