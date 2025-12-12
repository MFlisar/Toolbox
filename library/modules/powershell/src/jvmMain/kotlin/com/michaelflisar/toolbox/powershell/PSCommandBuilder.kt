package com.michaelflisar.toolbox.powershell

class PSCommandBuilder(
    val name: String
) {
    private var commands = ArrayList<String>()
    private var wsShell: Boolean = false

    fun build() =  PSCommand(name, commands.joinToString(";"))

    fun add(command: String) : PSCommandBuilder {
        commands.add(command)
        return this
    }

    fun createWShell() : PSCommandBuilder {
        add("\$wshell = New-Object -ComObject wscript.shell")
        wsShell = true
        return this
    }

    fun sleep(millis: Int) : PSCommandBuilder {
        add("Start-Sleep -milliseconds $millis")
        return this
    }

    fun sendKey(key: String) : PSCommandBuilder {
        if (!wsShell)
            throw RuntimeException("No wscript.shell object found!")
        add("\$wshell.SendKeys('$key')")
        return this
    }

    fun checkIsProcessRunning(path: String) : PSCommandBuilder {
        add("Get-Process | ?{\$_.path -eq '$path'}")
        return this
    }

    fun startProcess(pathExe: String) : PSCommandBuilder {
        add("Start-Process -NoNewWindow -FilePath '$pathExe' -RedirectStandardOutput \".\\NUL\"")
        return this
    }

    fun stopProcess(likeProcessName: String) : PSCommandBuilder {
        add("Get-Process | Where-Object { \$_.MainWindowTitle -like '$likeProcessName' } | Stop-Process -Force -processname { \$_.ProcessName }")
        return this
    }

    class PSCommand(
        val name: String,
        val command: String
    ) {
        fun run(wait: Boolean = true): PSResult {
            val result = PSUtil.runCommand(command, wait)
            if (result.isEmpty) {
                println("Command $name")
            } else {
                println("Command $name: $result")
            }
            //println("    - $command")
            return result
        }
    }
}