package com.example.srbus.model.search

import android.content.Context
import android.util.Log
import com.example.srbus.data.local.favorite.FavoriteStation
import com.example.srbus.data.local.favorite.FavoriteStationDB
import com.example.srbus.data.local.favorite.FavoriteStationInBusDB
import com.example.srbus.data.local.searchStationHistory.SearchStationHistory
import com.example.srbus.data.local.searchStationHistory.SearchStationHistoryDB
import com.example.srbus.data.remote.searchStation.SearchStationItem
import com.example.srbus.data.remote.searchStation.SearchStation
import com.example.srbus.retrofit.NetRetrofit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchStationDataSource(
    private val context: Context
) : SearchStationSource {
    private val TAG = javaClass.simpleName

    private val searchStationHistoryDao = SearchStationHistoryDB
        .getInstance(context)
        .searchStationHistoryDao()

    private val favoriteStationDao = FavoriteStationDB
        .getInstance(context)
        .favoriteStationDao()

    private val favoriteStationInBusDao = FavoriteStationInBusDB
        .getInstance(context)
        .favoriteStationInBusDao()


    override fun getSearchStation(callback: SearchStationSource.LoadDataCallBack, stSrch: String) {
        GlobalScope.launch(Dispatchers.Default) {
            val res = NetRetrofit.service.getStationByNameList(NetRetrofit.getURLStationByNameList(stSrch))

            res.enqueue(object : Callback<SearchStation> {
                override fun onResponse(call: Call<SearchStation>, response: Response<SearchStation>) {
                    Log.i(TAG, "onResponse() \ncall: $call \nresponse: $response \nbody: ${response.body().toString()}")
                    callback.onLoadData(response.body())
                }

                override fun onFailure(call: Call<SearchStation>, t: Throwable) {
                    Log.e(TAG, "onFailure() \ncall: $call \nt: $t")
                }
            })
        }
    }

    override fun insertRecentSearchStation(station: SearchStationItem) {
        station.convert()?.let {
            if (searchStationHistoryDao
                    .select(it.arsId)
                    .isEmpty()
            ) {
                searchStationHistoryDao.insert(it)
            } else {
                searchStationHistoryDao.delete(it.arsId)
                searchStationHistoryDao.insert(it)
            }
        }
    }

    override fun deleteAllSearchStationHistories() {
        searchStationHistoryDao.deleteAll()
    }

    override fun deleteSearchStationHistory(arsId: String) {
        searchStationHistoryDao.delete(arsId)
    }

    override fun getSearchStationHistories(callback: SearchStationSource.LoadRecentSearchStationCallBack) {
        callback.onLoadData(searchStationHistoryDao.selectAll())
    }

    override fun getAllFavoriteStations(): List<FavoriteStation>
            = favoriteStationDao.getAllFavoriteStation()

    override fun addFavoriteStation(station: SearchStationHistory) {
        favoriteStationDao.insert(
            FavoriteStation(null, station.arsId, station.stId, station.stNm, "")
        )
    }

    override fun removeFavoriteStation(station: SearchStationHistory) {
        favoriteStationDao.delete(station.arsId)
        favoriteStationInBusDao.deleteByArsId(station.arsId)
    }
}