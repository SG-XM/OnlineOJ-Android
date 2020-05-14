package com.twt.zq.commons.util;

import android.content.Context;

/**
 * http://git.oschina.net/tangbuzhi
 *
 * @author Tangbuzhi
 * @version V1.0
 * @Package com.tang.customview
 * @Description: ${TODO}
 * @date: 2017/8/24
 */


public class DensityUtil {
    public static int dp2px(Context context, float dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }

    public static int px2dp(Context context, int px) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (px / density + 0.5f);
    }
}
