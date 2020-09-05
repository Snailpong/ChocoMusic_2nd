package com.chocomusic.chocomusicandroid.ui.home

import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.chocomusic.chocomusicandroid.R
import com.chocomusic.chocomusicandroid.base.BaseFragment
import com.chocomusic.chocomusicandroid.ui.home.adapter.NearbyMusicianAdapter
import com.chocomusic.chocomusicandroid.ui.home.adapter.PostingAdapter
import com.chocomusic.chocomusicandroid.ui.home.adapter.PreferMusicianAdapter
import com.chocomusic.chocomusicandroid.ui.home.adapter.PromotionAdapter
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class HomeFragment: BaseFragment<HomeViewModel>() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    override val layoutSource: Int
        get() = R.layout.fragment_home

    override val viewModel: HomeViewModel by viewModel()

    /* Declaration of adapter variables */
    private lateinit var promotionAdapter: PromotionAdapter
    private lateinit var postingAdapter: PostingAdapter
    private lateinit var nearbyMusicianAdapter: NearbyMusicianAdapter
    private lateinit var preferMusicianAdapter: PreferMusicianAdapter

    private lateinit var promotionHandler: Handler
    private lateinit var updateRunnable: Runnable
    private var currentPromotionPage = 0

    override fun viewInit() {
        viewModel.getTokenInfo()

        val promotionTimer = Timer()

        /* Initialize adapters */
        promotionAdapter = PromotionAdapter(viewModel)

        /* Handler For Auto Promotion Slider */
        promotionHandler = Handler(Looper.myLooper()!!)
        updateRunnable = Runnable {
            if (currentPromotionPage == viewModel.getBannerListSize())
                currentPromotionPage = 0
            if (home_promotion_view_pager == null) {
                promotionHandler.removeCallbacks(updateRunnable)
                promotionTimer.cancel()
            } else {
                home_promotion_view_pager.setCurrentItem(currentPromotionPage++, true)
            }
        }
        promotionTimer.schedule(object : TimerTask() {
            override fun run() {
                promotionHandler.post(updateRunnable)
            }
        }, 5000L, 5000L)

        /* 프로모션 컨테이너에 dot 추가 */
        setUpIndicator()
        setUpCurrentIndicator(0)

        /* 프로모션 페이지 변경 콜백 함수 등록 및 초기화 */
        home_promotion_view_pager.apply {
            adapter = promotionAdapter
            layoutMode = ViewPager2.LAYOUT_DIRECTION_LTR
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    setUpCurrentIndicator(position)
                }
            })
        }

        /**
         *  Home Musician Part
         */
        postingAdapter = PostingAdapter(viewModel)
        nearbyMusicianAdapter = NearbyMusicianAdapter(viewModel)
        preferMusicianAdapter = PreferMusicianAdapter(viewModel)

        home_find_musician_list.apply {
            adapter = postingAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
        home_nearby_musician_list.apply {
            adapter = nearbyMusicianAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
        }
        home_prefer_musician_list.apply {
            adapter = preferMusicianAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
        }
    }

    override fun dataInit() {
        /* 토큰 가져왔으면 홈 정보 요청 */
        viewModel.userTokenInfo.observe(viewLifecycleOwner, {
            viewModel.getHomeInfo(it)
        })
        /* 홈 화면 정보 로딩 완료 */
        viewModel.homeLoadComplete.observe(viewLifecycleOwner, {
            postingAdapter.notifyDataSetChanged()
            nearbyMusicianAdapter.notifyDataSetChanged()
            preferMusicianAdapter.notifyDataSetChanged()

            home_nearby_musician_count.text = "${viewModel.getNearbyMusicianListSize()} 명"
            home_prefer_musician_count.text = "${viewModel.getPreferMusicianListSize()} 명"
        })
        /* 홈에 띄운 리스트 전부 clear */
        viewModel.homeClearComplete.observe(viewLifecycleOwner, {
            viewModel.getTokenInfo()
        })
        /* 토큰이 유효하지 않을 때 */
        viewModel.tokenInvalid.observe(viewLifecycleOwner, {
            showShortSnackBar(it)
        })
    }

    override fun finishInit() {
        /* find musician button */
        btn_home_find_musician.setOnClickListener {
            findNavController().navigate(R.id.action_mainServiceFragment_to_postingFragment)
        }
        /* search button click Listener */
        btn_home_toolbar_search.setOnClickListener {
            findNavController().navigate(R.id.action_mainServiceFragment_to_searchFragment)
        }
        /* Matching Nearby Button Click Listener */
        btn_home_nearby_musician.setOnClickListener {
            findNavController().navigate(R.id.action_mainServiceFragment_to_matchingNearbyFragment)
        }
        /* Matching Preference Button Click Listener */
        btn_home_prefer_musician.setOnClickListener {
            findNavController().navigate(R.id.action_mainServiceFragment_to_matchingPreferFragment)
        }
    }

    private fun setUpIndicator() {
        for (index in 0 until viewModel.getBannerListSize()) {
            val dotView = ImageView(requireContext()).apply {
                setImageResource(R.drawable.ic_dot)
                setPadding(5)
            }
            home_promotion_dots.addView(dotView)
        }
    }

    private fun setUpCurrentIndicator(index: Int) {
        val itemCount = home_promotion_dots.childCount
        for (item in 0 until itemCount) {
            val indicator = home_promotion_dots.getChildAt(item) as ImageView
            if (index == item) {
                indicator.setColorFilter(ContextCompat.getColor(requireContext(), R.color.main_color))
            } else {
                indicator.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray))
            }
        }
    }

}