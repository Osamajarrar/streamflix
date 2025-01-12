package com.tanasi.streamflix.fragments.people

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tanasi.streamflix.models.People
import com.tanasi.streamflix.utils.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PeopleViewModel(id: String) : ViewModel() {

    private val _state = MutableLiveData<State>(State.Loading)
    val state: LiveData<State> = _state

    sealed class State {
        object Loading : State()
        data class SuccessLoading(val people: People) : State()
        data class FailedLoading(val error: Exception) : State()
    }

    init {
        getPeople(id)
    }


    private fun getPeople(id: String) = viewModelScope.launch(Dispatchers.IO) {
        _state.postValue(State.Loading)

        try {
            val people = UserPreferences.currentProvider!!.getPeople(id)

            _state.postValue(State.SuccessLoading(people))
        } catch (e: Exception) {
            _state.postValue(State.FailedLoading(e))
        }
    }
}