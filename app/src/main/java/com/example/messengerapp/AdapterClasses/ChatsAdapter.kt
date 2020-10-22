package com.example.messengerapp.AdapterClasses

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.messengerapp.ModelClasses.Chat
import com.example.messengerapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
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
        if (chat.getMessage().equals("sent you an image") &&
                !chat.getUrl().equals(""))
        {
            // image message - right side
            if (chat.getSender().equals(firebaseUser!!.uid)){
                holder.showTextMessage!!.visibility = View.GONE
                holder.rightImageView!!.visibility = View.VISIBLE
                Picasso.get().load(chat.getUrl()).into(holder.rightImageView)
            }
            // image message - left side
            else if (!chat.getSender().equals(firebaseUser!!.uid)){
                holder.showTextMessage!!.visibility = View.GONE
                holder.leftImageView!!.visibility = View.VISIBLE
                Picasso.get().load(chat.getUrl()).into(holder.leftImageView)
            }
        }
        //text messages
        else{
            holder.showTextMessage!!.text = chat.getMessage()
            Log.d("CHAT", chat.getMessage().toString())
        }

        // sent and seen message
        if (position == mChatList.size - 1){
           if (chat.isIsseen()!!){
               holder.textSeen!!.text = "seen"
               if (chat.getMessage().equals("sent you an image") &&
                   !chat.getUrl().equals("")){
                   val lp: RelativeLayout.LayoutParams? = holder.textSeen!!.layoutParams as RelativeLayout.LayoutParams
                   lp!!.setMargins(0, 245, 10, 0)
                   holder.textSeen!!.layoutParams = lp
               }
           }else{
               holder.textSeen!!.text = "sent"
               if (chat.getMessage().equals("sent you an image") &&
                   !chat.getUrl().equals("")){
                   val lp: RelativeLayout.LayoutParams? = holder.textSeen!!.layoutParams as RelativeLayout.LayoutParams
                   lp!!.setMargins(0, 245, 10, 0)
                   holder.textSeen!!.layoutParams = lp
               }
           }
        }else{
            holder.textSeen!!.visibility = View.GONE
        }
    }
}