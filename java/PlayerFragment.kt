package com.chocomusic.chocomusicandroid.ui.player

import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController
import coil.api.load
import coil.transform.BlurTransformation
import coil.transform.CircleCropTransformation
import com.chocomusic.chocomusicandroid.MainActivity
import com.chocomusic.chocomusicandroid.R
import com.chocomusic.chocomusicandroid.base.BaseFragment
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import kotlinx.android.synthetic.main.fragment_player.*
import kotlinx.android.synthetic.main.v1_custom_player_controller.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerFragment: BaseFragment<PlayerViewModel>() {

    companion object {
        const val testMP3URL = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
        const val testVideoURL = "https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4"
        const val sampleAlbumURL = "https://upload.wikimedia.org/wikipedia/ko/c/c1/%ED%8A%B8%EC%99%80%EC%9D%B4%EC%8A%A4_-_What_is_Love%3F.jpeg"
    }

    override val layoutSource: Int
        get() = R.layout.fragment_player

    override val viewModel: PlayerViewModel by viewModel()

    override fun viewInit() {
        setPlayer()
        initializePlayer()
    }

    override fun dataInit() {}

    override fun finishInit() {
        /* Close Button */
        btn_content_player_exit.setOnClickListener {
            findNavController().navigate(R.id.action_contentPlayerFragment_Exit)
        }
        /* Comment Button */
        btn_content_player_controller_comment.setOnClickListener {
            findNavController().navigate(R.id.action_contentPlayerFragment_to_commentFragment)
        }
    }

    private fun initializePlayer() {
        if (MainActivity.mExoPlayer == null) {
            MainActivity.mExoPlayer = SimpleExoPlayer.Builder(requireContext()).build()

            val defaultHttpDataSourceFactory = DefaultHttpDataSourceFactory(getString(R.string.app_name))

            val httpMediaSource = ProgressiveMediaSource.Factory(defaultHttpDataSourceFactory)
                .createMediaSource(testVideoURL.toUri())

            MainActivity.mExoPlayer!!.apply {
                prepare(httpMediaSource)
                playWhenReady = true
            }
        }
        content_player.player = MainActivity.mExoPlayer
        content_player_controller.player = MainActivity.mExoPlayer

        checkAudioAndVideo(testVideoURL)
    }

    private fun setPlayer() {
        /* 플레이어를 제일 앞으로 가져옴. */
        content_player.apply {
            bringToFront()
        }
        /* 플레이어 배경화면 */
        content_player_background_img.load(R.drawable.img){
            transformations(BlurTransformation(requireContext(), 25.0f))
        }
        /* 플레이어 프로필 사진 */
        content_player_profile_img.load(R.drawable.img){
            transformations(CircleCropTransformation())
        }
        /* 플레이어 프로필 이름 */
        content_player_profile_name.text = "Izzy"
    }

    private fun checkAudioAndVideo(url: String) {
        val fileExtension = MimeTypeMap.getFileExtensionFromUrl(url)
        Log.d("MEDIA TYPE", fileExtension)
        when (fileExtension) {
            /* MUSIC */
            "mp3" -> {
                content_player.visibility = View.INVISIBLE
                content_artwork.visibility = View.VISIBLE
                content_artwork.load(sampleAlbumURL)
            }
            else -> {
                content_player.visibility = View.VISIBLE
                content_artwork.visibility = View.INVISIBLE
            }
        }
    }
}