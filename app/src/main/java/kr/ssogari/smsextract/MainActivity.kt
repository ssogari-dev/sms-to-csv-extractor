package kr.ssogari.smsextract

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentResolver
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.Telephony
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var startDateText: TextView
    private lateinit var endDateText: TextView
    private lateinit var exportButton: Button
    private lateinit var statusTextView: TextView
    private lateinit var smsCheckBox: CheckBox
    private lateinit var mmsCheckBox: CheckBox
    private lateinit var allDatesCheckBox: CheckBox
    private lateinit var incomingCheckBox: CheckBox
    private lateinit var outgoingCheckBox: CheckBox
    private lateinit var selectedColumnsRecyclerView: RecyclerView
    private lateinit var availableColumnsRecyclerView: RecyclerView
    private lateinit var creditButton: Button
    private lateinit var supportButton: Button
    private lateinit var attentionText: TextView

    private var startDate: Calendar = Calendar.getInstance()
    private var endDate: Calendar = Calendar.getInstance()

    private var fileUri: Uri? = null
    private lateinit var selectedColumnAdapter: ColumnItemAdapter
    private lateinit var availableColumnAdapter: ColumnItemAdapter

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1
    }

    private val createDocumentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            fileUri = result.data?.data
            exportMessagesToCsv()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 권한 요청
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_SMS, Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_MEDIA_AUDIO),
            PERMISSION_REQUEST_CODE
        )

        // UI 컴포넌트 초기화
        startDateText = findViewById(R.id.startDateText)
        endDateText = findViewById(R.id.endDateText)
        exportButton = findViewById(R.id.exportButton)
        statusTextView = findViewById(R.id.statusTextView)
        smsCheckBox = findViewById(R.id.smsCheckBox)
        mmsCheckBox = findViewById(R.id.mmsCheckBox)
        allDatesCheckBox = findViewById(R.id.allDatesCheckBox)
        incomingCheckBox = findViewById(R.id.incomingCheckBox)
        outgoingCheckBox = findViewById(R.id.outgoingCheckBox)
        selectedColumnsRecyclerView = findViewById(R.id.selectedColumnsRecyclerView)
        availableColumnsRecyclerView = findViewById(R.id.availableColumnsRecyclerView)
        creditButton = findViewById(R.id.creditButton)
        supportButton = findViewById(R.id.supportButton)
        attentionText = findViewById(R.id.attentionText)

        // 텍스트 클릭 이벤트 처리
        startDateText.setOnClickListener { openDatePickerDialog(true) }
        endDateText.setOnClickListener { openDatePickerDialog(false) }

        // 체크박스 클릭 이벤트 처리
        allDatesCheckBox.setOnCheckedChangeListener { _, isChecked -> toggleDatePickers(isChecked) }

        // 버튼 클릭 이벤트 처리
        exportButton.setOnClickListener { openFilePicker() }

        // Credit, Support 버튼 클릭 이벤트 처리
        creditButton.setOnClickListener { showCreditsDialog() }
        supportButton.setOnClickListener { showSupportDialog() }

        // 초기 날짜 설정
        updateDateText(startDateText, startDate)
        updateDateText(endDateText, endDate)

        // Column RecyclerView 초기화
        initColumnRecyclerViews()

        // 선택된 열이 없는 경우 버튼 비활성화
        updateExportButtonState()
    }

    private fun toggleDatePickers(isChecked: Boolean) {
        startDateText.isEnabled = !isChecked
        endDateText.isEnabled = !isChecked
    }

    private fun openDatePickerDialog(isStartDate: Boolean) {
        val calendar = if (isStartDate) startDate else endDate
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                updateDateText(if (isStartDate) startDateText else endDateText, calendar)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun updateDateText(textView: TextView, calendar: Calendar) {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        textView.text = format.format(calendar.time)
    }

    private fun openFilePicker() {
        val dateFormat = SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault())
        val currentTime = dateFormat.format(Date())
        val fileName = "sms_backup_$currentTime.csv"

        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/csv"
            putExtra(Intent.EXTRA_TITLE, fileName)
        }
        createDocumentLauncher.launch(intent)
    }

    private fun exportMessagesToCsv() {
        if (fileUri == null) {
            Toast.makeText(this, "File URI is null. Please select a file.", Toast.LENGTH_LONG).show()
            return
        }

        val startTime = if (allDatesCheckBox.isChecked) Long.MIN_VALUE else startDate.timeInMillis
        val endTime = if (allDatesCheckBox.isChecked) Long.MAX_VALUE else endDate.timeInMillis

        lifecycleScope.launch {
            contentResolver.openOutputStream(fileUri!!)?.use { outputStream ->
                OutputStreamWriter(outputStream, StandardCharsets.UTF_8).use { writer ->
                    val selectedColumns = selectedColumnAdapter.columnItems.map { it.name }
                    writer.append(selectedColumns.joinToString(",")).append("\n")

                    if (smsCheckBox.isChecked) {
                        exportSms(writer, startTime, endTime, selectedColumns)
                    }
                    if (mmsCheckBox.isChecked) {
                        exportMms(writer, startTime, endTime, selectedColumns)
                    }

                    withContext(Dispatchers.Main) {
                        statusTextView.text = "Messages exported to CSV file."
                        Toast.makeText(this@MainActivity, "Messages exported to CSV file.", Toast.LENGTH_LONG).show()
                        shareCsvFile(fileUri!!)
                    }
                }
            } ?: run {
                withContext(Dispatchers.Main) {
                    statusTextView.text = "Error while exporting messages."
                    Toast.makeText(this@MainActivity, "Error while exporting messages.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun exportSms(writer: OutputStreamWriter, startTime: Long, endTime: Long, selectedColumns: List<String>) {
        val contentResolver: ContentResolver = contentResolver
        val uri: Uri = Telephony.Sms.CONTENT_URI
        val selection = "${Telephony.Sms.DATE} BETWEEN ? AND ?"
        val selectionArgs = arrayOf(startTime.toString(), endTime.toString())
        val cursor: Cursor? = contentResolver.query(uri, null, selection, selectionArgs, null)

        cursor?.use {
            if (it.moveToFirst()) {
                do {
                    val address = it.getString(it.getColumnIndex(Telephony.Sms.ADDRESS))
                    val dateLong = it.getLong(it.getColumnIndex(Telephony.Sms.DATE))
                    val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(dateLong))
                    val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(dateLong))
                    val datetime = "$date $time"
                    val body = it.getString(it.getColumnIndex(Telephony.Sms.BODY)).replace("\n", " ").replace(",", " ")
                    val type = it.getInt(it.getColumnIndex(Telephony.Sms.TYPE))

                    val direction = if (type == Telephony.Sms.MESSAGE_TYPE_INBOX) "Incoming" else if (type == Telephony.Sms.MESSAGE_TYPE_SENT) "Outgoing" else "Unknown"

                    if ((incomingCheckBox.isChecked && direction == "Incoming") || (outgoingCheckBox.isChecked && direction == "Outgoing")) {
                        val senderNumber = if (direction == "Incoming") address else "Me"
                        val receiverNumber = if (direction == "Outgoing") address else "Me"

                        val rowData = mapOf(
                            "Type" to "SMS",
                            "Sender Number" to senderNumber,
                            "Receiver Number" to receiverNumber,
                            "Direction" to direction,
                            "Datetime" to datetime,
                            "Date" to date,
                            "Time" to time,
                            "Title" to "",
                            "Body" to body
                        )

                        val row = selectedColumns.map { rowData[it] ?: "" }.joinToString(",")
                        writer.append(row).append("\n")
                    }
                } while (it.moveToNext())
            }
        }
    }

    private fun exportMms(writer: OutputStreamWriter, startTime: Long, endTime: Long, selectedColumns: List<String>) {
        val contentResolver: ContentResolver = contentResolver
        val uri: Uri = Uri.parse("content://mms")
        val selection = "date >= ? AND date <= ?"
        val selectionArgs = arrayOf((startTime / 1000).toString(), (endTime / 1000).toString()) // MMS 날짜는 초 단위로 저장됨
        val cursor: Cursor? = contentResolver.query(uri, null, selection, selectionArgs, null)

        cursor?.use {
            if (it.moveToFirst()) {
                do {
                    val id = it.getString(it.getColumnIndex("_id"))
                    val dateLong = it.getLong(it.getColumnIndex("date")) * 1000
                    val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(dateLong))
                    val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(dateLong))
                    val datetime = "$date $time"
                    val address = getMmsAddress(id)
                    val body = getMmsText(id).replace("\n", " ").replace(",", " ")
                    val type = getMmsType(id)

                    val direction = if (type == PduHeaders.MESSAGE_TYPE_NOTIFICATION_IND) "Outgoing" else "Incoming"

                    if ((incomingCheckBox.isChecked && direction == "Incoming") || (outgoingCheckBox.isChecked && direction == "Outgoing")) {
                        val senderNumber = if (direction == "Outgoing") "Me" else address
                        val receiverNumber = if (direction == "Incoming") "Me" else address

                        val rowData = mapOf(
                            "Type" to "MMS",
                            "Sender Number" to senderNumber,
                            "Receiver Number" to receiverNumber,
                            "Direction" to direction,
                            "Datetime" to datetime,
                            "Date" to date,
                            "Time" to time,
                            "Title" to "",
                            "Body" to body
                        )

                        val row = selectedColumns.map { rowData[it] ?: "" }.joinToString(",")
                        writer.append(row).append("\n")
                    }
                } while (it.moveToNext())
            }
        }
    }

    private fun getMmsAddress(id: String): String {
        val uri = Uri.parse("content://mms/$id/addr")
        val cursor = contentResolver.query(uri, null, "type=137", null, null) // 137은 발신자 주소 타입
        cursor?.use {
            if (it.moveToFirst()) {
                return it.getString(it.getColumnIndex("address")) ?: ""
            }
        }
        return ""
    }

    private fun getMmsText(id: String): String {
        val uri = Uri.parse("content://mms/part")
        val cursor = contentResolver.query(uri, null, "mid=$id", null, null)
        val sb = StringBuilder()
        cursor?.use {
            while (it.moveToNext()) {
                val type = it.getString(it.getColumnIndex("ct"))
                if ("text/plain" == type) {
                    val partId = it.getString(it.getColumnIndex("_id"))
                    val partText = getMmsTextPart(partId)
                    sb.append(partText.replace("\n", " ").replace("\r", " ").replace("\r\n", " ").replace(",", " "))
                }
            }
        }
        return sb.toString()
    }

    private fun getMmsTextPart(id: String): String {
        val uri = Uri.parse("content://mms/part/$id")
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                return it.getString(it.getColumnIndex("text")) ?: ""
            }
        }
        return ""
    }

    private fun getMmsType(id: String): Int {
        val uri = Uri.parse("content://mms/$id")
        val cursor = contentResolver.query(uri, arrayOf("m_type"), null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                return it.getInt(it.getColumnIndex("m_type"))
            }
        }
        return PduHeaders.MESSAGE_TYPE_NOTIFICATION_IND // 기본값으로 수신 메시지 타입을 사용
    }

    private fun initColumnRecyclerViews() {
        val selectedColumns = mutableListOf(
            ColumnItem("Type"),
            ColumnItem("Sender Number"),
            ColumnItem("Datetime"),
            ColumnItem("Body")
        )

        val availableColumns = mutableListOf(
            ColumnItem("Receiver Number"),
            ColumnItem("Direction"),
            ColumnItem("Date"),
            ColumnItem("Time"),
            ColumnItem("Title")
        )

        selectedColumnAdapter = ColumnItemAdapter(selectedColumns) { item ->
            if (selectedColumnAdapter.removeItem(item)) {
                availableColumnAdapter.addItem(item)
                updateExportButtonState()
            }
        }

        availableColumnAdapter = ColumnItemAdapter(availableColumns) { item ->
            if (availableColumnAdapter.removeItem(item)) {
                selectedColumnAdapter.addItem(item)
                updateExportButtonState()
            }
        }

        selectedColumnsRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        selectedColumnsRecyclerView.adapter = selectedColumnAdapter

        availableColumnsRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        availableColumnsRecyclerView.adapter = availableColumnAdapter

        val itemTouchHelperSelected = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, 0) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                selectedColumnAdapter.moveItem(viewHolder.adapterPosition, target.adapterPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Swipe 동작은 처리하지 않음
            }
        })

        val itemTouchHelperAvailable = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, 0) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                availableColumnAdapter.moveItem(viewHolder.adapterPosition, target.adapterPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Swipe 동작은 처리하지 않음
            }
        })

        itemTouchHelperSelected.attachToRecyclerView(selectedColumnsRecyclerView)
        itemTouchHelperAvailable.attachToRecyclerView(availableColumnsRecyclerView)
    }

    private fun updateExportButtonState() {
        exportButton.isEnabled = selectedColumnAdapter.itemCount > 0
    }

    private fun showCreditsDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.credit_title))
        builder.setMessage(getString(R.string.credit_message))
        builder.setPositiveButton(getString(R.string.credit_twitter)) { _, _ ->
            openUrl(getString(R.string.twitter_url))
        }
        builder.setNegativeButton(getString(R.string.credit_github)) { _, _ ->
            openUrl(getString(R.string.github_url))
        }
        builder.setNeutralButton(getString(R.string.credit_close)) { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun showSupportDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.support_title))
        builder.setMessage(getString(R.string.support_message))
        builder.setPositiveButton(getString(R.string.support_paypal)) { _, _ ->
            openUrl(getString(R.string.support_paypal_url))
        }
        builder.setNegativeButton(getString(R.string.support_kakaopay)) { _, _ ->
            openUrl(getString(R.string.support_kakaopay_url))
        }
        builder.setNeutralButton(getString(R.string.credit_close)) { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    private fun shareCsvFile(fileUri: Uri) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, fileUri)
        }
        startActivity(Intent.createChooser(shareIntent, "Share CSV file"))
    }
}
