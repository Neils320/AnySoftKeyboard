/*
 * Copyright (c) 2013 Menny Even-Danan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.anysoftkeyboard.dictionaries;

import android.content.Context;

import com.anysoftkeyboard.base.dictionaries.EditableDictionary;
import com.anysoftkeyboard.dictionaries.content.ContactsDictionary;
import com.anysoftkeyboard.dictionaries.sqlite.AutoDictionary;
import com.anysoftkeyboard.utils.Logger;
import com.menny.android.anysoftkeyboard.AnyApplication;

public class DictionaryFactory {

    private static final String TAG = "ASK DictFactory";
    private AutoDictionary mAutoDictionary = null;
    private String mUserDictionaryLocale = null;
    private EditableDictionary mUserDictionary = null;

    public DictionaryFactory() {
    }

    private static boolean equalsString(String a, String b) {
        if (a == null && b == null) return true;
        else if (a == null || b == null) return false;
        else return a.equals(b);
    }

    public synchronized EditableDictionary createUserDictionary(Context context, String locale) {
        if (mUserDictionary != null) {
            if (!mUserDictionary.isClosed() && equalsString(mUserDictionaryLocale, locale)) {
                Logger.d(TAG, "Returning cached user-dictionary for locale %s", mUserDictionaryLocale);
                return mUserDictionary;
            } else {
                mUserDictionary.close();
            }
        }
        Logger.d(TAG, "Creating a new UserDictionary for locale %s", locale);
        mUserDictionary = new UserDictionary(context, locale);
        DictionaryASyncLoader.executeLoaderParallel(null, mUserDictionary);

        mUserDictionaryLocale = locale;
        return mUserDictionary;
    }

    public synchronized ContactsDictionary createContactsDictionary(Context context) {
        return new ContactsDictionary(context.getApplicationContext());
    }

    public synchronized AutoDictionary createAutoDictionary(Context context, String currentAutoDictionaryLocale) {
        if (AnyApplication.getConfig().getAutoDictionaryInsertionThreshold() < 0) return null;

        if (mAutoDictionary != null && !mAutoDictionary.isClosed()) {
            if (equalsString(mAutoDictionary.getLocale(), currentAutoDictionaryLocale)) {
                return mAutoDictionary;
            } else {
                //will create a new one shortly.
                mAutoDictionary.close();
            }
        }

        Logger.d(TAG, "Creating AutoDictionary for locale: %s", currentAutoDictionaryLocale);

        mAutoDictionary = new AutoDictionary(context, currentAutoDictionaryLocale);
        DictionaryASyncLoader.executeLoaderParallel(null, mAutoDictionary);

        return mAutoDictionary;
    }
}
