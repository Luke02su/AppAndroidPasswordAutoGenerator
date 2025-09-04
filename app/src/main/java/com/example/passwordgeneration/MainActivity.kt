package com.example.passwordgeneration

import android.os.Bundle
import android.os.Environment
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
import java.security.SecureRandom

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

        buttonRefresh.setOnClickListener {
            textPassword.text = generatePassword(seekBar.progress, switches)
        }

        buttonCopy.setOnClickListener {
            val password = textPassword.text.toString()
            if (password.isNotBlank() && password != "Selecione os parâmetros!") {
                val clipboard =
                    getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
                val clip = android.content.ClipData.newPlainText("password", password)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(this, "Senha copiada!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Nenhuma senha para copiar!", Toast.LENGTH_SHORT).show()
            }
        }

        buttonSave.setOnClickListener {
            val password = textPassword.text.toString()
            if (password.isNotBlank() && password != "Selecione os parâmetros!") {

                val downloadsDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val folder = File(downloadsDir, "Senhas")
                if (!folder.exists()) folder.mkdirs()

                val file = File(folder, "senha.txt")
                FileOutputStream(file, true).bufferedWriter().use { out ->
                    out.appendLine(password)
                }

                Toast.makeText(this, "Arquivo salvo em: ${file.absolutePath}", Toast.LENGTH_LONG)
                    .show()

            } else {
                Toast.makeText(this, "Nenhuma senha para salvar!", Toast.LENGTH_SHORT).show()
            }
        }
    }

        private fun generatePassword(length: Int, switches: List<Pair<Switch, String>>): String {
            val sets = switches.filter { it.first.isChecked }
                .map { (_, chars) ->
                    chars.codePoints().mapToObj { cp -> String(Character.toChars(cp)) }.toList()
                }

            if (sets.isEmpty()) return "Selecione os parâmetros!"

            val size = length.coerceAtLeast(4)
            val secureRandom = SecureRandom()

            val mandatory = sets.map { it[secureRandom.nextInt(it.size)] }

            val pool = sets.flatten()
            val remaining = (mandatory.size until size).map { pool[secureRandom.nextInt(pool.size)] }

            return (mandatory + remaining).shuffled(secureRandom).joinToString("")
        }
    }