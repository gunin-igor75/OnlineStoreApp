package com.github.gunin_igor75.onlineshopapp.presentation.product

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.github.gunin_igor75.onlineshopapp.domain.entity.Item
import com.github.gunin_igor75.onlineshopapp.domain.entity.User
import com.github.gunin_igor75.onlineshopapp.presentation.extentions.componentScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DefaultProductComponent @AssistedInject constructor(
    private val productStoreFactory: ProductStoreFactory,
    @Assisted("user") private val user: User,
    @Assisted("item") private val item: Item,
    @Assisted("onBackClicked") private val onBackClicked: () -> Unit,
    @Assisted("componentContext") componentContext: ComponentContext
) : ProductComponent, ComponentContext by componentContext {

    private val store = instanceKeeper.getStore { productStoreFactory.create(user, item) }

    private val componentScope = componentScope()

    @OptIn(ExperimentalCoroutinesApi::class)
    val model: StateFlow<ProductStore.State> = store.stateFlow

    init {
        componentScope.launch {
            store.labels.collect {
                when (it) {
                    ProductStore.Label.ClickBack -> {
                        onBackClicked()
                    }
                }
            }
        }
    }

    override fun onClickBack() {
        store.accept(ProductStore.Intent.ClickBack)
    }

    override fun onClickChangeFavorite() {
        store.accept(ProductStore.Intent.ClickChangeFavorite)
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("user") user: User,
            @Assisted("item") item: Item,
            @Assisted("onBackClicked") onBackClicked: () -> Unit,
            @Assisted("componentContext") componentContext: ComponentContext
        ): DefaultProductComponent
    }
}