package com.example.crawler.activities

import android.app.DownloadManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android.volley.Response
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.crawler.Data.Node
import com.example.crawler.R
import com.example.crawler.utility.Constants
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_root.*

class RootActivity : AppCompatActivity() {

    var node : Node = Node("","",0.0)
    lateinit var db : FirebaseFirestore
    var timeLap = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)

        db = FirebaseFirestore.getInstance()


        hitUrl("https://www.amazon.in/gp/bestsellers/boost/ref=zg_bs_nav_0")

        for (i in 1..100){
            val randomValue = listOf(20,30,10).shuffled()
            val url = "https://opentdb.com/api.php?amount=${randomValue[0]}"
            hitUrl(url)
        }




    }





    fun hitUrl(url : String)  {
        val queue = Volley.newRequestQueue(this)
        val stringRequest = StringRequest(Request.Method.GET, url,
            Response.Listener<String> { response ->
                // Display the first 500 characters of the response string.
                updates_text_view.append("Response is: ${response.length/1056784.0}\n")

                node.url = url
                node.response = response
                node.size = response.length/1056784.0

                val user = hashMapOf(
                    "url" to node.url,
                    "response" to node.response,
                    "size" to node.size
                )

                db.collection("responses")
                    .add(user)
                    .addOnSuccessListener { documentReference ->
                        Log.d(Constants.HTML_CHECK, "DocumentSnapshot added with ID: ${documentReference.id}")
                    }
                    .addOnFailureListener { e ->
                        Log.w(Constants.HTML_CHECK, "Error adding document", e)
                    }


                val temp = data_mb.text.toString().toDouble()+node.size
                data_mb.text = temp.toString()

            },
            Response.ErrorListener {
                updates_text_view.append("wrong !!")


                node.url = url
                node.response = ""
                node.size = 0.0

            }
        )
        queue.add(stringRequest)

        Log.e(Constants.HTML_CHECK, node.toString())

    }
}
