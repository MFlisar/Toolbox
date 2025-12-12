package com.michaelflisar.toolbox.powershell

import java.io.BufferedReader

import java.io.InputStreamReader

object PSUtil {

    fun runCommand(command: String, wait: Boolean) : PSResult {

        val powerShellProcess = Runtime.getRuntime().exec("powershell.exe $command")
        if (!wait)
            return PSResult()

       powerShellProcess.outputStream.close()

        val outputs = mutableListOf<String>()
        val errors = mutableListOf<String>()

       var line: String?
       val stdout = BufferedReader(InputStreamReader(powerShellProcess.inputStream))
       while (stdout.readLine().also { line = it } != null) {
           line?.takeIf { it.trim().isNotEmpty() }?.let {  outputs.add(it) }
       }
       stdout.close()

       val stderr = BufferedReader(InputStreamReader(powerShellProcess.errorStream))
       while (stderr.readLine().also { line = it } != null) {
           line?.takeIf { it.isNotEmpty() }?.let {  errors.add(it) }
       }
       stderr.close()
       powerShellProcess.destroy()

        return PSResult(outputs, errors)
    }
}