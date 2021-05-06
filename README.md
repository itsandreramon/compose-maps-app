[![Build](https://github.com/itsandreramon/mux-rulona/actions/workflows/build.yml/badge.svg)](https://github.com/itsandreramon/mux-rulona/actions/workflows/build.yml)

## Stack
| What           | How                        |
|----------------|----------------------------|
| User Interface | Jetpack [Compose](https://developer.android.com/jetpack/compose)|
| Dependency Injection | Google [Hilt](https://github.com/google/dagger)|
| State Management | Airbnb [Mavericks](https://github.com/airbnb/mavericks)|
| Caching | Dropbox [Store](https://github.com/dropbox/Store)|
| Networking | Square [Retrofit](https://github.com/square/retrofit)|

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