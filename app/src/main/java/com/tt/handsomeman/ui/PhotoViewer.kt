package com.tt.handsomeman.ui

import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.signature.MediaStoreSignature
import com.tt.handsomeman.R
import com.tt.handsomeman.databinding.ActivityPhotoViewerBinding

class PhotoViewer : BaseAppCompatActivity() {

    private lateinit var binding: ActivityPhotoViewerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_Night)
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        val authorizationCode = intent.getStringExtra("authorizationCode")
        val avatarLink = intent.getStringExtra("avatarLink")
        val updateDate = intent.getLongExtra("updateDate", 0)

        val glideUrl = GlideUrl(avatarLink,
                LazyHeaders.Builder().addHeader("Authorization", authorizationCode!!).build())

        Glide.with(this)
                .load(glideUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.custom_progressbar)
                .error(R.drawable.logo)
                .signature(MediaStoreSignature("", updateDate, 0))
                .into(binding.imageView)

        binding.close.setOnClickListener { onBackPressed() }
    }

}
