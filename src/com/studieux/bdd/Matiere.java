package com.studieux.bdd;

import java.util.List;
import com.studieux.bdd.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table MATIERE.
 */
public class Matiere {

    private Long id;
    /** Not-null value. */
    private String nom;
    private Float moyenne;
    private float coef;
    private Long cours_ID;
    private long periodeID;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient MatiereDao myDao;

    private Periode periode;
    private Long periode__resolvedKey;

    private List<Cours> coursList;
    private List<Devoir> devoirList;
    private List<Note> noteList;

    public Matiere() {
    }

    public Matiere(Long id) {
        this.id = id;
    }

    public Matiere(Long id, String nom, Float moyenne, float coef, Long cours_ID, long periodeID) {
        this.id = id;
        this.nom = nom;
        this.moyenne = moyenne;
        this.coef = coef;
        this.cours_ID = cours_ID;
        this.periodeID = periodeID;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getMatiereDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /** Not-null value. */
    public String getNom() {
        return nom;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setNom(String nom) {
        this.nom = nom;
    }

    public Float getMoyenne() {
        return moyenne;
    }

    public void setMoyenne(Float moyenne) {
        this.moyenne = moyenne;
    }

    public float getCoef() {
        return coef;
    }

    public void setCoef(float coef) {
        this.coef = coef;
    }

    public Long getCours_ID() {
        return cours_ID;
    }

    public void setCours_ID(Long cours_ID) {
        this.cours_ID = cours_ID;
    }

    public long getPeriodeID() {
        return periodeID;
    }

    public void setPeriodeID(long periodeID) {
        this.periodeID = periodeID;
    }

    /** To-one relationship, resolved on first access. */
    public Periode getPeriode() {
        if (periode__resolvedKey == null || !periode__resolvedKey.equals(periodeID)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            PeriodeDao targetDao = daoSession.getPeriodeDao();
            periode = targetDao.load(periodeID);
            periode__resolvedKey = periodeID;
        }
        return periode;
    }

    public void setPeriode(Periode periode) {
        if (periode == null) {
            throw new DaoException("To-one property 'periodeID' has not-null constraint; cannot set to-one to null");
        }
        this.periode = periode;
        periodeID = periode.getId();
        periode__resolvedKey = periodeID;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public synchronized List<Cours> getCoursList() {
        if (coursList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            CoursDao targetDao = daoSession.getCoursDao();
            coursList = targetDao._queryMatiere_CoursList(id);
        }
        return coursList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetCoursList() {
        coursList = null;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public synchronized List<Devoir> getDevoirList() {
        if (devoirList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DevoirDao targetDao = daoSession.getDevoirDao();
            devoirList = targetDao._queryMatiere_DevoirList(id);
        }
        return devoirList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetDevoirList() {
        devoirList = null;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public synchronized List<Note> getNoteList() {
        if (noteList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            NoteDao targetDao = daoSession.getNoteDao();
            noteList = targetDao._queryMatiere_NoteList(id);
        }
        return noteList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetNoteList() {
        noteList = null;
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

}
