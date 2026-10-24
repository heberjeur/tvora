package com.mohamedaminelouati.tvora.data.connectivity

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
    fun observe(): Flow<Boolean>
    fun isConnected(): Boolean
}
