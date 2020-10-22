package com.example.messengerapp.ModelClasses

class ChatList {
    private var id: String? = null

    constructor()

    constructor(id: String){
        this.id = id
    }

    fun getId(): String? {
        return id
    }
    fun setId(id: String) {
        this.id = id
    }
}