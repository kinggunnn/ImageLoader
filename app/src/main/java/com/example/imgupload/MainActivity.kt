package com.example.imgupload
//메인 앱의 도입 부분

import ImageAdapter
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil3.ImageLoader
import coil3.load
import coil3.request.CachePolicy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import org.json.JSONObject
import org.jsoup.Jsoup
import java.io.IOException

class MainActivity : AppCompatActivity() {
    //뷰설정
    private lateinit var recyclerView: RecyclerView
    private lateinit var uploadButton: Button
    private lateinit var adapter: ImageAdapter
    private val client = OkHttpClient()  // OkHttpClient  생성

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //메모리 활성화용 imageLoader
        val imageLoader = ImageLoader.Builder(this).memoryCachePolicy(CachePolicy.ENABLED).diskCachePolicy(CachePolicy.ENABLED).build()

        recyclerView = findViewById(R.id.listView)
        uploadButton = findViewById(R.id.uploadButton)

        //이미지어답터따로구성
        adapter = ImageAdapter(imageLoader)




        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = adapter

        uploadButton.setOnClickListener {
            loadImages()
            recyclerView.layoutManager = LinearLayoutManager(this)

        }
    }

    //이미지 로드함수, 잘 가져오는지 로그캣 확인 가능
    private fun loadImages() {
        lifecycleScope.launch {
            try {
                val imageUrls = withContext(Dispatchers.IO) {
                    fetchCategoryImages()  // API 방식으로 가져옴
                }

                Log.d("DEBUG_TAG", "num of URL1 : ${imageUrls.size}")
                //imageUrls.forEach { Log.d("DEBUG_TAG", "image URL: $it") }
                //이미지 url직접 확인가능

                adapter.submitList(imageUrls)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("DEBUG_TAG", "ERROR", e)
            }
        }
    }

    //api 요청으로 카테고리 이미지 가져오는 함수
    //홈페이지 개발자 모드 확인 후 Fetch/XHR 확인해서 jsonData 가져오기
    private fun fetchCategoryImages(): List<String> {
        val url =
            "https://sch.sooplive.co.kr/api.php?m=categoryList&szKeyword=&szOrder=view_cnt&nPageNo=1&nListCnt=120&nOffset=0&szPlatform=pc"
        //봇차단인거같아서 user-agent로 넣기
        val request = Request.Builder()
            .url(url)
            .header(
                "User-Agent",
                "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Mobile Safari/537.36"
            )
            .header("Accept", "application/json, text/plain, */*")
            .header("Referer", "https://www.sooplive.co.kr/")
            .build()

        val response = client.newCall(request).execute()  // 동기 실행
        val jsonData = response.body?.string() ?: return emptyList()

        return extractImageUrls(jsonData)
    }

    //응답받은 json데이터를 이미지 추출하는 함수
    private fun extractImageUrls(jsonData: String): List<String> {
        val imageUrls = mutableListOf<String>()
        //json 확인 120개 데이터 중 list내에 cate_img 항목만 가져와서 url 저장
        try {
            val jsonObject = JSONObject(jsonData)
            val data = jsonObject.getJSONObject("data")
            val categoryList = data.getJSONArray("list")

            for (i in 0 until categoryList.length()) {
                val category = categoryList.getJSONObject(i)
                val imageUrl = category.getString("cate_img")
                imageUrls.add(imageUrl)
            }
        } catch (e: Exception) {
            Log.e("DEBUG_TAG", " JSON parsing error: ${e.message}")
        }

        return imageUrls
    }
}
