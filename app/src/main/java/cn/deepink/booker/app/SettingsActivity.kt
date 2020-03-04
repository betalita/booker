package cn.deepink.booker.app

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import cn.deepink.booker.R
import cn.deepink.booker.common.ListAdapter
import cn.deepink.booker.common.setDrawableStart
import cn.deepink.booker.http.SOURCE
import cn.deepink.framework.annotation.BindLayout
import cn.deepink.framework.delegate.ViewDelegate
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.item_web.view.*

@BindLayout(R.layout.activity_settings)
class SettingsActivity : AppCompatActivity(), ViewDelegate {

    override fun onViewCreated() {
        mSettingsWebRecycler.adapter = buildAdapter().apply { submitList(SOURCE.values().toList()) }
    }

    override fun onDataObserve() {
    }

    private fun buildAdapter() = ListAdapter<SOURCE>(R.layout.item_web) { item, source ->
        item.itemView.mWebName.setDrawableStart(source.icon)
        item.itemView.mWebName.text = source.title
        item.itemView.mWebUrl.text = Uri.parse(source.url).host
        item.itemView.setOnClickListener { openUrl(source.url) }
    }

    private fun openUrl(url: String) = try {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    } catch (e: Exception) {
        //未安装浏览器应用
    }

}