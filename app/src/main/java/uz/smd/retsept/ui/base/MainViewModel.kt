package uz.smd.retsept.ui.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import retrofit2.HttpException
import uz.smd.retsept.data.APIInterface
import uz.smd.retsept.data.NetworkModule
import uz.smd.retsept.data.SearchResult
import java.lang.Exception

/**
 * Created by Siddikov Mukhriddin on 9/1/21
 */
class MainViewModel : ViewModel() {
    lateinit var auth: FirebaseAuth
    lateinit var  mGoogleSignInClient: GoogleSignInClient
    val errorHandle = MutableLiveData<String>()
    val isLoading = MutableLiveData<Boolean>()
    var searchId = " "
    val searchResult = MutableLiveData<List<SearchResult>?>()

    var searchFilter = ""
    var isRus = false
    var api: APIInterface = NetworkModule.utilService

    fun changeLan(isRu: Boolean = true) {
        isRus = isRu
    }

    fun search() {
        if (searchId.isNullOrEmpty())
            searchId = " "
        isLoading.value = true
        viewModelScope.launch {
            try {
                searchResult.postValue((api.search(searchId)).searchRes)
                isLoading.value = false
            } catch (e: HttpException) {
                isLoading.value = false
                errorHandle.value = e.message()
            } catch (e: Exception) {
                isLoading.value = false
                errorHandle.value = e.toString()
            }
        }
    }

}