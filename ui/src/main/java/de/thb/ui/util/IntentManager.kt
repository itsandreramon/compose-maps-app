package de.thb.ui.util

import android.app.Activity
import androidx.core.app.ShareCompat

object IntentManager {
    fun createSharePlaceIntent(
        activity: Activity,
        placeId: String,
    ): ShareCompat.IntentBuilder {
        return ShareCompat.IntentBuilder(activity)
            .setType("text/plain")
            .setText("https://dev-web-frontend-rulona.ci.beilich.de/rules/$placeId")
    }
}
