package ir.batna.neda.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(indices = {@Index(value = {"package_name", "signature", "token"})})
public class ClientApp {

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "package_name")
    @NotNull
    public String packageName;

    @ColumnInfo(name = "signature")
    public String signature;

    @ColumnInfo(name = "token")
    public String token;

    @ColumnInfo(name = "status")
    public String status;

    @ColumnInfo(name = "date_app_installed")
    public String dateInstalled;

    @ColumnInfo(name = "date_record_created")
    public String dateCreated;

    @ColumnInfo(name = "date_record_modified")
    public String dateModified;

    public ClientApp(String packageName, String signature, String token, String status, String dateInstalled, String dateCreated, String dateModified) {

        this.packageName = packageName;
        this.signature = signature;
        this.token = token;
        this.status = status;
        this.dateInstalled = dateInstalled;
        this.dateCreated = dateCreated;
        this.dateModified = dateModified;
    }
}
