package dev.work.chatapp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {
    private lateinit var database: FirebaseDatabase
    private lateinit var messagesReference: DatabaseReference
    private lateinit var messageAdapter: ArrayAdapter<String>
    private lateinit var messagesList: MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)
        database = FirebaseDatabase.getInstance()
        messagesReference = database.getReference("messages")

        val listViewChat = findViewById<ListView>(R.id.listViewChat)
        val editTextMessage = findViewById<EditText>(R.id.editTextMessage)
        val buttonSend = findViewById<Button>(R.id.buttonSend)

        messagesList = ArrayList()
        messageAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, messagesList)
        listViewChat.adapter = messageAdapter

        buttonSend.setOnClickListener {
            val message = editTextMessage.text.toString()
            sendMessage(message)
            editTextMessage.text.clear()
        }

        messagesReference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val message = dataSnapshot.getValue(String::class.java)
                message?.let {
                    messagesList.add(it)
                    messageAdapter.notifyDataSetChanged()
                    listViewChat.setSelection(messageAdapter.count - 1)
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun sendMessage(message: String) {
        val key = messagesReference.push().key
        key?.let {
            messagesReference.child(it).setValue(message)
        }
    }
}