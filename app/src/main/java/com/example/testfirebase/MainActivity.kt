package com.example.testfirebase

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.testfirebase.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Authentication and Database
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Users")

        // Check if the user is authenticated
        val currentUser = auth.currentUser
        if (currentUser == null) {
            signInAnonymously()
        }

        // Button click listener to save message to Firebase
        binding.btnSend.setOnClickListener {
            saveMessage()
        }
    }

    private fun signInAnonymously() {
        auth.signInAnonymously()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Signed in anonymously", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Sign-in failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveMessage() {
        val message = binding.etMessage.text.toString().trim()
        val userId = auth.currentUser?.uid

        if (message.isEmpty() || userId == null) {
            Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show()
            return
        }

        // Save message to the authenticated user's node in Firebase Database
        val messageId = database.child(userId).push().key ?: return
        val messageData = MessageModel(id = messageId, content = message)

        database.child(userId).child(messageId).setValue(messageData)
            .addOnSuccessListener {
                Toast.makeText(this, "Message saved!", Toast.LENGTH_SHORT).show()
                binding.etMessage.text.clear()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save message", Toast.LENGTH_SHORT).show()
            }
    }
}

// MessageModel class
data class MessageModel(
    val id: String = "",
    val content: String = ""
)