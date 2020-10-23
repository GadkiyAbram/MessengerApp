package com.example.messengerapp.AdapterClasses

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.messengerapp.MessageChatActivity
import com.example.messengerapp.ModelClasses.Chat
import com.example.messengerapp.ModelClasses.Users
import com.example.messengerapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.user_search_item_layout.view.*

class UserAdapter(
        mContext: Context,
        mUsers: List<Users>,
        isChatCheck: Boolean
        ) : RecyclerView.Adapter<UserAdapter.ViewHolder?>(){

    private val mContext: Context
    private val mUsers: List<Users>
    private var isChatCheck: Boolean
    var lastMsg: String = ""

    init {
        this.mUsers = mUsers
        this.mContext = mContext
        this.isChatCheck = isChatCheck
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(mContext).inflate(R.layout.user_search_item_layout, viewGroup, false)
        return UserAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mUsers.size
    }

    override fun onBindViewHolder(holder: ViewHolder, i: Int) {
        val user: Users? = mUsers[i]
        holder.userNameTxt.text = user!!.getUsername().toString()
        Picasso.get().load(user.getProfile()).into(holder.profileImageView);

        if (isChatCheck){
            retrieveLastMessage(user.getUID(), holder.lastMessageTxt)
        }else{
            holder.lastMessageTxt.visibility = View.GONE
        }

        if (isChatCheck){
            if (user.getStatus() == "online"){
                holder.onlineImageView.visibility = View.VISIBLE
                holder.offlineImageView.visibility = View.GONE
            }else{
                holder.onlineImageView.visibility = View.GONE
                holder.offlineImageView.visibility = View.VISIBLE
            }
        }else{
            holder.onlineImageView.visibility = View.GONE
            holder.offlineImageView.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            val options = arrayOf<CharSequence>(
                "Send Message",
                "Visit Profile"
            )
            val builder: AlertDialog.Builder = AlertDialog.Builder(mContext)
            builder.setTitle("Whot do you want?")
            builder.setItems(options, DialogInterface.OnClickListener { dialog, position ->
                if (position == 0){
                    val intent = Intent(mContext, MessageChatActivity::class.java)
                    intent.putExtra("visit_id", user.getUID())
                    mContext.startActivity(intent)
                }
                if (position == 1){

                }
            })
            builder.show()
        }
    }

    private fun retrieveLastMessage(chatUserId: String?, lastMessageTxt: TextView) {
        lastMsg = "defaultMsg"

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")

        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                for (dataSnapshot in p0.children){
                    val chat: Chat? = dataSnapshot.getValue(Chat::class.java)

                    if (firebaseUser != null && chat != null){
                        if (chat.getReceiver() == firebaseUser!!.uid && chat.getSender() == chatUserId || chat.getReceiver() == chatUserId && chat.getSender() == firebaseUser!!.uid){
                            lastMsg = chat.getMessage()!!
                        }
                    }
                }
                when(lastMsg){
                    "defaultMsg" -> lastMessageTxt.text = "no message"
                    "sent you an image" -> lastMessageTxt.text = "image sent"
                    else -> lastMessageTxt.text = lastMsg
                }
                lastMsg = "defaultMsg"
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var userNameTxt: TextView
        var profileImageView: CircleImageView
        var onlineImageView: CircleImageView
        var offlineImageView: CircleImageView
        var lastMessageTxt: TextView

        init {
            userNameTxt = itemView.findViewById(R.id.username)
            profileImageView = itemView.findViewById(R.id.profile_image)
            onlineImageView = itemView.findViewById(R.id.image_online)
            offlineImageView = itemView.findViewById(R.id.image_offline)
            lastMessageTxt = itemView.findViewById(R.id.message_last)
        }
    }
}