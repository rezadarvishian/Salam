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
class MainViewModel @Inject constructor(val sharedPref:AppPreferences):BaseViewModel() {

    private val _profile = Channel<String>(Channel.BUFFERED)
    val profile=_profile.receiveAsFlow()

    private val _userName = Channel<String>(Channel.BUFFERED)
    val userName=_userName.receiveAsFlow()


    init {
        launch {
            _profile.send(sharedPref.profile)
            _userName.send(sharedPref.userName)
        }
    }


    fun saveUserImage(encodedImage:String){
        sharedPref.profile = encodedImage
    }

    fun saveUserName(name:String){
        sharedPref.userName = name
    }

    fun userProfile() = sharedPref.profile

}