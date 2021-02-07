package ly.mensly.forgiveyourself

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import java.util.*

private const val ARG_DATETIME = "datetime"

class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {
    companion object {
        fun showInstance(activity: FragmentActivity, time: Long) {
            TimePickerFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_DATETIME, time)
                }
            }.show(activity.supportFragmentManager, "timePicker")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current time as the default values for the picker
        val c = Calendar.getInstance()
        c.timeInMillis = requireArguments().getLong(ARG_DATETIME)
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        // Create a new instance of TimePickerDialog and return it
        return TimePickerDialog(requireContext(), this, hour, minute, DateFormat.is24HourFormat(activity))
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        val c = Calendar.getInstance()
        c.timeInMillis = NotificationService.instance.scheduledTime.value!!
        c.set(Calendar.HOUR_OF_DAY, hourOfDay)
        c.set(Calendar.MINUTE, minute)
        NotificationService.instance.scheduledTime.value = c.timeInMillis
    }
}

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {
    companion object {
        fun showInstance(activity: FragmentActivity, time: Long) {
            DatePickerFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_DATETIME, time)
                }
            }.show(activity.supportFragmentManager, "datePicker")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val c = Calendar.getInstance()
        c.timeInMillis = requireArguments().getLong(ARG_DATETIME)
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Create a new instance of DatePickerDialog and return it
        return DatePickerDialog(requireContext(), this, year, month, day).apply {
            this.datePicker.minDate = System.currentTimeMillis()
        }
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        val c = Calendar.getInstance()
        c.timeInMillis = NotificationService.instance.scheduledTime.value!!
        c.set(Calendar.YEAR, year)
        c.set(Calendar.MONTH, month)
        c.set(Calendar.DAY_OF_MONTH, day)
        TimePickerFragment.showInstance(requireActivity(), c.timeInMillis)
    }
}