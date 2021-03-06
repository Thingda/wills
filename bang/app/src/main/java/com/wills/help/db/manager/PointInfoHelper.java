package com.wills.help.db.manager;

import com.wills.help.db.bean.PointInfo;
import com.wills.help.db.dao.PointInfoDao;

import java.util.List;

import rx.Observable;

/**
 * com.wills.help.db.manager
 * Created by lizhaoyong
 * 2017/1/6.
 */

public class PointInfoHelper implements IDBHelper<PointInfo>{

    private static PointInfoHelper pointInfoHelper;
    private PointInfoDao pointInfoDao;

    public PointInfoHelper() {
        pointInfoDao = DBManager.getInstance().getDaoSession().getPointInfoDao();
    }

    public static PointInfoHelper getInstance(){
        if (pointInfoHelper == null){
            pointInfoHelper = new PointInfoHelper();
        }
        return pointInfoHelper;
    }

    @Override
    public Observable<PointInfo> insertData(PointInfo pointInfo) {
        return pointInfoDao.rx().insertOrReplace(pointInfo);
    }

    @Override
    public Observable<Iterable<PointInfo>> insertData(List<PointInfo> list) {
        return pointInfoDao.rx().insertOrReplaceInTx(list);
    }

    @Override
    public Observable<PointInfo> saveData(PointInfo pointInfo) {
        return pointInfoDao.rx().save(pointInfo);
    }

    @Override
    public Observable<List<PointInfo>> queryAll() {
        return pointInfoDao.queryBuilder().rx().list();
    }

    @Override
    public Observable<Void> deleteData(PointInfo pointInfo) {
        return pointInfoDao.rx().delete(pointInfo);
    }

    @Override
    public Observable<Void> deleteAll() {
        return pointInfoDao.rx().deleteAll();
    }

    @Override
    public Observable<PointInfo> updateData(PointInfo pointInfo) {
        return pointInfoDao.rx().update(pointInfo);
    }

    public Observable<List<PointInfo>> queryByUserBlockId(String blockId){
        return pointInfoDao.queryBuilder().where(PointInfoDao.Properties.Blockid.eq(blockId)).rx().list();
    }

    public Observable<PointInfo> queryByPosId(String blockId){
        return pointInfoDao.queryBuilder().where(PointInfoDao.Properties.Posid.eq(blockId)).rx().unique();
    }

    public PointInfo queryById(String blockId){
        return pointInfoDao.queryBuilder().where(PointInfoDao.Properties.Posid.eq(blockId)).unique();
    }
}
