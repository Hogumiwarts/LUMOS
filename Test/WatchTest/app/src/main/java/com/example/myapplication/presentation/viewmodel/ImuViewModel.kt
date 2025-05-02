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
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "ImuViewModel"
@HiltViewModel
class TestViewModel @Inject constructor(
    private val testUseCase: TestUseCase
): ViewModel() {



    private val _testResponse = MutableLiveData<Result<ImuResponse>>()
    val testResponse: LiveData<Result<ImuResponse>>
        get() = _testResponse

    suspend fun postTest(data: ImuRequest):Result<ImuResponse>{


            val result = testUseCase.postTest(data)
            _testResponse.postValue(result)
            Log.d(TAG, "postTest: $result")
        return  result

    }


}