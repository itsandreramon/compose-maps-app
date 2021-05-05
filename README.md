[![Build](https://github.com/itsandreramon/mux-rulona/actions/workflows/build.yml/badge.svg)](https://github.com/itsandreramon/mux-rulona/actions/workflows/build.yml)

## Stack
- Jetpack [Compose](https://developer.android.com/jetpack/compose)
- JetBrains [Ktor](https://github.com/ktorio/ktor)
- Google [Accompanist](https://github.com/google/accompanist)
- Airbnb [Mavericks](https://github.com/airbnb/mavericks)
- Dropbox [Store](https://github.com/dropbox/Store)

## Instructions
This project uses Jetpack Compose and should be opened using Android Studio [Canary](https://developer.android.com/studio/preview).

#### Decrypt secrets
```
$ export ENCRYPT_KEY=
$ ./scripts/decrypt_secrets.sh
```

#### Add Signing Config
```
// keystore.properties
debugKeyPassword=
debugStorePassword=
```

#### Add Google Maps API key
```
// secrets.properties
MAPS_API_KEY=
```