package ir.vira.salam.viewModel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.vira.salam.DesignPatterns.Singleton.AppPreferences
import ir.vira.salam.core.BaseViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(val sharedPref:AppPreferences) :BaseViewModel() {


    //single Event Channel collect as flow observable
    private val _hasSharedKey = Channel<Boolean>(Channel.BUFFERED)
    val hasSharedKey=_hasSharedKey.receiveAsFlow()


    private val _preventDisplay = Channel<Boolean>(Channel.BUFFERED)
    val preventDisplay=_preventDisplay.receiveAsFlow()


    init {
        viewModelScope.launch {
            _hasSharedKey.send(sharedPref.sharedKey)
        }
    }


    fun setUserHasSharedKey(){
        sharedPref.sharedKey = true
    }


    fun preventDisplayingImageViewHelp() = launch {
        _preventDisplay.send(true)
    }

}