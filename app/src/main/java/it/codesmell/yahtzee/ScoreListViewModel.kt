package it.codesmell.yahtzee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.codesmell.yahtzee.dao.TableScoreDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ScoreListViewModel(
    private val dao: TableScoreDao
): ViewModel() {
    private val _sortType = MutableStateFlow(SortType.DATE)
    private val _scores = _sortType
        .flatMapLatest { sortType -> //whenever the user taps a radio button to change scores sorting, the sortType value is translated to a new Flow
            when (sortType){
                SortType.SCORE -> dao.getTablesByScore()
                SortType.DATE -> dao.getTablesByDate()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _state = MutableStateFlow(ScoreListState())

    //public version of the state that will be actually observed by the UI
    val state = combine(_state, _sortType, _scores){ state, sortType, scores -> //combines 3 flows in 1: anytime one of the Flows emits a new value, this code lines are executed
        state.copy(
            scores = scores, // updating scores in ScoreListState
            sortType = sortType // updating sortType in ScoreListState
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ScoreListState()) // 5s avoids bugs in UI

    fun onEvent(event: ScoreListEvent){ //each user event triggers this function
        when(event){
            is ScoreListEvent.deleteScore -> {
                viewModelScope.launch {
                    dao.deleteTable(event.tableScore)
                }
            }
            is ScoreListEvent.showTableScore -> TODO()
            is ScoreListEvent.sortScores -> _sortType.value = event.sortType
        }
    }
}