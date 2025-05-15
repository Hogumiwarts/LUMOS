package com.example.myapplication.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.ImuRequest
import com.example.myapplication.data.model.ImuResponse
import com.example.myapplication.domain.usecase.TestUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "ImuViewModel"
@HiltViewModel
class TestViewModel @Inject constructor(
    private val testUseCase: TestUseCase
): ViewModel() {

    private val _isOn = MutableStateFlow<List<String>>(emptyList())
    val isOn: StateFlow<List<String>> = _isOn

    fun add(message: String){
        _isOn.value= _isOn.value + message
    }

    fun clear() {
        _isOn.value = emptyList()
    }

    private val _testResponse = MutableLiveData<Result<ImuResponse>>()
    val testResponse: LiveData<Result<ImuResponse>>
        get() = _testResponse

    suspend fun postTest(data: ImuRequest):Result<ImuResponse>{


            val result = testUseCase.postTest(data)
            _testResponse.postValue(result)
            Log.d(TAG, "postTest: $result")
        return  result

    }
    suspend fun postPredictTest(data: ImuRequest):Result<ImuResponse>{


            val result = testUseCase.postPredictTest(data)
            _testResponse.postValue(result)
            Log.d(TAG, "postTest: $result")
        return  result

    }


}