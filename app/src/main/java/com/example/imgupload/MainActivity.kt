package com.example.imgupload


import ImageAdapter
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import org.json.JSONObject
import org.jsoup.Jsoup
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var uploadButton: Button
    private lateinit var adapter: ImageAdapter
    private val client = OkHttpClient()  // ✅ OkHttpClient 객체 생성

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.listView)
        uploadButton = findViewById(R.id.uploadButton)
        adapter = ImageAdapter()

        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = adapter

        uploadButton.setOnClickListener {
            loadImages()
            recyclerView.layoutManager = LinearLayoutManager(this)

        }
    }

    private fun loadImages() {
        lifecycleScope.launch {
            try {
                val imageUrls = withContext(Dispatchers.IO) {
                    fetchCategoryImages()  // ✅ API 방식으로 가져옴
                }

                Log.d("DEBUG_TAG", "num of URL1 : ${imageUrls.size}")
                //imageUrls.forEach { Log.d("DEBUG_TAG", "image URL: $it") }

                adapter.submitList(imageUrls)  // ✅ 기존 어댑터 유지
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("DEBUG_TAG", "ERROR!!!!", e)
            }
        }
    }

    private fun fetchCategoryImages(): List<String> {
        val url =
            "https://sch.sooplive.co.kr/api.php?m=categoryList&szKeyword=&szOrder=view_cnt&nPageNo=1&nListCnt=120&nOffset=0&szPlatform=pc"

        val request = Request.Builder()
            .url(url)
            .header(
                "User-Agent",
                "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Mobile Safari/537.36"
            )
            .header("Accept", "application/json, text/plain, */*")
            .header("Referer", "https://www.sooplive.co.kr/")
            .build()

        val response = client.newCall(request).execute()  // ✅ 동기 실행 (비동기 X)
        val jsonData = response.body?.string() ?: return emptyList()

        return extractImageUrls(jsonData)
    }

    private fun extractImageUrls(jsonData: String): List<String> {
        val imageUrls = mutableListOf<String>()

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
            Log.e("DEBUG_TAG", "🚨 JSON 파싱 오류: ${e.message}")
        }

        return imageUrls
    }
}
