package com.example.drmdemo


import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil.setContentView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.drmdemo.databinding.ActivityMainBinding
import com.example.drmdemo.helper.SharedPrefs
import com.example.drmdemo.helper.Utils
import com.example.drmdemo.model.BasicModel
import com.example.drmdemo.viewModel.StartActivityVM
import kotlinx.android.synthetic.main.activity_main.*


class StartingActivity : AppCompatActivity() {

    lateinit var model: BasicModel
    private val startActivityVM: StartActivityVM by lazy {
        ViewModelProviders.of(this).get(StartActivityVM::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = startActivityVM

        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )
        startActivityVM.ObserveForChange().observe(this, Observer {
            it?.getContentIfNotHandled()?.let {
                when (it) {

                    "1" -> {
                        val intent = Intent(this@StartingActivity, PlayVideoActivity::class.java)
                        intent.putExtra("data", model)
                        startActivityForResult(intent, 101)
                    }
                    else -> {
                        Toast.makeText(this, it, Toast.LENGTH_LONG).show()

                    }
                }
            }
        })


        startActivityVM.ObserveServiceResponse().observe(this, Observer {
            model = it
        })

        ivCancel.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver, filter)

    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
        SharedPrefs.removeAll(this)
    }

    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (!isInitialStickyBroadcast()) {
                if (Utils.isConnected(this@StartingActivity)) {
                    if (SharedPrefs.getStoredResponse(this@StartingActivity) != null) {
                        val intent1 = Intent(this@StartingActivity, PlayVideoActivity::class.java)
                        intent1.putExtra("data", SharedPrefs.getStoredResponse(this@StartingActivity))
                        startActivityForResult(intent1, 101)

                    }
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {

        }

        if (resultCode == Activity.RESULT_CANCELED) {
            Handler().postDelayed(Runnable {
                if (SharedPrefs.getStoredResponse(this@StartingActivity) != null && Utils.isConnected(this)) {
                    val intent1 = Intent(this@StartingActivity, PlayVideoActivity::class.java)
                    intent1.putExtra("data", SharedPrefs.getStoredResponse(this@StartingActivity))
                    startActivityForResult(intent1, 101)
                }

            }, 30000)
        }
    }
}
//http://l.deployninja.com/717610