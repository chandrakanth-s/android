package com.cm.retailer.ui.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.cm.retailer.R
import com.cm.retailer.Utils
import com.cm.retailer.data.Customer
import com.cm.retailer.databinding.ActivityHomeBinding
import com.cm.retailer.ui.global.base.adapter.BaseActivity
import com.cm.retailer.ui.home.customer.CustomerDetailsActivity
import com.cm.retailer.ui.home.customer.adapter.ContactActionListener
import com.cm.retailer.ui.home.downloader.DownloaderActivity
import com.cm.retailer.ui.home.notifications.CustomerAdapter
import com.cm.retailer.ui.home.product.list.ProductsListActivity
import java.util.*


class HomeActivity : BaseActivity() {
    internal val TAG = this.javaClass.canonicalName
    var viewModel = HomeViewModel()
    lateinit var adapter: CustomerAdapter
    lateinit var binding : ActivityHomeBinding
    private val REQUEST_CALL_PHONE = 112
    var customer: Customer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setTitle(getString(R.string.shop_name))
        viewModel.itemClickListener = object: ContactActionListener{

            override fun onSms(element: Customer) {
                var amount = viewModel.getBillAmount(element.id)
                var message = String.format(getString(R.string.msg_bill_payment,
                        element.name,
                        Utils.getServerDateInString(Date()),
                        amount.first,
                        amount.second,
                        getString(R.string.app_name)))
                val uri = Uri.parse("smsto:"+element.mobileNumber)
                val it = Intent(Intent.ACTION_SENDTO, uri)
                it.putExtra("sms_body", message)
                startActivity(it)
            }

            override fun onCall(element: Customer) {
                customer = element
                if (Build.VERSION.SDK_INT >= 23) {
                    val PERMISSIONS = arrayOf(android.Manifest.permission.CALL_PHONE)
                    if (!hasCallPermission(this@HomeActivity, PERMISSIONS.first())) {
                        ActivityCompat.requestPermissions(this@HomeActivity as Activity, PERMISSIONS, REQUEST_CALL_PHONE)
                    } else {
                        makeCall()
                    }
                } else {
                    makeCall()
                }
            }

            override fun onClick(element: Customer) {
                var cDetails = Intent(this@HomeActivity, CustomerDetailsActivity::class.java)
                cDetails.putExtra("CUSTOMER_ID", element.id)
                startActivityForResult(cDetails,1)
            }
        }

        binding = DataBindingUtil.setContentView<ActivityHomeBinding>(this, R.layout.activity_home)
        binding.viewModel = viewModel
        binding.executePendingBindings()
        initRecyclerView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_products -> {
                startActivity(Intent(this@HomeActivity, ProductsListActivity::class.java))
                return true
            }
            R.id.action_customers -> {
                startActivityForResult(Intent(this@HomeActivity, CustomerDetailsActivity::class.java),1)
                return true
            }
            R.id.action_report -> {
                startActivity(Intent(this@HomeActivity, DownloaderActivity::class.java))
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun initRecyclerView(){
        adapter = CustomerAdapter(viewModel)
        adapter.updateData(viewModel.getCustomerDetails())
        binding.rcvCustomers.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rcvCustomers.adapter = adapter
        binding.rcvCustomers.setItemViewCacheSize(30)
        binding.rcvCustomers.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode== Activity.RESULT_OK){
            initRecyclerView()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    @SuppressLint("MissingPermission")
    fun makeCall() {
        val intent4 = Intent(Intent.ACTION_CALL)
        intent4.data = Uri.parse("tel:${customer?.mobileNumber}")
        startActivity(intent4)
    }

    private fun hasCallPermission(context: Context?, vararg permissions: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(context!!, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CALL_PHONE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    makeCall()
                } else {
                    Toast.makeText(this, "The app was not allowed to call.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

}