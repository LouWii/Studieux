package com.studieux.bdd;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.Property;

import com.studieux.bdd.Periode;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table PERIODE.
*/
public class PeriodeDao extends AbstractDao<Periode, Long> {

    public static final String TABLENAME = "PERIODE";

    /**
     * Properties of entity Periode.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Nom = new Property(1, String.class, "nom", false, "NOM");
        public final static Property Date_debut = new Property(2, java.util.Date.class, "date_debut", false, "DATE_DEBUT");
        public final static Property Date_fin = new Property(3, java.util.Date.class, "date_fin", false, "DATE_FIN");
    };

    private DaoSession daoSession;


    public PeriodeDao(DaoConfig config) {
        super(config);
    }
    
    public PeriodeDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'PERIODE' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'NOM' TEXT NOT NULL ," + // 1: nom
                "'DATE_DEBUT' INTEGER NOT NULL ," + // 2: date_debut
                "'DATE_FIN' INTEGER NOT NULL );"); // 3: date_fin
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'PERIODE'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Periode entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getNom());
        stmt.bindLong(3, entity.getDate_debut().getTime());
        stmt.bindLong(4, entity.getDate_fin().getTime());
    }

    @Override
    protected void attachEntity(Periode entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Periode readEntity(Cursor cursor, int offset) {
        Periode entity = new Periode( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // nom
            new java.util.Date(cursor.getLong(offset + 2)), // date_debut
            new java.util.Date(cursor.getLong(offset + 3)) // date_fin
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Periode entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setNom(cursor.getString(offset + 1));
        entity.setDate_debut(new java.util.Date(cursor.getLong(offset + 2)));
        entity.setDate_fin(new java.util.Date(cursor.getLong(offset + 3)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Periode entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Periode entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}