package dev.muthuram.previewer.di

import dev.muthuram.previewer.ui.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object AppModule {

    private val viewModelModules = module {
        viewModel { MainViewModel() }
    }

    private val repoModules = module {

    }

    private val commonModules = module {

    }

    fun appModules() = viewModelModules + repoModules + commonModules
}