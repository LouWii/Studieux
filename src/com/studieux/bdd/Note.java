package com.studieux.bdd;

import com.studieux.bdd.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table NOTE.
 */
public class Note {

    private Long id;
    private String description;
    private Float value;
    private Integer quotient;
    private Float coef;
    private Long devoirId;
    private Long matiereId;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient NoteDao myDao;

    private Devoir devoir;
    private Long devoir__resolvedKey;

    private Matiere matiere;
    private Long matiere__resolvedKey;


    public Note() {
    }

    public Note(Long id) {
        this.id = id;
    }

    public Note(Long id, String description, Float value, Integer quotient, Float coef, Long devoirId, Long matiereId) {
        this.id = id;
        this.description = description;
        this.value = value;
        this.quotient = quotient;
        this.coef = coef;
        this.devoirId = devoirId;
        this.matiereId = matiereId;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getNoteDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    public Integer getQuotient() {
        return quotient;
    }

    public void setQuotient(Integer quotient) {
        this.quotient = quotient;
    }

    public Float getCoef() {
        return coef;
    }

    public void setCoef(Float coef) {
        this.coef = coef;
    }

    public Long getDevoirId() {
        return devoirId;
    }

    public void setDevoirId(Long devoirId) {
        this.devoirId = devoirId;
    }

    public Long getMatiereId() {
        return matiereId;
    }

    public void setMatiereId(Long matiereId) {
        this.matiereId = matiereId;
    }

    /** To-one relationship, resolved on first access. */
    public Devoir getDevoir() {
        if (devoir__resolvedKey == null || !devoir__resolvedKey.equals(devoirId)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DevoirDao targetDao = daoSession.getDevoirDao();
            devoir = targetDao.load(devoirId);
            devoir__resolvedKey = devoirId;
        }
        return devoir;
    }

    public void setDevoir(Devoir devoir) {
        this.devoir = devoir;
        devoirId = devoir == null ? null : devoir.getId();
        devoir__resolvedKey = devoirId;
    }

    /** To-one relationship, resolved on first access. */
    public Matiere getMatiere() {
        if (matiere__resolvedKey == null || !matiere__resolvedKey.equals(matiereId)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MatiereDao targetDao = daoSession.getMatiereDao();
            matiere = targetDao.load(matiereId);
            matiere__resolvedKey = matiereId;
        }
        return matiere;
    }

    public void setMatiere(Matiere matiere) {
        this.matiere = matiere;
        matiereId = matiere == null ? null : matiere.getId();
        matiere__resolvedKey = matiereId;
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
