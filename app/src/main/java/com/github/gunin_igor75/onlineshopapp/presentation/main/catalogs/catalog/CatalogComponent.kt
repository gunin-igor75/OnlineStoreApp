package com.github.gunin_igor75.onlineshopapp.presentation.main.catalogs.catalog

import com.github.gunin_igor75.onlineshopapp.domain.entity.Item
import kotlinx.coroutines.flow.StateFlow

interface CatalogComponent {

     val model: StateFlow<CatalogStore.State>
     fun sortFeedbackRating()
     fun sortPriceDesc()
     fun sortPriceAsc()
     fun choseFace()
     fun choseBody()
     fun choseSuntan()
     fun choseMask()
     fun choseAll()
     fun changeFavorite(item: Item)
     fun onItem(item: Item)
}