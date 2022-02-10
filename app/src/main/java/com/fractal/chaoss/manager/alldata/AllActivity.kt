package com.fractal.chaoss.manager.alldata

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.Window
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fractal.chaoss.R
import com.fractal.chaoss.manager.alldata.adapter.AllAdapter
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper


class AllActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var alllist: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            && this.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK < Configuration.SCREENLAYOUT_SIZE_LARGE
        ) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else if (this.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
        super.onCreate(savedInstanceState)
        getSupportActionBar()!!.hide();
        setContentView(R.layout.activity_all)
        ((findViewById(R.id.back)) as ImageView).setOnClickListener(this)
        alllist = findViewById(R.id.list) as RecyclerView
       /*  val viewTreeObserver: ViewTreeObserver = alllist.getViewTreeObserver()
        viewTreeObserver.addOnGlobalLayoutListener { calculateSize() }*/
        calculateSize()


    }

    private fun calculateSize() {
        alllist.setLayoutManager(GridLayoutManager(this, calculateNoOfColumns(this)))
        OverScrollDecoratorHelper.setUpOverScroll(
            alllist,
            OverScrollDecoratorHelper.ORIENTATION_VERTICAL
        )
        var adapter = AllAdapter(this@AllActivity)
        alllist.setAdapter(adapter)
      /*  val snapHelper: SnapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(alllist)*/
    }

    fun calculateNoOfColumns(context: Context): Int {
        val displayMetrics: DisplayMetrics = context.getResources().getDisplayMetrics()
        val dpWidth = displayMetrics.widthPixels / displayMetrics.density
        return (dpWidth / 180).toInt()
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.back -> {
                finish()
            }
        }
    }
}