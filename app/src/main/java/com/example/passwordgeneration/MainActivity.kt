package com.example.passwordgeneration

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textPassword = findViewById<TextView>(R.id.textPassword)
        val textLengthQtd = findViewById<TextView>(R.id.textLengthQtd)
        val seekBar = findViewById<SeekBar>(R.id.seekBarLength)
        val buttonSave = findViewById<Button>(R.id.buttonSave)
        val buttonRefresh = findViewById<ImageButton>(R.id.buttonRefresh)
        val buttonCopy = findViewById<ImageButton>(R.id.buttonCopy)

        val switches = listOf(
            findViewById<Switch>(R.id.switchLetter) to "abcdefghijklmnopqrstuvwxyz",
            findViewById<Switch>(R.id.switchNumber) to "0123456789",
            findViewById<Switch>(R.id.switchCapital) to "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
            findViewById<Switch>(R.id.switchSpecialC) to "!@#\$%&*?+-_=<>",
            findViewById<Switch>(R.id.switchEmoji) to "😀😁😂🤣😎😍😡👍🔥✨"
        )

        // Atualiza contador ao mover SeekBar
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                val length = progress.coerceAtLeast(4) // força mínimo de 4
                textLengthQtd.text = length.toString()
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })

        // Gera senha ao clicar no botão Refresh
        buttonRefresh.setOnClickListener {
            textPassword.text = generatePassword(seekBar.progress, switches)
        }

        // Salva senha em arquivo ao clicar em "Salvar"
        buttonSave.setOnClickListener {
            val password = textPassword.text.toString()
            if (password.isNotBlank() && password != "Selecione parâmetros!") {
                savePasswordToFile(password)
                val caminho = "/storage/emulated/0/Download/arquivo.txt"
                Toast.makeText(this, "Arquivo salvo em: " + caminho, Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Nenhuma senha para salvar!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Função para gerar senha
    private fun generatePassword(length: Int, switches: List<Pair<Switch, String>>): String {
        val pool = switches.filter { it.first.isChecked }.joinToString("") { it.second }
        val size = length.coerceAtLeast(4)
        return if (pool.isNotEmpty()) {
            (1..size).map { pool.random() }.joinToString("")
        } else "Selecione parâmetros!"
    }

    // Função para salvar senha em arquivo interno
    private fun savePasswordToFile(password: String) {
        val file = File(filesDir, "senha.txt")
        FileOutputStream(file, true).bufferedWriter().use { out ->
            out.appendLine(password)
        }
    }
}
