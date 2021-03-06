package com.hunter.dribbble.utils;

import android.text.TextUtils;

import com.hunter.dribbble.entity.ImagesEntity;

public class CheckImageUrlUtils {

    public static String checkImageUrl(ImagesEntity entity) {
        if (!TextUtils.isEmpty(entity.getHidpi())) {
            return entity.getHidpi();
        }
        if (!TextUtils.isEmpty(entity.getNormal())) {
            return entity.getNormal();
        }
        if (!TextUtils.isEmpty(entity.getTeaser())) {
            return entity.getTeaser();
        }

        return "";
    }
}
