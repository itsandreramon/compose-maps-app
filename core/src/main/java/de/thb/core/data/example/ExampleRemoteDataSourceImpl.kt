package de.thb.core.data.example

import javax.inject.Inject

class ExampleRemoteDataSourceImpl @Inject constructor(
    private val exampleService: ExampleService
) : ExampleRemoteDataSource
