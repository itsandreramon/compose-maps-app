package de.thb.rulona

import android.app.Application
import com.airbnb.mvrx.Mavericks
import de.thb.core.di.coreModule
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Mavericks.initialize(applicationContext)

        startKoin {
            modules(coreModule)
        }
    }
}
