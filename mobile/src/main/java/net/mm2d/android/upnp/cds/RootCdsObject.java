/*
 * Copyright (c) 2018 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.android.upnp.cds;

import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;
import java.util.List;

/**
 * CdsObjectのroot要素
 *
 * @author [大前良介 (OHMAE Ryosuke)](mailto:ryo@mm2d.net)
 */
public class RootCdsObject implements CdsObject {
    private final String mUdn;

    public RootCdsObject(@NonNull final String udn) {
        mUdn = udn;
    }

    @NonNull
    @Override
    public String getUdn() {
        return mUdn;
    }

    @Override
    public boolean isContainer() {
        return true;
    }

    @Override
    public boolean isItem() {
        return false;
    }

    @Override
    public int getType() {
        return TYPE_CONTAINER;
    }

    @NonNull
    @Override
    public String getObjectId() {
        return "0";
    }

    @NonNull
    @Override
    public String getParentId() {
        return "-1";
    }

    @NonNull
    @Override
    public String getUpnpClass() {
        return "object.container";
    }

    @NonNull
    @Override
    public String getTitle() {
        return "";
    }

    @Nullable
    @Override
    public String getValue(@NonNull final String xpath) {
        return null;
    }

    @Nullable
    @Override
    public String getValue(
            @NonNull final String xpath,
            final int index) {
        return null;
    }

    @Nullable
    @Override
    public String getValue(
            @Nullable final String tagName,
            @Nullable final String attrName) {
        return null;
    }

    @Nullable
    @Override
    public String getValue(
            @Nullable final String tagName,
            @Nullable final String attrName,
            final int index) {
        return null;
    }

    @Nullable
    @Override
    public Tag getTag(@Nullable final String tagName) {
        return null;
    }

    @Nullable
    @Override
    public Tag getTag(
            @Nullable final String tagName,
            final int index) {
        return null;
    }

    @NonNull
    @Override
    public Tag getRootTag() {
        return Tag.EMPTY;
    }

    @NonNull
    @Override
    public TagMap getTagMap() {
        return TagMap.EMPTY;
    }

    @Nullable
    @Override
    public List<Tag> getTagList(@Nullable final String tagName) {
        return null;
    }

    @Override
    public int getIntValue(
            @NonNull final String xpath,
            final int defaultValue) {
        return 0;
    }

    @Override
    public int getIntValue(
            @NonNull final String xpath,
            final int index,
            final int defaultValue) {
        return 0;
    }

    @Nullable
    @Override
    public Date getDateValue(@NonNull final String xpath) {
        return null;
    }

    @Nullable
    @Override
    public Date getDateValue(
            @NonNull final String xpath,
            final int index) {
        return null;
    }

    @Override
    public int getResourceCount() {
        return 0;
    }

    @Override
    public boolean hasResource() {
        return false;
    }

    @Override
    public boolean hasProtectedResource() {
        return false;
    }

    @NonNull
    @Override
    public String toDumpString() {
        return "";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(
            Parcel dest,
            int flags) {
        dest.writeString(this.mUdn);
    }

    private RootCdsObject(Parcel in) {
        this.mUdn = in.readString();
    }

    public static final Creator<RootCdsObject> CREATOR = new Creator<RootCdsObject>() {
        @Override
        public RootCdsObject createFromParcel(Parcel source) {
            return new RootCdsObject(source);
        }

        @Override
        public RootCdsObject[] newArray(int size) {
            return new RootCdsObject[size];
        }
    };
}
