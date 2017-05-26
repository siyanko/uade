package services

import java.text.SimpleDateFormat
import java.util.{Date, Locale}

object DateFormatter {

  private val UA = new Locale("uk", "UA")

  private val outputFormat = new SimpleDateFormat("dd MMMM yyyy", UA)

  def format(time: Long): String = outputFormat.format(new Date(time))
}
