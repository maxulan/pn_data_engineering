package net.pubnative.util

import java.nio.file.{Files, Paths}

object ProgramArgsValidator {

  def validate(fileNames: Iterable[String]) = {
    if (Option(fileNames).isEmpty || fileNames.size == 0) {
      val msg = "Missing input arguments: at least one filename should be supplied"
      throw new IllegalArgumentException(msg)
    }

    if (fileNames.size > 200) {
      val msg = "Too many input arguments: 200 filenames is max"
      throw new IllegalArgumentException(msg)
    }

    val nonExisting = fileNames.filterNot(fileName => Files.exists(Paths.get(fileName)))
    if (nonExisting.size > 0) {
      val msg = "Invalid arguments. Following input files don't exist: " + nonExisting.mkString("\"", "\", \"", "\"")
      throw new IllegalArgumentException(msg)
    }
  }

}
