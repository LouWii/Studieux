package com.studieux.bdd;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.IdentityScopeType;

import com.studieux.bdd.Matiere;
import com.studieux.bdd.Cours;
import com.studieux.bdd.Periode;

import com.studieux.bdd.MatiereDao;
import com.studieux.bdd.CoursDao;
import com.studieux.bdd.PeriodeDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig matiereDaoConfig;
    private final DaoConfig coursDaoConfig;
    private final DaoConfig periodeDaoConfig;

    private final MatiereDao matiereDao;
    private final CoursDao coursDao;
    private final PeriodeDao periodeDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        matiereDaoConfig = daoConfigMap.get(MatiereDao.class).clone();
        matiereDaoConfig.initIdentityScope(type);

        coursDaoConfig = daoConfigMap.get(CoursDao.class).clone();
        coursDaoConfig.initIdentityScope(type);

        periodeDaoConfig = daoConfigMap.get(PeriodeDao.class).clone();
        periodeDaoConfig.initIdentityScope(type);

        matiereDao = new MatiereDao(matiereDaoConfig, this);
        coursDao = new CoursDao(coursDaoConfig, this);
        periodeDao = new PeriodeDao(periodeDaoConfig, this);

        registerDao(Matiere.class, matiereDao);
        registerDao(Cours.class, coursDao);
        registerDao(Periode.class, periodeDao);
    }
    
    public void clear() {
        matiereDaoConfig.getIdentityScope().clear();
        coursDaoConfig.getIdentityScope().clear();
        periodeDaoConfig.getIdentityScope().clear();
    }

    public MatiereDao getMatiereDao() {
        return matiereDao;
    }

    public CoursDao getCoursDao() {
        return coursDao;
    }

    public PeriodeDao getPeriodeDao() {
        return periodeDao;
    }

}