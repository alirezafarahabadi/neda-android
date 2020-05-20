package ir.batna.neda.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {ClientApp.class}, version = 1)
public abstract class ClientAppDatabase extends RoomDatabase {

    private static ClientAppDatabase database;
    private static final String DATABASE_NAME = "Neda.db";

    public abstract ClientAppDao clientAppDao();

    public static ClientAppDatabase getDatabase(final Context context) {

        if (database == null) {
            synchronized (ClientAppDatabase.class) {
                database = Room.databaseBuilder(context, ClientAppDatabase.class, DATABASE_NAME).build();
            }
        }
        return database;
    }
}
