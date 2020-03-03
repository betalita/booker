package cn.deepink.booker.app

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import cn.deepink.booker.R
import cn.deepink.booker.common.STATE_END
import cn.deepink.booker.common.format
import cn.deepink.booker.common.setDrawableStart
import cn.deepink.booker.controller.BookViewModel
import cn.deepink.framework.annotation.BindLayout
import cn.deepink.framework.annotation.BindViewModel
import cn.deepink.framework.delegate.ViewDelegate
import kotlinx.android.synthetic.main.activity_book.*
import kotlinx.android.synthetic.main.item_book_search.*

@BindLayout(R.layout.activity_book)
class BookActivity : AppCompatActivity(), ViewDelegate, View.OnClickListener {

    @BindViewModel
    lateinit var controller: BookViewModel

    override fun onViewCreated() {
        controller.attach(intent) ?: return
        window.navigationBarColor = getColor(R.color.colorForeground)
        mBookName.setDrawableStart(controller.book.sourceResId)
        mBookName.text = controller.book.name
        mBookAuthor.text = controller.book.author
        mBookSummary.maxLines = Int.MAX_VALUE
        mBookSummary.text = controller.book.summary
    }

    override fun onDataObserve() {
        controller.hasSubscribed().observe(this, Observer { mBookSubscribe.setText(it) })
        controller.update().observe(this, Observer { book ->
            mBookStatus.text = if (book.state != STATE_END) book.lastUpdateTime.format() else getString(R.string.state_end)
            mBookLastChapterName.text = book.lastChapterName
            mBookStatistics.text = getString(book.statisticsResId, book.chapterTotal, book.monthlyTicket, book.recommendedTicket)
        })
    }

    override fun onClick(v: View?) {
        controller.subscribe().observe(this, Observer { mBookSubscribe.setText(it) })
    }

}