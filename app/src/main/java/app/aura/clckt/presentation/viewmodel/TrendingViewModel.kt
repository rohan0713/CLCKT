package app.aura.clckt.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.aura.clckt.data.model.PlacesItem
import app.aura.clckt.data.remote.NetworkClient
import app.aura.clckt.data.repository.TrendingRepositoryImpl
import app.aura.clckt.domain.repository.TrendingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class TrendingUiState {
    data object Loading : TrendingUiState()
    data class Success(val events: List<PlacesItem>) : TrendingUiState()
    data class Error(val message: String) : TrendingUiState()
}

class TrendingViewModel(
    private val repository: TrendingRepository = TrendingRepositoryImpl(NetworkClient.createService())
) : ViewModel() {

    private val _uiState = MutableStateFlow<TrendingUiState>(TrendingUiState.Loading)
    val uiState: StateFlow<TrendingUiState> = _uiState.asStateFlow()

    private val _selectedEvent = MutableStateFlow<PlacesItem?>(null)
    val selectedEvent: StateFlow<PlacesItem?> = _selectedEvent.asStateFlow()

    init {
        fetchTrendingEvents()
    }

    fun fetchTrendingEvents() {
        viewModelScope.launch {
            _uiState.value = TrendingUiState.Loading
            try {
                val response = repository.getTrendingEvents()
                val items = response?.places?.filterNotNull() ?: emptyList()
                _uiState.value = TrendingUiState.Success(items)
            } catch (e: Exception) {
                _uiState.value = TrendingUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun setSelectedEvent(event: PlacesItem?) {
        _selectedEvent.value = event
    }
}
