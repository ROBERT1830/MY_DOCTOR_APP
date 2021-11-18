package com.robertconstantindinescu.my_doctor_app.models

import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class Repository @Inject constructor(
    localDataSource: LocalDataSource,
    remoteDataSource: RemoteDataSource
) {
    val local = localDataSource
    val remote = remoteDataSource
}