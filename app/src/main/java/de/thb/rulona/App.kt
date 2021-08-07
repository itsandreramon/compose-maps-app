package de.thb.rulona

import android.app.Application
import com.airbnb.mvrx.Mavericks
import de.thb.core.di.boundariesModule
import de.thb.core.di.categoriesModule
import de.thb.core.di.coreModule
import de.thb.core.di.districtModule
import de.thb.core.di.mapsModule
import de.thb.core.di.networkModule
import de.thb.core.di.placesModule
import de.thb.core.di.rulesModule
import de.thb.rulona.di.appModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        Mavericks.initialize(applicationContext)

        startKoin {
            androidContext(this@App)

            modules(
                appModule,
                coreModule,
                mapsModule,
                rulesModule,
                placesModule,
                boundariesModule,
                districtModule,
                categoriesModule,
                networkModule,
            )
        }
    }
}
