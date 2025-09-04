package com.example.passwordgeneration

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
            findViewById<Switch>(R.id.switchSpecialC) to "!@#/$%&*?+-_=<>,.;:()[]{}´`'|^~°¨¶¿¡",
            findViewById<Switch>(R.id.switchEmoji) to "😀😁😂🤣😅😊😍😘😎🤩🥳😡😭😴😇🤔🙄😱😜🤪"
        )

        // Atualiza contador ao mover SeekBar
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                val length = progress.coerceAtLeast(4)
                textLengthQtd.text = length.toString()

                if (length != progress) {
                    sb?.progress = length
                }
            }

            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })

        // Gera senha ao clicar no botão Refresh
        buttonRefresh.setOnClickListener {
            textPassword.text = generatePassword(seekBar.progress, switches)
        }

        // Copia a senha para área de transferência ao clicar no botão Copy
        buttonCopy.setOnClickListener {
            val password = textPassword.text.toString()
            if (password.isNotBlank() && password != "Selecione os parâmetros!") {
                val clipboard = getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
                val clip = android.content.ClipData.newPlainText("password", password)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(this, "Senha copiada!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Nenhuma senha para copiar!", Toast.LENGTH_SHORT).show()
            }
        }

        // Salva senha em arquivo ao clicar em "SAVE PASSWORD"
        buttonSave.setOnClickListener {
            val password = textPassword.text.toString()
            if (password.isNotBlank() && password != "Selecione os parâmetros!") {
                savePasswordToFile(password)
                val caminho = "/storage/Download/Senhas/senha.txt"
                Toast.makeText(this, "Arquivo salvo em: " + caminho, Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Nenhuma senha para salvar!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Função para gerar senha (corrigida para emojis)
        private fun generatePassword(length: Int, switches: List<Pair<Switch, String>>): String {
            val pool = switches.filter { it.first.isChecked }
                .flatMap { (_, chars) ->
                    chars.codePoints().toArray().map { cp -> String(Character.toChars(cp)) }
                }

            val size = length.coerceAtLeast(4)
            return if (pool.isNotEmpty()) {
                (1..size).map { pool.random() }.joinToString("")
            } else "Selecione os parâmetros!"
        }


    // Função para salvar senha em arquivo interno
    private fun savePasswordToFile(password: String) {
        val file = File(filesDir, "senha.txt")
        FileOutputStream(file, true).bufferedWriter().use { out ->
            out.appendLine(password)
        }
    }
}
