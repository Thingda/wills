package com.wills.help.release.presenter;

import java.util.Map;

/**
 * com.wills.help.release.presenter
 * Created by lizhaoyong
 * 2017/1/10.
 */

public interface ReleaseListPresenter {
    void getReleaseList(Map<String , String > map);
    void confirm(Map<String , String > map,int from);//from 1:activity 2：fragment，便于进度跳
    void exec(Map<String , String > map,int from);//from 1:activity 2：fragment，便于进度跳
}
