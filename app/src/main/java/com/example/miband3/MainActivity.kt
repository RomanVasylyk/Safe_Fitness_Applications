package com.example.miband3

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var arrayAdapter: ArrayAdapter<String>
    private val bluetoothDevices = mutableListOf<BluetoothDevice>()
    private val deviceNames = mutableListOf<String>()
    private lateinit var miBandConnector: MiBandConnector

    private val REQUEST_LOCATION_PERMISSION = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        miBandConnector = MiBandConnector(this)

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        val listView: ListView = findViewById(R.id.lv_devices)
        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, deviceNames)
        listView.adapter = arrayAdapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedDevice = bluetoothDevices[position]
            miBandConnector.connectAndAuthorize(selectedDevice)
        }

        val filter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        }
        registerReceiver(receiver, filter)

        val scanButton: Button = findViewById(R.id.btn_scan)
        scanButton.setOnClickListener {
            checkLocationPermission()
        }
    }

    private fun checkLocationPermission() {
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
        } else {
            startScan()
        }
    }

    private fun startScan() {
        if (bluetoothAdapter.isDiscovering) {
            bluetoothAdapter.cancelDiscovery()
        }
        bluetoothAdapter.startDiscovery()
        Toast.makeText(this, "Skenovacie zariadenia...", Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                startScan()
            } else {
                Toast.makeText(this, "Povolenie na umiestnenie zamietnuté", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action: String? = intent.action
            if (BluetoothDevice.ACTION_FOUND == action) {
                val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                device?.let {
                    if (!bluetoothDevices.contains(device)) {
                        bluetoothDevices.add(device)
                        val deviceName = device.name ?: "Neznáme zariadenie"
                        deviceNames.add(deviceName)
                        arrayAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}
