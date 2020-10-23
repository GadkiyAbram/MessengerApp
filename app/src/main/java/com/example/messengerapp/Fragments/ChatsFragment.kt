package com.example.messengerapp.Fragments

import android.media.session.MediaSession
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.messengerapp.AdapterClasses.UserAdapter
import com.example.messengerapp.ModelClasses.Chat
import com.example.messengerapp.ModelClasses.ChatList
import com.example.messengerapp.ModelClasses.Users
import com.example.messengerapp.Notifications.Token
import com.example.messengerapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId

class ChatsFragment : Fragment() {

    private var userAdapter: UserAdapter? = null
    private var mUsers: List<Users>? = null
    private var usersChatList: List<ChatList>? = null
    lateinit var recyclerViewChatLists: RecyclerView
    private var firebaseUser: FirebaseUser? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chats, container, false)
        recyclerViewChatLists = view.findViewById(R.id.recycler_view_chatlist)
        recyclerViewChatLists.setHasFixedSize(true)
        recyclerViewChatLists.layoutManager = LinearLayoutManager(context)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        usersChatList = ArrayList()

        val ref = FirebaseDatabase.getInstance().reference.child("ChatLists").child(firebaseUser!!.uid)
        ref!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                (usersChatList as ArrayList).clear()

                for (dataSnapshot in p0.children){
                    val chatlist = dataSnapshot.getValue(ChatList::class.java)

                    (usersChatList as ArrayList).add(chatlist!!)
                }
                retrieveChatList()
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

        updateToken(FirebaseInstanceId.getInstance().token)

        return view
    }

    private fun updateToken(token: String?) {
        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")
        val token1 = Token(token!!)
        ref.child(firebaseUser!!.uid).setValue(token1)
    }

    private fun retrieveChatList(){
        mUsers = ArrayList()

        val ref = FirebaseDatabase.getInstance().reference.child("Users")
        ref!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                (mUsers as ArrayList).clear()

                for (dataSnapshot in p0.children){
                    val user = dataSnapshot.getValue(Users::class.java)

                    for (eachChatList in usersChatList!!){
                        if (user!!.getUID().equals(eachChatList.getId())){
                            (mUsers as ArrayList).add(user!!)
                        }
                    }
                }
                userAdapter = UserAdapter(context!!, (mUsers as ArrayList<Users>), true)
                recyclerViewChatLists.adapter = userAdapter
            }

        })
    }
}