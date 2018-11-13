/*
 * Copyright (c) 2015 [1076559197@qq.com | tchen0707@gmail.com]
 *
 * Licensed under the Apache License, Version 2.0 (the "License”);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nze.nzeframework.loadcontrol;


import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.nze.nzeframework.R;


public class VaryViewHelperController {

    private IVaryViewHelper helper;

    public VaryViewHelperController(View view) {
        this(new VaryViewHelper(view));
    }

    public VaryViewHelperController(IVaryViewHelper helper) {
        super();
        this.helper = helper;
    }

    public void showNetworkError(View.OnClickListener onClickListener) {
        final View layout = helper.inflate(R.layout.common_no_net_view);
        layout.findViewById(R.id.click_reload).setOnClickListener(onClickListener);
//        layout.findViewById(R.id.click_checknetwork).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 检查网络设置
//                Intent intent = new Intent(Settings.ACTION_SETTINGS);
//                layout.getContext().startActivity(intent);
//            }
//        });

        helper.showLayout(layout);
    }

    public void showError(String errorMsg, View.OnClickListener onClickListener) {
        View layout = helper.inflate(R.layout.common_error_view);
        TextView textView = (TextView) layout.findViewById(R.id.message_info);
        if (!TextUtils.isEmpty(errorMsg)) {
            textView.setText(errorMsg);
        } else {
            textView.setText(helper.getContext().getResources().getString(R.string.common_error_msg));
        }

        ImageView imageView = (ImageView) layout.findViewById(R.id.message_icon);
        imageView.setImageResource(R.mipmap.ic_error);

        if (null != onClickListener) {
            layout.setOnClickListener(onClickListener);
        }

        helper.showLayout(layout);
    }

    public void showEmpty(String emptyMsg, int imageId) {
        View layout = helper.inflate(R.layout.common_no_data_view);
        TextView textView = (TextView) layout.findViewById(R.id.tv_empty_cev);
        if (!TextUtils.isEmpty(emptyMsg)) {
            textView.setText(emptyMsg);
        } else {
            textView.setText(helper.getContext().getResources().getString(R.string.common_empty_msg));
        }

        ImageView imageView = (ImageView) layout.findViewById(R.id.iv_empty_cev);
        if (imageId != 0)
            imageView.setImageResource(imageId);


        helper.showLayout(layout);
    }

    public void showEmpty(String emptyMsg, View.OnClickListener onClickListener, int imageId, boolean isNeedMargin) {
        View layout = helper.inflate(R.layout.common_no_data_view);
        TextView textView = (TextView) layout.findViewById(R.id.tv_empty_cev);
        if (!TextUtils.isEmpty(emptyMsg)) {
            textView.setText(emptyMsg);
        } else {
            textView.setText(helper.getContext().getResources().getString(R.string.common_empty_msg));
        }

        ImageView imageView = (ImageView) layout.findViewById(R.id.iv_empty_cev);
        imageView.setImageResource(imageId);

        if (null != onClickListener) {
            layout.setOnClickListener(onClickListener);
        }

        helper.showLayout(layout);
    }

    public void showLoading(String msg) {
        View layout = helper.inflate(R.layout.common_loading_view);
//        if (!TextUtils.isEmpty(msg)) {
//            TextView textView = (TextView) layout.findViewById(R.id.loading_msg);
//            textView.setText(msg);
//        }
        ImageView loadIv = (ImageView) layout.findViewById(R.id.loadingImageView);
        RotateAnimation mAnim = new RotateAnimation(360, 0, Animation.RESTART, 0.5f, Animation.RESTART, 0.5f);
        mAnim.setDuration(1000);
        mAnim.setRepeatCount(Animation.INFINITE);// 重复次数
        mAnim.setRepeatMode(Animation.RESTART);
        mAnim.setStartTime(Animation.START_ON_FIRST_FRAME);
        mAnim.setInterpolator(new LinearInterpolator());// 旋转不停顿
        loadIv.startAnimation(mAnim);

//        AnimationDrawable animationDrawable = (AnimationDrawable) loadIv.getBackground();
//        animationDrawable.start();

        helper.showLayout(layout);
    }

    public void restore() {
        helper.restoreView();
    }
}
