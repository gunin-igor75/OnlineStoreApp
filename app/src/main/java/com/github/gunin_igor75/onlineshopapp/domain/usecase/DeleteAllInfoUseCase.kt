package com.github.gunin_igor75.onlineshopapp.domain.usecase

import com.github.gunin_igor75.onlineshopapp.domain.repository.ItemRepository
import javax.inject.Inject

class DeleteAllInfoUseCase @Inject constructor(
    private val repository: ItemRepository
) {
    suspend operator fun invoke() = repository.deleteAllInfo()
}