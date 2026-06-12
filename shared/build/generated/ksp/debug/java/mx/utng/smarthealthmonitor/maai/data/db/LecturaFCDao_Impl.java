package mx.utng.smarthealthmonitor.maai.data.db;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class LecturaFCDao_Impl implements LecturaFCDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<LecturaFC> __insertionAdapterOfLecturaFC;

  private final SharedSQLiteStatement __preparedStmtOfLimpiarViejos;

  public LecturaFCDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfLecturaFC = new EntityInsertionAdapter<LecturaFC>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `lecturas_fc` (`id`,`valorBpm`,`timestamp`,`hora`,`esNormal`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final LecturaFC entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getValorBpm());
        statement.bindLong(3, entity.getTimestamp());
        statement.bindString(4, entity.getHora());
        final int _tmp = entity.getEsNormal() ? 1 : 0;
        statement.bindLong(5, _tmp);
      }
    };
    this.__preparedStmtOfLimpiarViejos = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM lecturas_fc WHERE timestamp < ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertar(final LecturaFC lectura, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfLecturaFC.insert(lectura);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object limpiarViejos(final long limite, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfLimpiarViejos.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, limite);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfLimpiarViejos.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<LecturaFC>> obtenerUltimas() {
    final String _sql = "SELECT * FROM lecturas_fc ORDER BY timestamp DESC LIMIT 50";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"lecturas_fc"}, new Callable<List<LecturaFC>>() {
      @Override
      @NonNull
      public List<LecturaFC> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfValorBpm = CursorUtil.getColumnIndexOrThrow(_cursor, "valorBpm");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfHora = CursorUtil.getColumnIndexOrThrow(_cursor, "hora");
          final int _cursorIndexOfEsNormal = CursorUtil.getColumnIndexOrThrow(_cursor, "esNormal");
          final List<LecturaFC> _result = new ArrayList<LecturaFC>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final LecturaFC _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpValorBpm;
            _tmpValorBpm = _cursor.getInt(_cursorIndexOfValorBpm);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpHora;
            _tmpHora = _cursor.getString(_cursorIndexOfHora);
            final boolean _tmpEsNormal;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfEsNormal);
            _tmpEsNormal = _tmp != 0;
            _item = new LecturaFC(_tmpId,_tmpValorBpm,_tmpTimestamp,_tmpHora,_tmpEsNormal);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object contarRegistros(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM lecturas_fc";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
