package demo.kotilndemo

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


/**
 * Created by zhangyao on 17/5/31.
 */
class DemoAdapter(val dataVariable: Int,val presenterVariable: Int
                  , val layoutId: Int, var presenter: Presenter, val data: List<OrgBean>) : RecyclerView.Adapter<DemoAdapter.ViewHolder>() {
    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.setVariable(dataVariable, data.get(position));
        holder.binding.setVariable(presenterVariable, presenter);
        holder.binding.executePendingBindings();
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                layoutId, parent, false)
        val holder = ViewHolder(binding.getRoot())
        holder.binding = binding
        return holder
    }

    class ViewHolder(val itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var binding: ViewDataBinding
    }

}