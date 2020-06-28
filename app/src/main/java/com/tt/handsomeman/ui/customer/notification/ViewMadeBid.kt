package com.tt.handsomeman.ui.customer.notification

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tt.handsomeman.HandymanApp
import com.tt.handsomeman.R
import com.tt.handsomeman.adapter.FileNameAdapter
import com.tt.handsomeman.databinding.ActivityViewMadeBidBinding
import com.tt.handsomeman.request.AcceptBidRequest
import com.tt.handsomeman.response.BidFileResponse
import com.tt.handsomeman.ui.BaseAppCompatActivityWithViewModel
import com.tt.handsomeman.util.CustomDividerItemDecoration
import com.tt.handsomeman.util.DecimalFormat
import com.tt.handsomeman.util.SharedPreferencesUtils
import com.tt.handsomeman.util.StatusConstant
import com.tt.handsomeman.viewmodel.NotificationViewModel
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ViewMadeBid : BaseAppCompatActivityWithViewModel<NotificationViewModel?>() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var sharedPreferencesUtils: SharedPreferencesUtils
    private lateinit var jobTitle: TextView
    private lateinit var bid: TextView
    private lateinit var letter: TextView
    private lateinit var whoBid: TextView
    private lateinit var btnAcceptBid: Button
    private lateinit var rcvFileName: RecyclerView
    private lateinit var listFileLayout: LinearLayout
    private lateinit var fileNameAdapter: FileNameAdapter
    private var bidFileResponseList: MutableList<BidFileResponse> = ArrayList()
    private var isRead = false
    private var isReadForFirstTime = false
    private var notificationPos = 0
    private var jobId = 0
    private val scope = CoroutineScope(SupervisorJob())
    private var map: HashMap<String, Long> = HashMap()
    private lateinit var binding: ActivityViewMadeBidBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewMadeBidBinding.inflate(layoutInflater)
        setContentView(binding.root)
        HandymanApp.getComponent().inject(this)
        baseViewModel = ViewModelProvider(this, viewModelFactory).get(NotificationViewModel::class.java)

        bindView()

        val intent = intent
        val handymanId = intent.getIntExtra("handymanId", 0)
        val notificationId = intent.getIntExtra("notificationId", 0)
        val jobTitle = intent.getStringExtra("jobTitle")
        val jobBidId = intent.getIntExtra("jobBidId", 0)
        val personBid = intent.getStringExtra("personBid")
        isRead = intent.getBooleanExtra("isRead", false)
        notificationPos = intent.getIntExtra("notificationPos", 0)

        this.jobTitle.text = jobTitle
        whoBid.text = getString(R.string.who_just_made_bid, personBid)
        val token = sharedPreferencesUtils.get("token", String::class.java)
        getNotificationData(notificationId, jobBidId, token)
        acceptBid(handymanId, token)
    }

    private fun markAsRead(notificationId: Int,
                           token: String) {
        if (!isRead) {
            baseViewModel!!.markNotificationAsRead(token, notificationId)
            baseViewModel!!.standardResponseMarkReadMutableLiveData.observe(this, Observer { standardResponse ->
                if (standardResponse.status == StatusConstant.OK) {
                    isRead = true
                    isReadForFirstTime = true
                }
            })
        }
    }

    private fun acceptBid(handymanId: Int,
                          token: String) {
        btnAcceptBid.setOnClickListener {
            val now = Calendar.getInstance()
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss ZZ", Locale.getDefault())
            val sendTime = formatter.format(now.time)
            baseViewModel!!.acceptBid(token, AcceptBidRequest(jobId, handymanId, sendTime))
            baseViewModel!!.standardResponseAcceptBidMutableLiveData.observe(this, Observer { standardResponse ->
                Toast.makeText(this@ViewMadeBid, standardResponse.message, Toast.LENGTH_SHORT).show()
                if ((standardResponse.status == StatusConstant.OK)) {
                    val intent = Intent()
                    intent.putExtra("isRead", isRead)
                    intent.putExtra("notificationPos", notificationPos)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            })
        }
    }

    private fun getNotificationData(notificationId: Int,
                                    jobBidId: Int,
                                    token: String) {
        baseViewModel!!.fetchMadeBidNotification(token, jobBidId)
        baseViewModel!!.madeABidNotificationResponseMutableLiveData.observe(this, Observer { madeABidNotificationResponseDataBracketResponse ->
            if (madeABidNotificationResponseDataBracketResponse.status == StatusConstant.OK) {
                val madeABidNotificationResponse = madeABidNotificationResponseDataBracketResponse.data
                bid.text = getString(R.string.money_currency_string, DecimalFormat.format(madeABidNotificationResponse.bid))
                letter.text = madeABidNotificationResponse.bidDescription
                jobId = madeABidNotificationResponse.jobId
                if (madeABidNotificationResponse.bidFileList.size > 0) {
                    listFileLayout.visibility = View.VISIBLE
                    createRecyclerView()
                    bidFileResponseList.clear()
                    bidFileResponseList.addAll(madeABidNotificationResponse.bidFileList)
                    fileNameAdapter.notifyDataSetChanged()
                }
                if (!isRead) {
                    markAsRead(notificationId, token)
                }
            } else {
                Toast.makeText(this@ViewMadeBid, madeABidNotificationResponseDataBracketResponse.message, Toast.LENGTH_SHORT).show()
                onBackPressed()
            }
        })
    }

    private fun createRecyclerView() {
        fileNameAdapter = FileNameAdapter(this, bidFileResponseList)
        fileNameAdapter.setOnItemClickListener { position ->
            val bidFileResponse = bidFileResponseList[position]

            downloadFile(bidFileResponse, position)
        }
        val layoutManagerJob: RecyclerView.LayoutManager = LinearLayoutManager(this)
        rcvFileName.layoutManager = layoutManagerJob
        rcvFileName.itemAnimator = DefaultItemAnimator()
        rcvFileName.addItemDecoration(CustomDividerItemDecoration(resources.getDrawable(R.drawable.recycler_view_divider, null)))
        rcvFileName.adapter = fileNameAdapter
    }

    private fun downloadFile(bidFileResponse: BidFileResponse,
                             position: Int) {
        scope.launch {
            val client = OkHttpClient()
            val request = Request.Builder()
                    .url(bidFileResponse.downloadLink)
                    .addHeader("Authorization", sharedPreferencesUtils.get("token", String::class.java))
                    .build()
            var response: Response? = null
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null
            try {
                withContext(Dispatchers.IO) {
                    if (isActive) {
                        response = client.newCall(request).execute()
                    }
                }
                map.put(
                        getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/${bidFileResponse.fileName}",
                        response?.body?.contentLength()!!
                )
                // if don't find file available contentLength will return -1
                if (response?.body?.contentLength()!! > 0) {
                    val file_size: Long? = response?.body?.contentLength()
                    inputStream =
                            BufferedInputStream(response?.body?.byteStream())
                    outputStream = FileOutputStream(
                            getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/${bidFileResponse.fileName}"
                    )
                    val data = ByteArray(8192)
                    var total = 0f
                    var readBytes: Int

                    // below code in java:
                    // while ( (read_bytes = inputStream.read(data)) != -1 )
                    // Wow kotlin
                    withContext(Dispatchers.IO) {
                        while (inputStream.read(data).also { readBytes = it } != -1) {
                            ensureActive()
                            total += readBytes
                            outputStream.write(data, 0, readBytes)
                            updateDownloadProgressBar(position, (total / file_size!! * 100).toInt())
                        }
                    }
                    updateDownloadProgressBar(position, 0)
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                                this@ViewMadeBid,
                                getString(R.string.file_not_available),
                                Toast.LENGTH_SHORT
                        )
                                .show()
                    }
                }
                withContext(Dispatchers.Main) {
                    fileNameAdapter.notifyItemChanged(position)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                withContext(Dispatchers.IO) {
                    response?.body?.close()
                    inputStream?.close()
                    outputStream?.flush()
                    outputStream?.close()
                }
            }
        }
    }

    private fun updateDownloadProgressBar(position: Int,
                                          progress: Int) {
        val viewHolder = rcvFileName.findViewHolderForAdapterPosition(position)
        (viewHolder as FileNameAdapter.ViewHolder).pgDownload.progress = progress
    }

    private fun bindView() {
        jobTitle = binding.jobTitle
        whoBid = binding.whoMadeBid
        bid = binding.bid
        letter = binding.bidLetter
        btnAcceptBid = binding.acceptBid
        rcvFileName = binding.recyclerFileName
        listFileLayout = binding.listFileLayout
        binding.backButton.setOnClickListener { onBackPressed() }
    }

    private fun checkIfDownloadSuccessful() {
        for ((fileName, fileLength) in map) {
            val myFile = File(fileName)
            if (myFile.exists() && myFile.length() < fileLength) {
                Log.d("file size", "$fileName has $fileLength bytes")
                Log.d("file size downloaded", myFile.length().toString() + " bytes")
                myFile.delete()
            }
        }
    }

    override fun onBackPressed() {
        scope.cancel()
        checkIfDownloadSuccessful()
        val intent = Intent()
        intent.putExtra("isRead", isRead)
        intent.putExtra("isReadForFirstTime", isReadForFirstTime)
        intent.putExtra("notificationPos", notificationPos)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}