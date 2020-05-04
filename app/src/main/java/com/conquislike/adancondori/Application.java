/*
 *  Copyright 2017 Rozdoum
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.conquislike.adancondori;

import android.Manifest;
import android.widget.Toast;

import com.conquislike.adancondori.main.interactors.PostInteractor;
import com.conquislike.adancondori.utils.Utils;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class Application extends android.app.Application {

    public static final String TAG = Application.class.getSimpleName();
    private static final String[] STORAGE =
            {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};

    private static final int RC_WRITE_STORAGE_PERM = 1230;
    private static final int RC_READ_STORAGE_PERM = 124;

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.printHashKey(this);
        ApplicationHelper.initDatabaseHelper(this);
        PostInteractor.getInstance(this).subscribeToNewPosts();
    }
}
