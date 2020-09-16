package com.edu.uan.android.todolist

import android.app.Activity
import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener, AdapterView.OnItemClickListener,
    AdapterView.OnItemLongClickListener {
    private var tarea: EditText? = null
    private var boton: Button? = null
    private var lista: ListView? = null

    private var añadir = ""

    private var listaEdit: Array<String>? = null

    private var num = 1000
    private var count = 1

    private var pos = 0
    private var action = "insert"

    private var vibrator: Vibrator? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tarea = findViewById(R.id.agregar)

        lista = findViewById(R.id.lista1)
        lista?.onItemClickListener = this
        lista?.onItemLongClickListener = this

        boton = findViewById(R.id.button)
        boton?.setOnClickListener(this)

        listaEdit = Array(num, { "" })

        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator?
        val f = File(filesDir, "texto.txt")
        f.createNewFile()
        leerArchivo()

    }

    private fun tareasOperacion() {

        when (action) {
            "insert" -> {
                añadirTareas()

            }
            "update" -> {
                actualizarDatos()
            }
        }
    }


    private fun añadirTareas() {
/*   Log.d("tag", "${filesDir}")*/

        var n = 0
        añadir = tarea?.text.toString()
        if (agregar?.text.toString() != "") {
            var tareas: Array<String>
            for (i in n.until(num - 1)) {
                if (listaEdit?.get(i) == "") {
                    listaEdit?.set(i, añadir)

                    tareas = Array(count, { "" })
                    for (j in 0..i) {
                        tareas[j] = listaEdit?.get(j) as String
                    }
                    val adapter =
                        ArrayAdapter(this, android.R.layout.simple_list_item_1, tareas)
                    lista!!.adapter = adapter
                    count++
                    //aca se guarda lo que escribimos
                    guardarTareas(agregar?.text.toString())

                    break
                    //adapter.notifyDataSetChanged()
                }

            }

            tarea!!.setText("")
        } else {
            Toast.makeText(this, "Por favor ingresa una tarea", Toast.LENGTH_SHORT).show()
        }
    }


    private fun actualizarDatos() {
        count = 1
        var n = 0
        for (i in n.until(num - 1)) {
            if (listaEdit?.get(i) != "") {
                if (pos === i) {
                    listaEdit?.set(i, tarea?.text.toString())
                }
                val listName = Array(count, { "" })
                for (j in n.until(count)) {
                    listName?.set(j, listaEdit?.get(j) as String)
                }
                count++
                val adapter =
                    ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listName)
                lista!!.adapter = adapter
            }
        }
        tarea!!.setText("")
        action = "insert"
    }

    override fun onClick(p0: View?) {
        tareasOperacion()
    }

    //un clic para editar el texto
    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        tarea?.setText(listaEdit?.get(p2))
        pos = p2
        action = "update"
    }

    //Eliminar manteniendo la lista prolongada
    override fun onItemLongClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createOneShot(8, 60))
            alertDialog(p2)
        }
        return true
    }

    private fun alertDialog(i: Int) {
        val alert = AlertDialog.Builder(this)
        alert.setIcon(R.mipmap.ic_pdhn)
            .setTitle(R.string.app_alertTitle)
            .setPositiveButton("Eliminar") { dialog, which ->
                listaEdit?.set(i, "")
                eliminar()
                //mostrar()
                Toast.makeText(this, "Eliminando...", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar") { dialog, which -> }
            .show()
    }

    private fun mostrar() {
        count = 1
        for (i in 0 until num) {
            if (listaEdit!![i] != "") {
                val listName = arrayOfNulls<String>(count)
                for (j in 0 until count) {
                    listName[j] = listaEdit!![i]
                }
                count++
                val adapter =
                    ArrayAdapter(this, android.R.layout.simple_list_item_1, listName)
                lista!!.adapter = adapter
                adapter.notifyDataSetChanged()

            }

        }
    }

    ///////////Aca vamos a aplicar los txt
    //aca comienza leer con txt
    private fun guardarTareas(tarea: String?) {
        //escribir las tareas en el archivo
        if (tarea != null) {
            val out = PrintStream(openFileOutput("texto.txt", Activity.MODE_APPEND))
            out.println(tarea)
            out.close()

        }

    }

    private fun leerArchivo() {

        //leemos el archivo
        val entrada = Scanner(openFileInput("texto.txt"))

        val vistaLista: MutableList<String> = mutableListOf()
        while (entrada.hasNextLine()) {
            val l = entrada.nextLine()
            vistaLista.add(l)
        }
        entrada.close()
        //agregamos el contenido a la vista de la lista
        val adapter =
            ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, vistaLista)
        lista?.adapter = adapter
        vistaLista.joinToString(vistaLista.add(tarea?.text.toString()).toString())
        adapter.notifyDataSetChanged()

    }

    private fun eliminar() {
        
        var fichero = File("texto.txt")
        val vistaLista: MutableList<String> = mutableListOf()
        /* if (fichero.delete())
             System.out.println("El fichero ha sido borrado satisfactoriamente");
         else
             System.out.println("El fichero no puede ser borrado");*/
        //leemos el archivo
        var line = ""
        var entrada = Scanner(openFileInput("texto.txt"))


        while (entrada.hasNextLine()) {
            val l = entrada.nextLine()
            vistaLista.remove(fichero.delete())

        }
        entrada.close()

        //vistaLista.joinToString(" ")
        //agregamos el contenido a la vista de la lista
        val adapter =
            ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, vistaLista)
        lista?.adapter = adapter
        adapter.notifyDataSetChanged()
    }
}