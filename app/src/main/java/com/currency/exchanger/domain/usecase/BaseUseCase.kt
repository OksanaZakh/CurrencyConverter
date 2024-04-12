package com.currency.exchanger.domain.usecase

interface BaseUseCase<T, V> {
    suspend fun execute(params: T): V
}