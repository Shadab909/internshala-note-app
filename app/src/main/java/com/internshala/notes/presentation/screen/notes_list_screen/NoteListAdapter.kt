package com.internshala.notes.presentation.screen.notes_list_screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.internshala.notes.common.Utils.getTimeAgo
import com.internshala.notes.databinding.NoteItemLayoutBinding
import com.internshala.notes.domain.model.Note

class NoteListAdapter(private val listener: NoteItemClickListener) : ListAdapter<Note,NoteListAdapter.NoteListViewHolder>(NoteListDiffCallback) {
    object NoteListDiffCallback : DiffUtil.ItemCallback<Note>(){
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }
    }

    inner class NoteListViewHolder(private val binding : NoteItemLayoutBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(note: Note){
            binding.noteItemTitle.text = note.title
            binding.noteItemContent.text = note.content
            binding.noteItemDate.text = getTimeAgo(note.timestamp)

            binding.root.setOnLongClickListener {
                listener.onNoteLongClick(note.id)
                true
            }

            binding.root.setOnClickListener {
                listener.onNoteClick(note)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = NoteItemLayoutBinding.inflate(inflater, parent, false)
        return NoteListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}