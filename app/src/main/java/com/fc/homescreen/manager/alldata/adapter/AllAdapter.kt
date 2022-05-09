package com.fc.homescreen.manager.alldata.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.fc.homescreen.manager.detail.DetailActivity
import com.fc.homescreen.R


class AllAdapter(val context: Context ) :
    RecyclerView.Adapter<AllAdapter.VHItem>() {
    private val activity: Activity
    private var itemView: View? = null
    //var dataHeaderList = ArrayList<SeriesData.Data?>()
    /*  fun addItems(userData: SeriesData,workout_completed: ArrayList<HistoryData.CompletedWorkout?>) {
          dataHeaderList = userData.data
          this.workout_completed = workout_completed
          notifyDataSetChanged()
      }*/


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AllAdapter.VHItem {

        itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.all_list, parent, false)
        return VHItem(itemView)

    }

    @SuppressLint("WrongConstant")
    override fun onBindViewHolder(holder: VHItem, position: Int) {

        holder.img.setOnClickListener(View.OnClickListener {
            var mainIntent = Intent(context, DetailActivity::class.java)
            context.startActivity(mainIntent)
            (context as Activity).finish()
        })


        /*val currentItem: SeriesData.Data = dataHeaderList.get(position)!!

        if (currentItem.series_workout.size != 0 && currentItem.series_workout.size != null) {
            try {
                holder.title_top.text = singleCapsName(currentItem.series_name)

                holder.seriesRecyclerview.itemAnimator = SlideInUpAnimator()
                holder.seriesRecyclerview.adapter =
                    SeriesViewAdapter(
                        activity,
                        currentItem.series_workout,
                        workout_completed,
                        holder.seriesRecyclerview,
                        position,holder.title_top.text.toString()
                    )
                holder.seriesRecyclerview.layoutManager =
                    LinearLayoutManager(activity, LinearLayout.HORIZONTAL, false)
                OverScrollDecoratorHelper.setUpOverScroll(holder.seriesRecyclerview, OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL)
                holder.seriesRecyclerview.isNestedScrollingEnabled=false
                val snapHelper: SnapHelper = PagerSnapHelper()
                snapHelper.attachToRecyclerView(holder.seriesRecyclerview)
            }catch (ex:Exception){

            }

        }*/
    }


    override fun getItemCount(): Int {
        return 25
    }


    inner class VHItem(itemView: View?) :
        RecyclerView.ViewHolder(itemView!!) {
        val img: ImageView

        init {
            img = itemView!!.findViewById(R.id.img)
        }
    }

    init {
        activity = context as Activity
    }

}



