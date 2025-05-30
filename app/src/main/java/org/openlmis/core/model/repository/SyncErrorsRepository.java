package org.openlmis.core.model.repository;

import android.content.Context;

import com.google.inject.Inject;
import com.j256.ormlite.dao.Dao;

import org.openlmis.core.exceptions.LMISException;
import org.openlmis.core.model.SyncError;
import org.openlmis.core.model.SyncType;
import org.openlmis.core.persistence.DbUtil;
import org.openlmis.core.persistence.GenericDao;

import java.sql.SQLException;
import java.util.List;

public class SyncErrorsRepository {

    final Context context;
    GenericDao<SyncError> genericDao;

    @Inject
    DbUtil dbUtil;

    @Inject
    public SyncErrorsRepository(Context context) {
        genericDao = new GenericDao<>(SyncError.class, context);
        this.context = context;
    }

    public void save(final SyncError syncError) {
        try {
            genericDao.createOrUpdate(syncError);
        } catch (LMISException e) {
            e.reportToFabric();
        }
    }

    public void delete(SyncError syncError) {
        try {
            genericDao.delete(syncError);
        } catch (LMISException e) {
            e.reportToFabric();
        }
    }

    public List<SyncError> getBySyncTypeAndObjectId(final SyncType syncType, final long syncObjectId) {
        try {
            return dbUtil.withDao(SyncError.class, new DbUtil.Operation<SyncError, List<SyncError>>() {

                @Override
                public List<SyncError> operate(Dao<SyncError, String> dao) throws SQLException {
                    return dao.queryBuilder().where().eq("syncType", syncType).and().eq("syncObjectId", syncObjectId).query();
                }
            });
        } catch (LMISException e) {
            e.reportToFabric();
            return null;
        }
    }

    public Integer deleteBySyncTypeAndObjectId(final SyncType syncType, final long syncObjectId) {
        try {
            return dbUtil.withDao(SyncError.class, new DbUtil.Operation<SyncError, Integer>() {

                @Override
                public Integer operate(Dao<SyncError, String> dao) throws SQLException {
                    return dao.delete(dao.queryBuilder().orderBy("id", false).where().eq("syncType", syncType).and().eq("syncObjectId", syncObjectId).query());
                }
            });
        } catch (LMISException e) {
            e.reportToFabric();
            return null;
        }
    }

    public boolean hasSyncErrorOf(final SyncType syncType) throws LMISException {
        SyncError syncError = dbUtil.withDao(SyncError.class, new DbUtil.Operation<SyncError, SyncError>() {
            @Override
            public SyncError operate(Dao<SyncError, String> dao) throws SQLException {
                return dao.queryBuilder().where().eq("syncType", syncType).queryForFirst();
            }
        });
        return syncError != null;
    }
}
