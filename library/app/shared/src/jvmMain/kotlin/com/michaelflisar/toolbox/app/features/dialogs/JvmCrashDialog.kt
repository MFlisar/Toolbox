package com.michaelflisar.toolbox.app.features.dialogs

import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Font
import java.awt.Frame
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.JDialog
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JSplitPane
import javax.swing.JTextArea
import javax.swing.SwingConstants
import javax.swing.UIManager
import javax.swing.WindowConstants

object JvmCrashDialog {

    const val DEFAULT_WIDTH = 800
    const val DEFAULT_HEIGHT = 600

    const val MIN_WIDTH = 800
    const val MIN_HEIGHT = 600

    fun showExceptionDialog(
        title: String,
        throwable: Throwable,
    ) {
        val infoPanel = JPanel(GridBagLayout())

        var row = 0

        addRow(
            infoPanel,
            row++,
            "Java Home:",
            System.getProperty("java.home")
        )

        addRow(
            infoPanel,
            row++,
            "Java Runtime:",
            "${System.getProperty("java.runtime.name")} ${System.getProperty("java.runtime.version")}"
        )

        addRow(
            infoPanel,
            row++,
            "Java Vendor:",
            System.getProperty("java.vendor")
        )

        addRow(
            infoPanel,
            row++,
            "Working Directory:",
            System.getProperty("user.dir")
        )

        addFillRow(infoPanel, row++)


        val stacktraceArea = JTextArea(
            throwable.stackTraceToString()
        ).apply {
            isEditable = false
            font = Font(Font.MONOSPACED, Font.PLAIN, 12)
            caretPosition = 0
        }

        val splitPane = JSplitPane(
            JSplitPane.VERTICAL_SPLIT,
            JScrollPane(infoPanel),
            JScrollPane(stacktraceArea)
        ).apply {
            resizeWeight = 0.33
            dividerLocation = 220
        }

        val iconLabel = JLabel(
            UIManager.getIcon("OptionPane.errorIcon")
        ).apply {
            horizontalAlignment = SwingConstants.CENTER
            border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
        }

        val closeButton = JButton("Close")

        val content = JPanel(BorderLayout(0, 10)).apply {
            border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
            add(iconLabel, BorderLayout.NORTH)
            add(splitPane, BorderLayout.CENTER)
        }

        val dialog = JDialog(null as Frame?, title, true).apply {
            defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
            layout = BorderLayout()

            add(content, BorderLayout.CENTER)

            add(
                JPanel(FlowLayout(FlowLayout.RIGHT)).apply {
                    add(closeButton)
                },
                BorderLayout.SOUTH
            )

            size = Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT)
            minimumSize = Dimension(MIN_WIDTH, MIN_HEIGHT)
            setLocationRelativeTo(null)
        }

        closeButton.addActionListener {
            dialog.dispose()
        }

        dialog.isVisible = true
    }

    private fun gbc(x: Int, y: Int) = GridBagConstraints().apply {
        gridx = x
        gridy = y
        anchor = GridBagConstraints.NORTHWEST
        insets = Insets(4, 8, 4, 8)

        weightx = if (x == 1) 1.0 else 0.0
        fill = if (x == 1) GridBagConstraints.HORIZONTAL else GridBagConstraints.NONE

        weighty = 0.0
    }

    private fun addRow(
        infoPanel: JPanel,
        row: Int,
        label: String,
        value: String,
    ) {
        infoPanel.add(
            JLabel("<html><b>$label</b></html>"),
            gbc(0, row)
        )

        infoPanel.add(
            JLabel(value),
            gbc(1, row)
        )
    }

    private fun addFillRow(infoPanel: JPanel, row: Int) {
        infoPanel.add(
            JPanel(),
            GridBagConstraints().apply {
                gridx = 0
                gridy = row
                gridwidth = 2

                weightx = 1.0
                weighty = 1.0

                fill = GridBagConstraints.BOTH
            }
        )
    }

}