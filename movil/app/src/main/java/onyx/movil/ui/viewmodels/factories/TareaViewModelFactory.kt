package onyx.movil.ui.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import onyx.movil.providers.TareaProvider
import onyx.movil.ui.viewmodels.TareaViewModel

class TareaViewModelFactory(private val provider: TareaProvider) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(TareaViewModel::class.java))
        return TareaViewModel(provider) as T
    }
}
