package com.github.gunin_igor75.onlineshopapp.data.repository

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.room.Transaction
import com.github.gunin_igor75.onlineshopapp.R
import com.github.gunin_igor75.onlineshopapp.data.local.db.ItemDao
import com.github.gunin_igor75.onlineshopapp.data.local.db.UserDao
import com.github.gunin_igor75.onlineshopapp.data.local.model.UserItemDbModel
import com.github.gunin_igor75.onlineshopapp.data.mapper.toItems
import com.github.gunin_igor75.onlineshopapp.domain.entity.Item
import com.github.gunin_igor75.onlineshopapp.domain.repository.ItemRepository
import com.github.gunin_igor75.onlineshopapp.ext.getIndex
import com.github.gunin_igor75.onlineshopapp.utils.UIContentDto
import com.github.gunin_igor75.onlineshopapp.utils.readJsonFromAssets
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import javax.inject.Inject

class ItemRepositoryImpl @Inject constructor(
    private val itemDao: ItemDao,
    private val userDao: UserDao,
    private val context: Context
) : ItemRepository {

    private var _items: MutableList<Item>
    private val items
        get() = _items.toList()

    private val itemsChangeEvents = MutableSharedFlow<Unit>(replay = 1).apply {
        tryEmit(Unit)
    }

    private val sortState = mutableStateOf(SortState.RATING)

    private val currentUser = mutableStateOf(0L)

    private val itemsDefault: List<Item>


    init {
        val jsonString = readJsonFromAssets(context, MOCK_JSON)
        val fakeItems = Gson().fromJson(jsonString, UIContentDto::class.java)
        _items = mutableListOf<Item>().apply {
            addAll(fakeItems.items.toItems())
            sortedByDescending { it.feedback.rating }
        }
        itemsDefault = items
    }

    override fun getCountFavorite(userId: Long): Flow<String> =
        itemDao.getCountFavorite(userId)
            .map { transformString(it) }

    override fun getItems(userId: Long): Flow<List<Item>> = flow {
        currentUser.value = userId
        setupFavorite(_items)
        itemsChangeEvents.collect {
            emit(items)
        }
    }

    override fun getSortFeedbackDesc() {
        sortState.value = SortState.RATING
        _items.sortedByDescending { it.feedback.rating }
        itemsChangeEvents.tryEmit(Unit)
    }

    override fun getSortPriceDesc() {
        sortState.value = SortState.PRICE_DESC
        _items.sortedByDescending { it.price.priceWithDiscount }
        itemsChangeEvents.tryEmit(Unit)
    }

    override fun getSortPriceAsc() {
        sortState.value = SortState.PRICE_ASC
        _items.sortedBy { it.price.priceWithDiscount }
        itemsChangeEvents.tryEmit(Unit)
    }

    override suspend fun getChoseAll() {
        val temp = executeFilter(FilterState.ALL)
        _items = temp.toMutableList()
        executeSort()
    }

    override suspend fun getChoseFace() {
        val temp = executeFilter(FilterState.FACE)
        _items = temp.toMutableList()
        executeSort()
    }

    override suspend fun getChoseBody() {
        val temp = executeFilter(FilterState.BODY)
        _items = temp.toMutableList()
        executeSort()
    }

    override suspend fun getChoseSuntan() {
        val temp = executeFilter(FilterState.SUNTAN)
        _items = temp.toMutableList()
        executeSort()
    }

    override suspend fun getChoseMask() {
        val temp = executeFilter(FilterState.MASK)
        _items = temp.toMutableList()
        executeSort()
    }

    override fun observeIsFavorite(userId: Long, itemId: String): Flow<Boolean> =
        itemDao.observeIsFavorite(userId, itemId)


    override suspend fun saveFavoriteItem(userId: Long, itemId: String) {
        val index = _items.getIndex(itemId)
            ?: throw IllegalArgumentException("Item with id $itemId not exists")
        val userItem = UserItemDbModel(userId, itemId)
        itemDao.insertUserItem(userItem)
        _items[index] = _items[index].copy(isFavorite = true)
        itemsChangeEvents.tryEmit(Unit)
    }

    override suspend fun deleteFavoriteItem(userId: Long, itemId: String) {
        val index = _items.getIndex(itemId)
            ?: throw IllegalArgumentException("Item with id $itemId not exists")
        val userItem = UserItemDbModel(userId, itemId)
        itemDao.deleteUserItem(userItem)
        _items[index] = _items[index].copy(isFavorite = false)
        itemsChangeEvents.tryEmit(Unit)
    }

    @Transaction
    override suspend fun deleteAllInfo() {
        itemDao.deleteUserSItems()
        userDao.deleteUsers()
    }

    override fun getFavorites(userId: Long): Flow<List<Item>> = flow {
        val favorites = itemDao.getItemsIdIsFavorite(userId)
        favorites.collect{itemId ->
            val result = mutableListOf<Item>()
            _items.forEach {item ->
                if (item.id == itemId) {
                    result.add(item)
                }
            }
            emit(result)
        }
    }
    private suspend fun setupFavorite(list: MutableList<Item>) {
        val favorites = itemDao.getItemsIdIsFavorite(currentUser.value).toList()
        (0 until list.size).forEach { index ->
            if (favorites.contains(list[index].id)) {
                list[index] = list[index].copy(isFavorite = true)
            }
        }
    }

    private suspend fun executeFilter(state: FilterState): List<Item> {
        val list = itemsDefault.toMutableList()
        setupFavorite(list)
        return when (state) {
            FilterState.ALL -> {
                list.toList()
            }

            FilterState.FACE -> {
                list.filter { it.tags.contains(FACE) }
            }

            FilterState.BODY -> {
                list.filter { it.tags.contains(BODY) }
            }

            FilterState.SUNTAN -> {
                list.filter { it.tags.contains(SUNTAN) }
            }

            FilterState.MASK -> {
                list.filter { it.tags.contains(MASK) }
            }
        }
    }

    private fun executeSort() {
        when (sortState.value) {
            SortState.RATING -> {
                getSortFeedbackDesc()
            }

            SortState.PRICE_DESC -> {
                getSortPriceDesc()
            }

            SortState.PRICE_ASC -> {
                getSortPriceAsc()
            }
        }
    }

    private fun transformString(number: Int): String {
        return context.resources.getQuantityString(R.plurals.product_hint, number, number)
    }

    companion object {
        private const val MOCK_JSON = "data.json"
        private const val FACE = "face"
        private const val BODY = "body"
        private const val SUNTAN = "suntan"
        private const val MASK = "mask"
    }
}