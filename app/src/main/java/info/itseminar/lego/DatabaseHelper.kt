package info.itseminar.lego

import android.app.DownloadManager.*
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import info.itseminar.lego.Model.User

val DATABASE_NAME = "trainDB"
val TABLE_NAME = "Users"
val COL_USERNAME = "username"
val COL_PASSWORD = "password"
val COL_ID = "id"

class DatabaseHelper(var context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {

        val createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID +" INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_USERNAME + " VARCHAR(256)," +
                COL_PASSWORD + " VARCHAR(256))";
    db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun createUser(user : User){
        val db = this.writableDatabase
        var cv = ContentValues()
        cv.put(COL_USERNAME, user.userName)
        cv.put(COL_PASSWORD, user.password)
        var result = db.insert(TABLE_NAME, null, cv)
        if (result == -1.toLong())
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        else
            Toast.makeText(context, "SUCCESS", Toast.LENGTH_SHORT).show()
    }
    fun validateUser(user : User): User?{
        val db = this.readableDatabase
        val query = "Select * from " + TABLE_NAME + " WHERE " + COL_USERNAME + " ='" + user.userName + "'" +  " AND " +
                COL_PASSWORD + "='" + user.password + "'"
        val result = db.rawQuery(query,null)
        var user: User? = null
        if (result.moveToFirst()) {
            do{
                user = User()
                user.id = result.getString(0).toInt()
                user.userName = result.getString(1)
                user.password = result.getString(2)
            } while (result.moveToNext())
        } else {
            var user = null
            Toast.makeText(context, "User not found.", Toast.LENGTH_SHORT).show()
        }
        return user
    }
}