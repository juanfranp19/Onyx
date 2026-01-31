package onyx.movil.ui.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import onyx.movil.providers.UserProvider
import onyx.movil.ui.viewmodels.UserViewModel

class UserViewModelFactory(private val provider: UserProvider) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(UserViewModel::class.java))
        return UserViewModel(provider) as T
    }
}
