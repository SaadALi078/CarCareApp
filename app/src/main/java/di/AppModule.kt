package com.example.carcare.di

import android.content.Context
import com.example.carcare.viewmodels.ThemeViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideThemeViewModel(@ApplicationContext context: Context): ThemeViewModel {
        return ThemeViewModel(context)
    }
}
