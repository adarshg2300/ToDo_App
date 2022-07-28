package com.trainee.appinventiv.notesapp.ui.note

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.CalendarContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Text
import com.trainee.appinventiv.notesapp.R
import com.trainee.appinventiv.notesapp.databinding.FragmentNotesBinding
import com.trainee.appinventiv.notesapp.model.request.NoteRequest
import com.trainee.appinventiv.notesapp.model.response.NoteResponse
import com.trainee.appinventiv.notesapp.utils.NetworkResult
import dagger.hilt.android.AndroidEntryPoint
import org.apache.poi.xwpf.usermodel.ParagraphAlignment
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


@AndroidEntryPoint
class NotesFragment : Fragment() , DatePickerDialog.OnDateSetListener ,
    TimePickerDialog.OnTimeSetListener {


    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!
    var startMillis: Long? = null
    var endMillis: Long? = null
    var day = 0
    var month = 0
    var year = 0
    var hour = 0
    var minute = 0

    var savedDay = 0
    var saveMonth = 0
    var saveYear = 0
    var saveHour = 0
    var saveMintue = 0

    var title = ""
    var desc = ""
    var count = 0
    var etStartDate: EditText? = null
    var etEndDate: EditText? = null
    var eventID: Long = 0
    private var note: NoteResponse? = null
    private val noteViewModel by viewModels<NotesViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater , container: ViewGroup? ,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentNotesBinding.inflate(inflater , container , false)
        return binding.root
    }

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        initialData()
        bindHandlers()
        bindObservers()

        binding.btnReminder.setOnClickListener {
            pickDate()
        }
        binding.btnConvertPdf.setOnClickListener {
            alertBox()
        }

        binding.btnConvertDoc.setOnClickListener {
            alertBoxDoc()
        }

    }

    private fun bindObservers() {
        noteViewModel.statusLiveData.observe(viewLifecycleOwner , Observer {
            when (it) {
                is NetworkResult.Success -> {
                    findNavController().popBackStack()
                }
                is NetworkResult.Error -> {

                }
                is NetworkResult.Loading -> {

                }
            }
        })
    }


    private fun initialData() {
        val jsonNote = arguments?.getString("note")

        if (jsonNote != null) {
            note = Gson().fromJson(jsonNote , NoteResponse::class.java)
            note.let {
                binding.txtTitle.setText(it?.title)
                binding.txtDescription.setText(it?.description)

                title = it!!.title
                desc = it.description

            }
        } else {
            binding.addEditText.text = getString(R.string.addnote)
            binding.btnConvertPdf.visibility = View.INVISIBLE
            binding.btnConvertDoc.visibility = View.INVISIBLE
        }
    }

    fun convertToPdf(filename: String) {

        val jsonNote = arguments?.getString("note")
        if (jsonNote != null) {
            note = Gson().fromJson(jsonNote , NoteResponse::class.java)
        }

        val pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .toString()
        val file = File(pdfPath , "$filename.pdf")
        val outPutStream = FileOutputStream(file)

        val writer = PdfWriter(file)

        val pdfDocument = PdfDocument(writer)

        val document = com.itextpdf.layout.Document(pdfDocument)

        val tile = Text("Title").setBold()
        val desc = Text("Description").setBold()
        val titlePara = Paragraph(tile)
        val descPara = Paragraph(desc)

        val paragraph = Paragraph(note?.title)
        val paragraph2 = Paragraph(note?.description)
        document.add(titlePara)
        document.add(paragraph)
        document.add(descPara)
        document.add(paragraph2)
        document.close()
        Toast.makeText(requireContext() , getString(R.string.savepdf) , Toast.LENGTH_SHORT).show()
    }

    private fun bindHandlers() {
        binding.btnDelete.setOnClickListener {
            note?.let { noteViewModel.deleteNotes(it._id) }
        }
        binding.apply {
            btnSubmit.setOnClickListener {

                val title = txtTitle.text.toString()
                val description = txtDescription.text.toString()
                val noteRequest = NoteRequest(title , description)
                if (note == null) {
                    noteViewModel.createNotes(noteRequest)
                } else {
                    Toast.makeText(
                        requireContext() ,
                        getString(R.string.update) ,
                        Toast.LENGTH_SHORT
                    ).show()
                    noteViewModel.updateNotes(note!!._id , noteRequest)
                }
            }
        }
    }

    private fun pickDate() {

        val mDailogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.date_picker_layout , null)
        val mBuilder = AlertDialog.Builder(requireContext())
            .setView(mDailogView)
            .setTitle(getString(R.string.selectdate))

        val mAlertDialog = mBuilder.show()

        val btnAddReminder = mDailogView.findViewById<Button>(R.id.btn_add_reminder)
        etStartDate = mDailogView.findViewById<EditText>(R.id.et_start_date)
        etEndDate = mDailogView.findViewById<EditText>(R.id.et_end_date)

        etStartDate!!.setOnClickListener {
            getDateTimeCalendar()
            val dtp = DatePickerDialog(requireContext() , this , year , month , day)
            dtp.updateDate(2022 , 6 , 6)
            dtp.show()
        }


        etEndDate!!.setOnClickListener {
            getDateTimeCalendar()
            val dtp = DatePickerDialog(requireContext() , this , year , month , day)
            dtp.updateDate(2022 , 6 , 6)
            dtp.show()


        }
        btnAddReminder.setOnClickListener {
            if (etEndDate != null && etStartDate != null) {
                addEvent()

            } else {
                Toast.makeText(
                    requireContext() ,
                    getString(R.string.pleaseselectdate) ,
                    Toast.LENGTH_SHORT
                ).show()
            }
            if (eventID > 0) {
                mAlertDialog.dismiss()
                Toast.makeText(
                    requireContext() ,
                    getString(R.string.addreminder) ,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun getDateTimeCalendar() {
        val cal: Calendar = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.DAY_OF_MONTH)
        year = cal.get(Calendar.DAY_OF_MONTH)
        hour = cal.get(Calendar.DAY_OF_MONTH)
        minute = cal.get(Calendar.DAY_OF_MONTH)
        day = cal.get(Calendar.DAY_OF_MONTH)

    }

    fun addEvent() {
        val calID: Long = 3

        val values = ContentValues().apply {
            put(CalendarContract.Events.DTSTART , startMillis)
            put(CalendarContract.Events.DTEND , endMillis)
            put(CalendarContract.Events.TITLE , title)
            put(CalendarContract.Events.DESCRIPTION , desc)
            put(CalendarContract.Events.CALENDAR_ID , calID)
            put(CalendarContract.Events.EVENT_TIMEZONE , "America/Los_Angeles")
        }
        val uri: Uri? =
            activity?.contentResolver?.insert(CalendarContract.Events.CONTENT_URI , values)

// get the event ID that is the last element in the Uri
        eventID = uri?.lastPathSegment?.toLong()!!
        // Toast.makeText(requireContext() , "$eventID" , Toast.LENGTH_SHORT).show()

    }


    private fun alertBox() {
        val mDailogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.file_name_layout , null)
        val mBuilder = AlertDialog.Builder(requireContext())
            .setView(mDailogView)
            .setTitle(getString(R.string.filename))

        val mAlertDialog = mBuilder.show()

        val btnSave = mDailogView.findViewById<Button>(R.id.btn_save)
        val etFileName = mDailogView.findViewById<EditText>(R.id.et_file_name)


        btnSave.setOnClickListener {

            if (etFileName.text.toString().isNotEmpty()) {
                mAlertDialog.dismiss()
                convertToPdf(etFileName.text.toString())
            } else {
                Toast.makeText(
                    requireContext() ,
                    getString(R.string.enterfilename) ,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun alertBoxDoc() {
        val mDailogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.file_name_layout , null)
        val mBuilder = AlertDialog.Builder(requireContext())
            .setView(mDailogView)
            .setTitle(getString(R.string.filename))

        val mAlertDialog = mBuilder.show()

        val btnSave = mDailogView.findViewById<Button>(R.id.btn_save)
        val etFileName = mDailogView.findViewById<EditText>(R.id.et_file_name)


        btnSave.setOnClickListener {

            if (etFileName.text.toString().isNotEmpty()) {
                mAlertDialog.dismiss()
                saveOurDoc(addParagraph(createWordDoc()) , etFileName.text.toString())
            } else {
                Toast.makeText(
                    requireContext() ,
                    getString(R.string.enterfilename) ,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onDateSet(p0: DatePicker? , p1: Int , p2: Int , p3: Int) {
        savedDay = p3
        saveMonth = p2
        saveYear = p1

        if (count == 0) {
            startMillis = Calendar.getInstance().run {
                set(saveYear , saveMonth , savedDay , saveHour , saveMintue)
                timeInMillis
            }
            getDateTimeCalendar()
            TimePickerDialog(requireContext() , this , hour , minute , false).show()
            etStartDate?.setText("$savedDay/$saveMonth/$saveYear")
            count++
        } else {
            endMillis = Calendar.getInstance().run {
                set(saveYear , saveMonth , savedDay , saveHour , saveMintue)
                timeInMillis
            }
            etEndDate!!.setText("$savedDay/$saveMonth/$saveYear")
            count = 0
        }


    }

    private fun addParagraph(targetDoc: XWPFDocument): XWPFDocument {
        //creating a paragraph in our document and setting its alignment
        val paragraph1 = targetDoc.createParagraph()
        paragraph1.alignment = ParagraphAlignment.LEFT

        //creating a run for adding text
        val mtitle = paragraph1.createRun()

        //format the text
        mtitle.isBold = true
        mtitle.fontSize = 15
        mtitle.fontFamily = "Comic Sans MS"
        mtitle.setText("Title")
        //add a sentence break
        mtitle.addBreak()

        val titleInput = paragraph1.createRun()

        //format the text
        titleInput.fontSize = 12
        titleInput.fontFamily = "Comic Sans MS"
        titleInput.setText(title)
        //add a sentence break
        titleInput.addBreak()


        //add another run
        val mDesc = paragraph1.createRun()
        mDesc.fontSize = 15
        mDesc.isBold = true
        mDesc.fontFamily = "Comic Sans MS"
        mDesc.setText("Description")
        mDesc.addBreak()

        val mDescInput = paragraph1.createRun()
        mDescInput.fontSize = 15

        mDescInput.fontFamily = "Comic Sans MS"
        mDescInput.setText(desc)
        mDescInput.addBreak()
        return targetDoc

    }


    private fun createWordDoc(): XWPFDocument {
        val ourWordDoc = XWPFDocument()
        return ourWordDoc
    }

    private fun saveOurDoc(targetDoc: XWPFDocument , fileName: String) {
        val ourAppFileDirectory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        //Check whether it exists or not, and create one if it does not exist.
        if (ourAppFileDirectory != null && !ourAppFileDirectory.exists()) {
            ourAppFileDirectory.mkdirs()
        }

        //Create a word file called test.docx and save it to the file system

        val wordFile = File(ourAppFileDirectory , "$fileName.docx")
        try {
            val fileOut = FileOutputStream(wordFile)
            targetDoc.write(fileOut)
            fileOut.close()
            Toast.makeText(requireContext() , getString(R.string.saveddoc) , Toast.LENGTH_SHORT).show()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onTimeSet(p0: TimePicker? , p1: Int , p2: Int) {
        saveHour = p1
        saveMintue = p2
        Toast.makeText(requireContext() , p1.toString() + p2.toString() , Toast.LENGTH_SHORT).show()
    }
}