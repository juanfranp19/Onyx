package onyx.movil.ui.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import onyx.movil.providers.GrupoProvider
import onyx.movil.ui.viewmodels.GrupoViewModel

class GrupoViewModelFactory(private val provider: GrupoProvider) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(GrupoViewModel::class.java))
        return GrupoViewModel(provider) as T
    }
}
