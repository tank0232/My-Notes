package com.example.noteapp

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DialogTitle
import android.view.*
import android.widget.BaseAdapter
import android.widget.SearchView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.ticket.view.*

class MainActivity : AppCompatActivity() {

    var listNotes=ArrayList<Note>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Add Dummy Data
        listNotes.add(Note(1,"Charmeleon","Charmeleon mercilessly destroys its foes using its sharp claws. If it encounters a strong foe, it turns aggressive. In this excited state, the flame at the tip of its tail flares with a bluish white color."))
        listNotes.add(Note(1,"Squirtle","Squirtle's shell is not merely used for protection. The shell's rounded shape and the grooves on its surface help minimize resistance in water, enabling this Pokémon to swim at high speeds."))
        listNotes.add(Note(1,"Caterpie","Caterpie has a voracious appetite. It can devour leaves bigger than its body right before your eyes. From its antenna, this Pokémon releases a terrifically strong odor."))
        listNotes.add(Note(1,"Pikachu","Whenever Pikachu comes across something new, it blasts it with a jolt of electricity. If you come across a blackened berry, it's evidence that this Pokémon mistook the intensity of its charge."))
        listNotes.add(Note(1,"Clefairy","On every night of a full moon, groups of this Pokémon come out to play. When dawn arrives, the tired Clefairy return to their quiet mountain retreats and go to sleep nestled up against each other."))

       var myNotesAdapter= MyNotesAdapter(this,listNotes)

       //Toast.makeText(this, "onCreate", Toast.LENGTH_LONG).show()
        //Load from DB

        LoadQuery("%")
    }

    override fun onResume() {
        super.onResume()
        LoadQuery("%")
        //Toast.makeText(this, "onResume", Toast.LENGTH_LONG).show()

    }

     override fun onStart(){
         super.onStart()
        // Toast.makeText(this, "onStart", Toast.LENGTH_LONG).show()

     }

    override fun onPause() {
        super.onPause()
        //Toast.makeText(this, "onPause", Toast.LENGTH_LONG).show()

    }

    override fun onStop() {
        super.onStop()
        //Toast.makeText(this, "onStop", Toast.LENGTH_LONG).show()

    }

    override fun onDestroy() {
        super.onDestroy()
        //Toast.makeText(this, "onDestroy", Toast.LENGTH_LONG).show()

    }

    override fun onRestart() {
        super.onRestart()
        //Toast.makeText(this, "onRestart", Toast.LENGTH_LONG).show()

    }
    fun LoadQuery(title: String){

        var dbManager=DbManager(this)
        val projections= arrayOf("ID","Title","Description")
        val selectionArgs = arrayOf(title)
        val cursor=dbManager.Query(projections, "Title like ?",selectionArgs,"Title")

        listNotes.clear()
        if(cursor.moveToFirst()){
            do{
                val ID=cursor.getInt(cursor.getColumnIndex("ID"))
                val Title =cursor.getString(cursor.getColumnIndex("Title"))
                val Description =cursor.getString(cursor.getColumnIndex("Description"))
                listNotes.add(Note(ID,Title, Description))

            }while (cursor.moveToNext())
        }

        var myNotesAdapter=MyNotesAdapter(this,listNotes)
        lvNotes.adapter=myNotesAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu,menu)
        val sv=menu!!.findItem(R.id.app_bar_search).actionView as SearchView
        val sm=getSystemService(Context.SEARCH_SERVICE) as SearchManager
        sv.setSearchableInfo(sm.getSearchableInfo(componentName))
        sv.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                Toast.makeText(applicationContext, query, Toast.LENGTH_LONG).show()
                LoadQuery("%"+query+"%")
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
     if(item != null) {
         when (item.itemId) {
             R.id.addNote -> {
                  var intent = Intent(this, AddNotes::class.java)
                 startActivity(intent)
             }
         }
     }
        return super.onOptionsItemSelected(item)
    }

    inner class MyNotesAdapter:BaseAdapter{

        var listNotesAdapter = ArrayList<Note>()
        var context:Context?=null
        constructor(context: Context,listNotes:ArrayList<Note>):super(){
            this.listNotesAdapter=listNotes
            this.context = context

        }
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

          var myView=layoutInflater.inflate(R.layout.ticket,null)
            var myNode=listNotesAdapter[position]
            myView.tvTitle.text=myNode.nodeName
            myView.tvDes.text=myNode.nodeDes
            myView.ivDelete.setOnClickListener(View.OnClickListener {

                var dbManager=DbManager(this.context!!)
                val selectionArgs = arrayOf(myNode.nodeID.toString())
                dbManager.Delete("ID=?",selectionArgs)
                LoadQuery("%")
            })
            myView.ivEdit.setOnClickListener(View.OnClickListener {
               GoToUpdate(myNode)
            })

            return myView
        }

        override fun getItem(position: Int): Any {
          return listNotesAdapter[position]
        }

        override fun getItemId(position: Int): Long {
         return position.toLong()
        }

        override fun getCount(): Int {
            return listNotesAdapter.size

         }


    }

    fun GoToUpdate(note:Note){
        var intent= Intent(this,AddNotes::class.java)
        intent.putExtra("ID", note.nodeID)
        intent.putExtra("name", note.nodeName)
        intent.putExtra("des", note.nodeDes)
        startActivity(intent)


    }
}
