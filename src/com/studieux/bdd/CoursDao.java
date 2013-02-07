package com.studieux.bdd;

import java.util.List;
import java.util.ArrayList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.SqlUtils;
import de.greenrobot.dao.Query;
import de.greenrobot.dao.QueryBuilder;

import com.studieux.bdd.Cours;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table COURS.
*/
public class CoursDao extends AbstractDao<Cours, Long> {

    public static final String TABLENAME = "COURS";

    /**
     * Properties of entity Cours.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Nom = new Property(1, String.class, "nom", false, "NOM");
        public final static Property Type = new Property(2, String.class, "type", false, "TYPE");
        public final static Property Jour = new Property(3, int.class, "jour", false, "JOUR");
        public final static Property Date_debut = new Property(4, java.util.Date.class, "date_debut", false, "DATE_DEBUT");
        public final static Property Date_fin = new Property(5, java.util.Date.class, "date_fin", false, "DATE_FIN");
        public final static Property Heure_debut = new Property(6, long.class, "heure_debut", false, "HEURE_DEBUT");
        public final static Property Heure_fin = new Property(7, long.class, "heure_fin", false, "HEURE_FIN");
        public final static Property MatiereId = new Property(8, long.class, "matiereId", false, "MATIERE_ID");
    };

    private DaoSession daoSession;

    private Query<Cours> matiere_CoursListQuery;

    public CoursDao(DaoConfig config) {
        super(config);
    }
    
    public CoursDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'COURS' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'NOM' TEXT NOT NULL ," + // 1: nom
                "'TYPE' TEXT," + // 2: type
                "'JOUR' INTEGER NOT NULL ," + // 3: jour
                "'DATE_DEBUT' INTEGER NOT NULL ," + // 4: date_debut
                "'DATE_FIN' INTEGER NOT NULL ," + // 5: date_fin
                "'HEURE_DEBUT' INTEGER NOT NULL ," + // 6: heure_debut
                "'HEURE_FIN' INTEGER NOT NULL ," + // 7: heure_fin
                "'MATIERE_ID' INTEGER NOT NULL );"); // 8: matiereId
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'COURS'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Cours entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getNom());
 
        String type = entity.getType();
        if (type != null) {
            stmt.bindString(3, type);
        }
        stmt.bindLong(4, entity.getJour());
        stmt.bindLong(5, entity.getDate_debut().getTime());
        stmt.bindLong(6, entity.getDate_fin().getTime());
        stmt.bindLong(7, entity.getHeure_debut());
        stmt.bindLong(8, entity.getHeure_fin());
        stmt.bindLong(9, entity.getMatiereId());
    }

    @Override
    protected void attachEntity(Cours entity) {
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
    public Cours readEntity(Cursor cursor, int offset) {
        Cours entity = new Cours( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // nom
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // type
            cursor.getInt(offset + 3), // jour
            new java.util.Date(cursor.getLong(offset + 4)), // date_debut
            new java.util.Date(cursor.getLong(offset + 5)), // date_fin
            cursor.getLong(offset + 6), // heure_debut
            cursor.getLong(offset + 7), // heure_fin
            cursor.getLong(offset + 8) // matiereId
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Cours entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setNom(cursor.getString(offset + 1));
        entity.setType(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setJour(cursor.getInt(offset + 3));
        entity.setDate_debut(new java.util.Date(cursor.getLong(offset + 4)));
        entity.setDate_fin(new java.util.Date(cursor.getLong(offset + 5)));
        entity.setHeure_debut(cursor.getLong(offset + 6));
        entity.setHeure_fin(cursor.getLong(offset + 7));
        entity.setMatiereId(cursor.getLong(offset + 8));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Cours entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Cours entity) {
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
    
    /** Internal query to resolve the "coursList" to-many relationship of Matiere. */
    public synchronized List<Cours> _queryMatiere_CoursList(long matiereId) {
        if (matiere_CoursListQuery == null) {
            QueryBuilder<Cours> queryBuilder = queryBuilder();
            queryBuilder.where(Properties.MatiereId.eq(matiereId));
            matiere_CoursListQuery = queryBuilder.build();
        } else {
            matiere_CoursListQuery.setParameter(0, matiereId);
        }
        return matiere_CoursListQuery.list();
    }

    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getMatiereDao().getAllColumns());
            builder.append(" FROM COURS T");
            builder.append(" LEFT JOIN MATIERE T0 ON T.'MATIERE_ID'=T0.'_id'");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected Cours loadCurrentDeep(Cursor cursor, boolean lock) {
        Cours entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        Matiere matiere = loadCurrentOther(daoSession.getMatiereDao(), cursor, offset);
         if(matiere != null) {
            entity.setMatiere(matiere);
        }

        return entity;    
    }

    public Cours loadDeep(Long key) {
        assertSinglePk();
        if (key == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(getSelectDeep());
        builder.append("WHERE ");
        SqlUtils.appendColumnsEqValue(builder, "T", getPkColumns());
        String sql = builder.toString();
        
        String[] keyArray = new String[] { key.toString() };
        Cursor cursor = db.rawQuery(sql, keyArray);
        
        try {
            boolean available = cursor.moveToFirst();
            if (!available) {
                return null;
            } else if (!cursor.isLast()) {
                throw new IllegalStateException("Expected unique result, but count was " + cursor.getCount());
            }
            return loadCurrentDeep(cursor, true);
        } finally {
            cursor.close();
        }
    }
    
    /** Reads all available rows from the given cursor and returns a list of new ImageTO objects. */
    public List<Cours> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<Cours> list = new ArrayList<Cours>(count);
        
        if (cursor.moveToFirst()) {
            if (identityScope != null) {
                identityScope.lock();
                identityScope.reserveRoom(count);
            }
            try {
                do {
                    list.add(loadCurrentDeep(cursor, false));
                } while (cursor.moveToNext());
            } finally {
                if (identityScope != null) {
                    identityScope.unlock();
                }
            }
        }
        return list;
    }
    
    protected List<Cours> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<Cours> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}