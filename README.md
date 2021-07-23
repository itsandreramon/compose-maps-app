[![Build](https://github.com/itsandreramon/mux-rulona/actions/workflows/build.yml/badge.svg)](https://github.com/itsandreramon/mux-rulona/actions/workflows/build.yml)

## Stack

| What           | How                        |
|----------------|----------------------------|
| User Interface | [Compose](https://developer.android.com/jetpack/compose)|
| Dependency Injection | [Koin](https://github.com/InsertKoinIO/koin)|
| State Management | [Mavericks](https://github.com/airbnb/mavericks)|
| Caching | [Store](https://github.com/dropbox/Store)|
| Networking | [Retrofit](https://github.com/square/retrofit)|

## Instructions

This project uses Jetpack Compose and should be opened using Android
Studio [Arctic Fox](https://developer.android.com/studio/preview) or higher.

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
// local.properties
MAPS_API_KEY=
```
