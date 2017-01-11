package com.wills.help.release.model;

import com.wills.help.net.HttpManager;

import java.util.Map;

import rx.Observable;

/**
 * com.wills.help.release.model
 * Created by lizhaoyong
 * 2017/1/10.
 */

public class ReleaseListModel {
    public Observable<ReleaseList> getReleaseList(Map<String , String > map){
        return HttpManager.getApiInterface().getReleaseList(map);
    }
}