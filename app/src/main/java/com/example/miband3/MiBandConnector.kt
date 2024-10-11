package com.example.miband3

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.miband3.com.luigidarco.mibandconnector.ActionCallback
import com.example.miband3.com.luigidarco.mibandconnector.MiBand


class MiBandConnector(private val context: Context) {

    private var miBand: MiBand? = null

    fun connectAndAuthorize(device: BluetoothDevice) {
        miBand = MiBand()

        miBand?.connect(device, object : ActionCallback {
            override fun onSuccess(data: Any?) {
                (context as MainActivity).runOnUiThread {
                    Toast.makeText(context, "Pripojené k ${device.name}", Toast.LENGTH_SHORT).show()
                }
                authorizeMiBand()
            }

            override fun onFailure(errorCode: Int, msg: String?) {
                (context as MainActivity).runOnUiThread {
                    Toast.makeText(context, "Chyba pripojenia: $msg", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun authorizeMiBand() {
        miBand?.pair(object : ActionCallback {
            override fun onSuccess(data: Any?) {
                (context as MainActivity).runOnUiThread {
                    Toast.makeText(context, "Autorizácia je úspešná", Toast.LENGTH_SHORT).show()
                }

                val intent = Intent(context, HeartRateActivity::class.java)
                intent.putExtra("heartRate", 0)
                intent.putExtra("steps", 0)
                intent.putExtra("meters", 0)
                intent.putExtra("calories", 0)
                context.startActivity(intent)

//                miBand?.showServicesAndCharacteristics();

                getRealtimeData()
            }

            override fun onFailure(errorCode: Int, msg: String?) {
                (context as MainActivity).runOnUiThread {
                    Toast.makeText(context, "Autorizácia zlyhala: $msg", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun getRealtimeData() {
        miBand?.setHeartRateScanListener { heartRate ->
            Log.d("HeartRateData", "Srdcový rytmus: $heartRate bpm")

            val intent = Intent("HEART_RATE_UPDATED")
            intent.putExtra("heartRate", heartRate)
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
        }

        miBand?.setRealtimeStepsNotifyListener { stepsData ->
            val steps = stepsData[0]
            val meters = stepsData[1]
            val calories = stepsData[2]

            Log.d("StepsData", "Kroky: $steps, Metre: $meters, Kalórie: $calories")

            val intent = Intent("STEPS_UPDATED")
            intent.putExtra("steps", steps)
            intent.putExtra("meters", meters)
            intent.putExtra("calories", calories)
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
        }

        miBand?.startHeartRateScan()
        miBand?.enableRealtimeStepsNotify()
    }

    fun disconnect() {
        miBand?.close()
        (context as MainActivity).runOnUiThread {
            Toast.makeText(context, "Spojenie je prerušené", Toast.LENGTH_SHORT).show()
        }
    }
}
