package com.github.gunin_igor75.onlineshopapp.presentation.main.home

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.github.gunin_igor75.onlineshopapp.presentation.main.home.HomeStore.Intent
import com.github.gunin_igor75.onlineshopapp.presentation.main.home.HomeStore.Label
import com.github.gunin_igor75.onlineshopapp.presentation.main.home.HomeStore.State

internal interface HomeStore : Store<Intent, State, Label> {

    sealed interface Intent {
    }

    data class State(val unit: Unit)

    sealed interface Label {
    }
}

internal class HomeStoreFactory(
    private val storeFactory: StoreFactory
) {

    fun create(): HomeStore =
        object : HomeStore, Store<Intent, State, Label> by storeFactory.create(
            name = "HomeStore",
            initialState = State(Unit),
            bootstrapper = BootstrapperImpl(),
            executorFactory = HomeStoreFactory::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Action {
    }

    private sealed interface Msg {
    }

    private class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
        }
    }

    private class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Msg, Label>() {
        override fun executeIntent(intent: Intent, getState: () -> State) {
        }

        override fun executeAction(action: Action, getState: () -> State) {
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(message: Msg): State =State(Unit)
    }
}
