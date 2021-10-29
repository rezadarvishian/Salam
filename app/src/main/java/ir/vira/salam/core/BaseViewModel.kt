package ir.vira.salam.core

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

open class BaseViewModel : ViewModel(), CoroutineScope {



    val _message  = MutableLiveData<String>()
    val message :LiveData<String> = _message

    val _loading = MutableLiveData<Boolean>()
    val loading :LiveData<Boolean> = _loading


    private val parentJob = SupervisorJob()
    private val coroutineExceptionHandler: CoroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            viewModelScope.launch {
                throwable.message?.apply {
                    _loading.postValue(false)
                    _message.postValue(this)
                }
            }
        }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + parentJob + coroutineExceptionHandler

    override fun onCleared() {
        parentJob.complete()
        super.onCleared()
    }




}