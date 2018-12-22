package com.cm.retailer.ui.home.downloader

import android.app.DatePickerDialog
import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.ObservableList
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout
import android.widget.TableRow
import android.widget.TextView
import com.cm.retailer.R
import com.cm.retailer.Utils
import com.cm.retailer.data.Customer
import com.cm.retailer.data.Product
import com.cm.retailer.data.Purchases
import com.cm.retailer.databinding.ActivityDownloadBinding
import com.cm.retailer.extensions.binding.toObservable
import com.cm.retailer.extensions.observable.ObservableString
import com.cm.retailer.extensions.observable.OptimizedObservableArrayList
import com.cm.retailer.ui.global.base.adapter.BaseActivity
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.realm.Realm
import java.io.File
import java.io.FileOutputStream
import java.util.*


class DownloaderActivity : BaseActivity(){

    val CAPTURE_IMAGE_FILE_PROVIDER = "%s.fileprovider"
    var enableMenu = false
    var viewModel = DownloaderViewModel()
    protected val destroyDisposables: CompositeDisposable = CompositeDisposable()
    lateinit var binding: ActivityDownloadBinding
    private var bitmap: Bitmap? = null
    lateinit var scrollView:RelativeLayout

    var shopNameFont = Font(Font.FontFamily.TIMES_ROMAN, 18f,Font.BOLD)
    var tableHeadingFont = Font(Font.FontFamily.TIMES_ROMAN, 14f,Font.BOLD)
    var contentFont = Font(Font.FontFamily.TIMES_ROMAN, 12f,Font.BOLD)
    var purchases: ObservableList<Purchases>? = OptimizedObservableArrayList()
    var customerId: String? = null
    var calendarFrom = Calendar.getInstance()
    var calendarTo = Calendar.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_download)
        binding.viewModel = viewModel
        binding.executePendingBindings()

        initToolbar()
        scrollView = findViewById(R.id.scrollView)

        var args = intent.extras
        if(args!=null){
            customerId = args.getString("CUSTOMER_ID")
        }

        binding.dateListener = View.OnClickListener { view ->
            if(view.tag.equals("start")){
                val picker = DatePickerDialog(this,
                        DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                            calendarFrom.set(Calendar.YEAR,year)
                            calendarFrom.set(Calendar.MONTH,month+1)
                            calendarFrom.set(Calendar.DAY_OF_MONTH,dayOfMonth)
                            val selectedDate = calendarFrom.time
                            var currentState = viewModel.state.get() as DownloaderStateModel.Data
                            calendarFrom.set(Calendar.HOUR, 0)
                            calendarFrom.set(Calendar.MINUTE, 0)
                            calendarFrom.set(Calendar.SECOND, 0)
                            calendarFrom.set(Calendar.HOUR_OF_DAY, 0)
                            viewModel.fromDateCalendar = calendarFrom.time
                            viewModel.state.set(currentState.copy(fromDate = ObservableString( Utils.toServerFormat(selectedDate))))
                        },
                        calendarFrom.get(Calendar.YEAR),
                        calendarFrom.get(Calendar.MONTH)-1,
                        calendarFrom.get(Calendar.DAY_OF_MONTH))
                picker.show()
            }else{
                val picker = DatePickerDialog(this,
                        DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                            calendarTo.set(Calendar.YEAR,year)
                            calendarTo.set(Calendar.MONTH,month+1)
                            calendarTo.set(Calendar.DAY_OF_MONTH,dayOfMonth)
                            val selectedDate = calendarTo.time
                            var currentState = viewModel.state.get() as DownloaderStateModel.Data
                            viewModel.toDateCalendar = calendarTo.time
                            viewModel.state.set(currentState.copy(toDate = ObservableString( Utils.toServerFormat(selectedDate))))
                        },
                        calendarTo.get(Calendar.YEAR),
                        calendarTo.get(Calendar.MONTH)-1,
                        calendarTo.get(Calendar.DAY_OF_MONTH))
                picker.show()
            }


        }

        viewModel.state
                .toObservable()
                .filter { it is DownloaderStateModel.Data }
                .map { it as DownloaderStateModel.Data }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {

                    this.purchases = it.entries

                    binding.tableLayout.removeAllViews()

                    val row = TableRow(this)
                    val tv1 = TextView(this)
                    tv1.text = " Date "
                    tv1.setGravity(Gravity.CENTER)
                    tv1.setTextColor(Color.WHITE)
                    tv1.setPadding(32,32,32,32)
                    row.addView(tv1)
                    row.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimary))

                    val tv2 = TextView(this)
                    tv2.text = getString(R.string.particulars)
                    tv2.setTextColor(Color.WHITE)
                    tv2.setGravity(Gravity.CENTER)
                    tv2.setPadding(32,32,32,32)
                    row.addView(tv2)
                    row.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimary))

                    val tv3 = TextView(this)
                    tv3.text = " Units "
                    tv3.setTextColor(Color.WHITE)
                    tv3.setGravity(Gravity.CENTER)
                    tv3.setPadding(32,32,32,32)
                    row.addView(tv3)
                    row.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimary))

                    val tv5 = TextView(this)
                    tv5.text = " Kind "
                    tv5.setTextColor(Color.WHITE)
                    tv5.gravity = Gravity.CENTER
                    tv5.setPadding(32,32,32,32)
                    row.addView(tv5)
                    row.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimary))

                    val tv4 = TextView(this)
                    tv4.text = " Amount "
                    tv4.setTextColor(Color.WHITE)
                    tv4.setGravity(Gravity.CENTER)
                    tv4.setPadding(32,32,32,32)
                    row.addView(tv4)
                    row.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimary))
                    binding.tableLayout.addView(row)

                    it.entries.forEach {entry->

                        var productItem = Realm.getDefaultInstance().where(Product::class.java).equalTo("id",entry.productId).findFirst()

                        val row = TableRow(this)
                        val tv1 = TextView(this)
                        tv1.text = Utils.toServerFormat(entry.createdDate)
                        tv1.setTextColor(Color.BLACK)
                        tv1.setGravity(Gravity.CENTER)
                        tv1.setPadding(32,32,32,32)
                        row.addView(tv1)
                        row.setBackgroundColor(ContextCompat.getColor(this,android.R.color.white))

                        val tv2 = TextView(this)
                        tv2.text = entry.name
                        tv2.setTextColor(Color.BLACK)
                        tv2.setGravity(Gravity.CENTER)
                        tv2.setPadding(32,32,32,32)
                        row.addView(tv2)
                        row.setBackgroundColor(ContextCompat.getColor(this,android.R.color.white))

                        val tv3 = TextView(this)
                        tv3.text = entry.units
                        tv3.setTextColor(Color.BLACK)
                        tv3.setGravity(Gravity.CENTER)
                        tv3.setPadding(32,32,32,32)
                        row.addView(tv3)
                        row.setBackgroundColor(ContextCompat.getColor(this,android.R.color.white))

                        val tv5 = TextView(this)
                        tv5.text = productItem?.kind
                        tv5.setTextColor(Color.BLACK)
                        tv5.setGravity(Gravity.CENTER)
                        tv5.setPadding(32,32,32,32)
                        row.addView(tv5)
                        row.setBackgroundColor(ContextCompat.getColor(this,android.R.color.white))

                        val tv4 = TextView(this)
                        tv4.text = Utils.calculate(entry.units,productItem?.price)
                        tv4.setGravity(Gravity.CENTER)
                        tv4.setTextColor(Color.BLACK)
                        tv4.setPadding(32,32,32,32)
                        row.addView(tv4)
                        row.setBackgroundColor(ContextCompat.getColor(this,android.R.color.white))

                        binding.tableLayout.addView(row)

                    }
                }
                .addTo(destroyDisposables)

    }

    private fun initToolbar(){
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.filter)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.filter, menu)
        return true
    }


    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun openGeneratedPDF(url: File) {
        if (url.exists()) {

        }else{
            return
        }

        // Create URI
        val uri = FileProvider.getUriForFile(this, String.format(CAPTURE_IMAGE_FILE_PROVIDER, packageName), url)
        val intent = Intent(Intent.ACTION_VIEW)
        try{
            // Check what kind of file you are trying to open, by comparing the url with extensions.
            // When the if condition is matched, plugin sets the correct intent (mime) type,
            // so Android knew what application to use to open the file
            if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
                // Word document
                intent.setDataAndType(uri, "application/msword")
            } else if (url.toString().contains(".pdf")) {
                // PDF file
                intent.setDataAndType(uri, "application/pdf")
            } else if (url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
                // Powerpoint file
                intent.setDataAndType(uri, "application/vnd.ms-powerpoint")
            } else if (url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
                // Excel file
                intent.setDataAndType(uri, "application/vnd.ms-excel")
            } else if (url.toString().contains(".zip") || url.toString().contains(".rar")) {
                // WAV audio file
                intent.setDataAndType(uri, "application/x-wav")
            } else if (url.toString().contains(".rtf")) {
                // RTF file
                intent.setDataAndType(uri, "application/rtf")
            } else if (url.toString().contains(".wav") || url.toString().contains(".mp3")) {
                // WAV audio file
                intent.setDataAndType(uri, "audio/x-wav")
            } else if (url.toString().contains(".gif")) {
                // GIF file
                intent.setDataAndType(uri, "image/gif")
            } else if (url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
                // JPG file
                intent.setDataAndType(uri, "image/jpeg")
            } else if (url.toString().contains(".txt")) {
                // Text file
                intent.setDataAndType(uri, "text/plain")
            } else if (url.toString().contains(".3gp") || url.toString().contains(".mpg") || url.toString().contains(".mpeg") || url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
                // Video files
                intent.setDataAndType(uri, "video/*")
            } else {
                //if you want you can also define the intent type for any other file
                //additionally use else clause below, to manage other unknown extensions
                //in this case, Android will show all applications installed on the device
                //so you can choose which application to use
                intent.setDataAndType(uri, "*/*")
            }
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }catch (e: Exception){
            intent.setDataAndType(uri, "*/*")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_download -> {
                if(this.purchases!=null && this.purchases!!.isNotEmpty()){
                    createDocument(this.purchases!!)
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun addEmptyLine(paragraph: Paragraph, number: Int) {

        for(i in 0..number){
            paragraph.add(Paragraph(" "));
        }
    }


    private fun createDocument(purchases: ObservableList<Purchases>){

        var dir = filesDir.absolutePath+File.separator+"myDirectory";
        //create folder
        var folder = File(dir); //folder name
        folder.mkdirs();

        var filePath = File(folder, Calendar.getInstance().timeInMillis.toString()+".pdf")
        filePath?.createNewFile()

        var document = Document()
        PdfWriter.getInstance(document, FileOutputStream(filePath));
        document.open()
        document.newPage()

        var paragraph = Paragraph(getString(R.string.shop_name),shopNameFont)
        paragraph.alignment = Element.ALIGN_CENTER

        addEmptyLine(paragraph, 2);

        document.add(paragraph)

        var userInfoTable = PdfPTable(2)

        if(customerId!=null){
            var customer = Realm.getDefaultInstance().where(Customer::class.java).equalTo("id",customerId).findFirst()
            if(customer!=null){
                userInfoTable.addCell(PdfPCell(Phrase("Name",tableHeadingFont)))
                userInfoTable.addCell(PdfPCell(Phrase(customer.name)))
                userInfoTable.setHeaderRows(1)

                userInfoTable.addCell(Phrase("Mobile number",tableHeadingFont))
                userInfoTable.addCell(customer.mobileNumber)

                userInfoTable.addCell(Phrase("Address",tableHeadingFont))
                userInfoTable.addCell(customer.address)

                document.add(userInfoTable)
                var empty = Paragraph("")
                addEmptyLine(empty, 1)
                document.add(empty)
            }
        }

        var table = PdfPTable(5)

        var c1 = PdfPCell(Phrase("Date",tableHeadingFont))
        c1.horizontalAlignment = Element.ALIGN_CENTER;
        table.addCell(c1)

        c1 = PdfPCell(Phrase(getString(R.string.particulars),tableHeadingFont))
        c1.horizontalAlignment = Element.ALIGN_CENTER;
        table.addCell(c1)

        c1 = PdfPCell(Phrase(getString(R.string.label_unit),tableHeadingFont));
        c1.horizontalAlignment = Element.ALIGN_CENTER;
        table.addCell(c1);

        c1 = PdfPCell(Phrase(getString(R.string.kind),tableHeadingFont));
        c1.horizontalAlignment = Element.ALIGN_CENTER;
        table.addCell(c1);

        c1 = PdfPCell(Phrase("Amount",tableHeadingFont));
        c1.horizontalAlignment = Element.ALIGN_CENTER;
        table.addCell(c1)

        table.setHeaderRows(1)

        var totalValue = 0.0

        purchases.forEach {
            table.addCell(Utils.toServerFormat(it.createdDate))
            table.addCell(it.name)
            table.addCell(it.units)
            table.addCell(Realm.getDefaultInstance().where(Product::class.java).equalTo("id",it.productId).findFirst()?.kind)
            var productItem = Realm.getDefaultInstance().where(Product::class.java).equalTo("id",it.productId).findFirst()
            if(productItem!=null){
                table.addCell(Utils.calculate(it.units, productItem.price))
                totalValue += (it.units.toDouble() * productItem.price.toDouble())
            }
        }

        table.addCell("")
        table.addCell("")
        table.addCell("")
        table.addCell(Phrase("Total",tableHeadingFont))
        table.addCell(Utils.getformattedAmount(totalValue.toString()))

        document.add(table)
        document.close()

        openGeneratedPDF(filePath)
    }

}