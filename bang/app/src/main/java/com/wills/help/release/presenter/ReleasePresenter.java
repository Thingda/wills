package com.wills.help.release.presenter;

import java.util.Map;

/**
 * com.wills.help.release.presenter
 * Created by lizhaoyong
 * 2017/1/10.
 */

public interface ReleasePresenter {
    void release(Map<String,String> map , int from);
    void updateOrder(Map<String,String> map);
    void cancelOrder(Map<String,String> map);
}
