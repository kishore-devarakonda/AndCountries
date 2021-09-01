package com.example.countries.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.countries.Country
import com.example.countries.model.CountriesService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class ListViewModel : ViewModel() {

    private val countriesService = CountriesService()
    /* sense viewmodel is using rxjava to get the information from the service and when the viewmodel is closed we need to close
    or clear that connection */
    private val disposable = CompositeDisposable()

    val countries = MutableLiveData<List<Country>>()
    val countryLoadingError = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()

    fun refresh() {
        fetchCountries()
    }

    private fun fetchCountries() {
        loading.value = true
        disposable.add(
            countriesService.getCountries()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<List<Country>>() {
                    override fun onSuccess(value: List<Country>?) {
                        countries.value = value
                        countryLoadingError.value = false
                        loading.value = false
                    }

                    override fun onError(e: Throwable?) {
                        countryLoadingError.value = true
                        loading.value = false
                    }
                })
        )

    /* val mockData: List<Country> = listOf(
             Country("Country A"),
             Country("Country B"),
             Country("Country C"),
             Country("Country D"),
             Country("Country E"),
             Country("Country F"),
             Country("Country G"),
             Country("Country H"),
             Country("Country I"),
             Country("Country J"),
             Country("Country K")
         )

         countryLoadingError.value = false
         loading.value = false
         countries.value = mockData*/
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}
