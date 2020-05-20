package ir.batna.neda.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ClientAppDao {

    @Query("SELECT * FROM ClientApp")
    List<ClientApp> getAll();

    @Query("SELECT * FROM ClientApp")
    List<ClientApp> loadAllData();

    @Query("SELECT * FROM ClientApp WHERE status LIKE :status")
    List<ClientApp> loadAllDataByStatus(String status);

    @Query("SELECT * FROM ClientApp WHERE package_name LIKE :packageName AND " +
            "signature LIKE :signature")
    ClientApp findByPackageName(String packageName, String signature);

    @Query("SELECT * FROM ClientApp WHERE token LIKE :token LIMIT 1")
    ClientApp findByToken(String token);

    @Insert
    void insert(ClientApp clientApp);

    @Query("UPDATE ClientApp SET " +
            "status = :status, " +
            "date_modified = :dateModified " +
            "WHERE package_name = :packageName AND signature =:signature")
    void update(String packageName, String signature, String status, String dateModified);

    @Query("UPDATE ClientApp SET " +
            "status = :status, " +
            "date_modified = :date_modified " +
            "WHERE token = :token")
    void updateByToken(String token, String status, String date_modified);


    @Query("Delete FROM ClientApp WHERE package_name = :packageName AND " +
            "signature = :signature")
    void delete(String packageName, String signature);


}
