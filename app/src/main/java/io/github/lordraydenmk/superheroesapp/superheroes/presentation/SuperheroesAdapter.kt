package io.github.lordraydenmk.superheroesapp.superheroes.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.jakewharton.rxbinding3.view.clicks
import io.github.lordraydenmk.superheroesapp.R
import io.github.lordraydenmk.superheroesapp.databinding.SuperheroesItemBinding
import io.github.lordraydenmk.superheroesapp.superheroes.presentation.SuperheroesAdapter.SuperheroViewHolder
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

private val diffCallback = object : DiffUtil.ItemCallback<SuperheroViewEntity>() {

    override fun areItemsTheSame(
        oldItem: SuperheroViewEntity,
        newItem: SuperheroViewEntity
    ): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: SuperheroViewEntity,
        newItem: SuperheroViewEntity
    ): Boolean = oldItem == newItem

}

class SuperheroesAdapter : ListAdapter<SuperheroViewEntity, SuperheroViewHolder>(diffCallback) {

    private val _actions = PublishSubject.create<Long>()
    val actions: Observable<Long>
        get() = _actions

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuperheroViewHolder {
        val binding =
            SuperheroesItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = SuperheroViewHolder(binding, binding.root)
        holder.clicks.map { getItem(it).id }
            .subscribe(_actions)
        return holder
    }

    override fun onBindViewHolder(holder: SuperheroViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).id
    }

    class SuperheroViewHolder(
        private val binding: SuperheroesItemBinding,
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val clicks: Observable<Int> = itemView.clicks()
            .map { adapterPosition }

        fun bind(item: SuperheroViewEntity) = with(binding) {
            ivSuperhero.load(item.imageUrl) {
                placeholder(R.drawable.ic_hourglass_bottom_black)
                error(R.drawable.ic_baseline_broken_image)
                crossfade(true)
            }
            tvSuperheroName.text = item.name
        }
    }
}