package systems.maju.myapplication

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.app_item.view.*
import kotlinx.android.synthetic.main.row.view.*
import kotlinx.android.synthetic.main.row.view.iconView
import kotlinx.android.synthetic.main.row.view.nameView


class RecyclerViewAdapter(val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var data: MutableList<App> = mutableListOf()

    fun addItems(list: MutableList<App>) {
        data = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val item =
            LayoutInflater.from(context).inflate(R.layout.row, parent, false) as ViewGroup
        return RowHolder(item)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = data[position]

        when (holder.itemViewType) {
            0 -> {
                holder as RowHolder
                holder.nameView.text = item.appName
                holder.packageView.text = item.packageName
                holder.id.text = item.versionCode.toString()
                holder.version.text = item.version
                holder.iconView.setImageDrawable(context.packageManager.getApplicationIcon(item.packageName))
                holder.itemView.setOnClickListener {
                    val launchIntent: Intent? =
                        context.getPackageManager().getLaunchIntentForPackage(item.packageName)
                    context.startActivity(launchIntent)
                }
            }
        }

    }

}

open class AppHolder(itemView: ViewGroup) : RecyclerView.ViewHolder(itemView)

class RowHolder(itemView: ViewGroup) : AppHolder(itemView) {
    var iconView = itemView.iconView
    var nameView = itemView.nameView
    var packageView = itemView.packageView
    var id = itemView.idView
    var version = itemView.versionview
}