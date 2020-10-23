package com.example.messengerapp.AdapterClasses

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.messengerapp.ModelClasses.Chat
import com.example.messengerapp.R
import com.example.messengerapp.ViewFullImageActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ChatsAdapter(
    mContext: Context,
    mChatList: List<Chat>,
    imageUrl: String
) : RecyclerView.Adapter<ChatsAdapter.ViewHolder?>(){

    private val mContext: Context
    private val mChatList: List<Chat>
    private val imageUrl: String
    var firebaseUser: FirebaseUser = FirebaseAuth.getInstance().currentUser!!

    init {
        this.mChatList = mChatList
        this.mContext = mContext
        this.imageUrl = imageUrl
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var profileImage: CircleImageView? = null
        var showTextMessage: TextView? = null
        var leftImageView: ImageView? = null
        var rightImageView: ImageView? = null
        var textSeen: TextView? = null

        init {
            profileImage = itemView.findViewById(R.id.profile_image)
            showTextMessage = itemView.findViewById(R.id.show_text_message)
            leftImageView = itemView.findViewById(R.id.left_image_view)
            rightImageView = itemView.findViewById(R.id.right_image_view)
            textSeen = itemView.findViewById(R.id.text_seen)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (mChatList[position].getSender().equals(firebaseUser!!.uid)){
            1
        }else{
            0
        }
    }

    private fun deleteSentMessage(position: Int, holder: ChatsAdapter.ViewHolder) {
        val ref = FirebaseDatabase.getInstance().reference.child("Chats")
            .child(mChatList.get(position).getMessageId()!!)
            .removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        holder.itemView.context,
                        "Message Deleted",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        holder.itemView.context,
                        "Failed, Not Deleted",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        return if (position == 1){
            val view: View = LayoutInflater.from(mContext).inflate(R.layout.message_item_right, parent, false)
            ViewHolder(view)
        }else{
            val view: View = LayoutInflater.from(mContext).inflate(R.layout.message_item_left, parent, false)
            ViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return mChatList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val chat: Chat = mChatList[position]

        Picasso.get().load(imageUrl).into(holder.profileImage)

        //images Messages
        if (chat.getMessage().equals("sent you an image") && !chat.getUrl().equals("")) {
            // image message - right side
            if (chat.getSender().equals(firebaseUser!!.uid)) {
                holder.showTextMessage!!.visibility = View.GONE
                holder.rightImageView!!.visibility = View.VISIBLE
                Picasso.get().load(chat.getUrl()).into(holder.rightImageView)

                holder.rightImageView!!.setOnClickListener {
                    val options = arrayOf<CharSequence>(
                        "View Full Image",
                        "Delete Image",
                        "Cancel"
                    )

                    var builder: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("What do you want?")

                    builder.setItems(options, DialogInterface.OnClickListener { dialog, which ->
                        if (which == 0) {
                            val intent = Intent(mContext, ViewFullImageActivity::class.java)
                            intent.putExtra("url", chat.getUrl())
                            mContext.startActivity(intent)
                        } else if (which == 1) {
                            deleteSentMessage(position, holder)
                        }
                    })
                    builder.show()
                }
            }
            // image message - left side
            else if (!chat.getSender().equals(firebaseUser!!.uid)) {
                holder.showTextMessage!!.visibility = View.GONE
                holder.leftImageView!!.visibility = View.VISIBLE
                Picasso.get().load(chat.getUrl()).into(holder.leftImageView)

                holder.leftImageView!!.setOnClickListener {
                    val options = arrayOf<CharSequence>(
                        "View Full Image",
                        "Cancel"
                    )

                    var builder: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("What do you want?")

                    builder.setItems(options, DialogInterface.OnClickListener { dialog, which ->
                        if (which == 0) {
                            val intent = Intent(mContext, ViewFullImageActivity::class.java)
                            intent.putExtra("url", chat.getUrl())
                            mContext.startActivity(intent)
                        }
                    })
                    builder.show()
                }
            }
        }
        //text messages
        else {
            holder.showTextMessage!!.text = chat.getMessage()
            Log.d("CHAT", chat.getMessage().toString())

            holder.showTextMessage!!.setOnClickListener {
                val options = arrayOf<CharSequence>(
                    "Delete",
                    "Cancel"
                )

                var builder: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                builder.setTitle("What do you want?")

                builder.setItems(options, DialogInterface.OnClickListener { dialog, which ->
                    if (which == 0) {
                        deleteSentMessage(position, holder)
                    }
                })
                builder.show()
            }
        }

        // sent and seen message
        if (position == mChatList.size - 1) {
            if (chat.isIsseen()!!) {
                holder.textSeen!!.text = "seen"
                if (chat.getMessage().equals("sent you an image") &&
                    !chat.getUrl().equals("")
                ) {
                    val lp: RelativeLayout.LayoutParams? =
                        holder.textSeen!!.layoutParams as RelativeLayout.LayoutParams
                    lp!!.setMargins(0, 245, 10, 0)
                    holder.textSeen!!.layoutParams = lp
                }
            } else {
                holder.textSeen!!.text = "sent"
                if (chat.getMessage().equals("sent you an image") &&
                    !chat.getUrl().equals("")
                ) {
                    val lp: RelativeLayout.LayoutParams? =
                        holder.textSeen!!.layoutParams as RelativeLayout.LayoutParams
                    lp!!.setMargins(0, 245, 10, 0)
                    holder.textSeen!!.layoutParams = lp
                }
            }
        } else {
            holder.textSeen!!.visibility = View.GONE
        }
    }
}