package de.thb.core.hilt

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.thb.core.data.example.ExampleRemoteDataSource
import de.thb.core.data.example.ExampleRemoteDataSourceImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class DataBindings {

    @Binds
    abstract fun bindExampleRemoteDataSource(
        exampleRemoteDataSourceImpl: ExampleRemoteDataSourceImpl
    ): ExampleRemoteDataSource
}
