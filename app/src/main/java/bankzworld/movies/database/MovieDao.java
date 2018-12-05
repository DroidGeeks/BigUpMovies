package bankzworld.movies.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import bankzworld.movies.pojo.Results;


@Dao
public interface MovieDao {

    // inserts into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovie(Results results);

    // gets all items from the database
    @Query("SELECT * FROM Results ORDER BY id")
    LiveData<List<Results>> retrieveMovies();

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateMovies(Results results);

    // deletes an item from the database
    @Delete
    void removeMovie(Results results);

    // selects a particular item that needs to be updated
    @Query("SELECT * FROM Results WHERE id = :id")
    List<Results> retrieveMovieById(Integer id);
}

