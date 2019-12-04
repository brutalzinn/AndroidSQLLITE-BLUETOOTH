package info.androidhive.sqlite.database.model;

/**
 * Created by ravi on 20/02/18.
 */

public class Note {
    public static final String TABLE_NAME = "alarmes";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NOTE = "note";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_ISENABLED= "isenabled";
    public static final String COLUMN_RESPONSE = "response";
    public static final String COLUMN_TIMER = "timer";
    public static final String COLUMN_TYPE = "type";
    private int id;
    private String note;
    private String timestamp;
    private int isenabled;
    private String response;
    private String timer;
    private int type;
    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_NOTE + " TEXT,"
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                    + COLUMN_ISENABLED + " INTEGER,"
                    + COLUMN_RESPONSE + " TEXT,"
                    + COLUMN_TIMER + " TEXT,"
                    + COLUMN_TYPE + " INTEGER"
                    + ")";

    public Note() {
    }

    public Note(int id, String note, String timestamp, int isenabled, String response, String timer, int type) {
        this.id = id;
        this.note = note;
        this.timestamp = timestamp;
        this.isenabled = isenabled;
        this.response = response;
this.timer = timer;
this.type = type;
    }

    public int getId() {
        return id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }


    public int getisEnabled() {
        return isenabled;
    }

    public void setisEnabled(int isenabled) {
        this.isenabled = isenabled;
    }
//timer
public String getTimer() {
    return timer;
}

    public void setTimer(String timer) {
        this.timer = timer;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    //end timer
    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
