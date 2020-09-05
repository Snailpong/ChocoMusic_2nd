package com.chocomusic.chocomusicandroid.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import com.chocomusic.chocomusicandroid.base.BaseViewModel
import com.chocomusic.chocomusicandroid.data.CoreRepository
import com.chocomusic.chocomusicandroid.data.UserRepository
import com.chocomusic.chocomusicandroid.data.dto.musician.MusicianDTO
import com.chocomusic.chocomusicandroid.data.dto.posting.PostingDTO
import com.chocomusic.chocomusicandroid.utils.SingleLiveEvent

class HomeViewModel(
    private val coreRepository: CoreRepository,
    private val userRepository: UserRepository,
): BaseViewModel() {

    companion object {
        private const val TAG = "HomeViewModel"
    }

    private val imgResourceList = listOf(
        "https://upload.wikimedia.org/wikipedia/ko/c/c1/%ED%8A%B8%EC%99%80%EC%9D%B4%EC%8A%A4_-_What_is_Love%3F.jpeg",
        "https://az879543.vo.msecnd.net/twice/TWICE-MORE&MORE-COVER-300x300.jpg",
        "https://i.pinimg.com/originals/dd/dc/72/dddc72fb831ca64f55c463681ece5dcd.png"
    )

    fun getBannerListSize() = imgResourceList.size
    fun getBannerListItem(position: Int) = imgResourceList[position]

    /**
     * Home Musician Posting Part
     */
    private val _userTokenInfo: SingleLiveEvent<String> = SingleLiveEvent()
    val userTokenInfo: LiveData<String> get() = _userTokenInfo

    private val recentPostList = ArrayList<PostingDTO>()
    fun getRecentPostListSize() = recentPostList.size
    fun getRecentPostListItem(position: Int) = recentPostList[position]

    private val nearbyMusicianList = ArrayList<MusicianDTO>()
    fun getNearbyMusicianListSize() = nearbyMusicianList.size
    fun getNearbyMusicianListItem(position: Int) = nearbyMusicianList[position]

    private val preferMusicianList = ArrayList<MusicianDTO>()
    fun getPreferMusicianListSize() = preferMusicianList.size
    fun getPreferMusicianListItem(position: Int) = preferMusicianList[position]

    private val _homeLoadComplete: SingleLiveEvent<Any> = SingleLiveEvent()
    val homeLoadComplete: LiveData<Any> get() = _homeLoadComplete

    private val _homeClearComplete: SingleLiveEvent<Any> = SingleLiveEvent()
    val homeClearComplete: LiveData<Any> get() = _homeClearComplete

    private val _tokenInvalid: SingleLiveEvent<String> = SingleLiveEvent()
    val tokenInvalid: LiveData<String> get() = _tokenInvalid

    fun getTokenInfo() {
        apiCall(single = userRepository.getLocalUserInfo(),
        onSuccess = {
            _userTokenInfo.postValue(it.accessToken)
            Log.i(TAG, "UserToken Complete $it")
        },
        onError = {
            callNetworkInvalid()
            Log.e(TAG, "UserToken Error", it)
        })
    }

    fun getHomeInfo(token: String) {
        apiCall(single = coreRepository.getHomeInfo(token),
        onSuccess = {
            recentPostList.addAll(it.recentPostingList)
            nearbyMusicianList.addAll(it.nearbyMusicianList)
            preferMusicianList.addAll(it.preferMusicianList)
            _homeLoadComplete.call()
        },
        onError = {
            if (it.message == "HTTP 401 Unauthorized")
                _tokenInvalid.postValue("유효하지 않은 유저입니다.")
            else
                callNetworkInvalid()
            Log.e(TAG, "Home Info Failed", it)
        }, indicator = true)
    }
}
