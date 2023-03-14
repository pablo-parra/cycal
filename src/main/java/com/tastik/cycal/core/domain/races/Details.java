package com.tastik.cycal.core.domain.races;

import java.util.Objects;

public class Details {
    public final String title;
    public final String url;
    public final boolean isExternal;

    public Details(String title, String url, boolean isExternal) {
        this.title = title;
        this.url = url;
        this.isExternal = isExternal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Details details = (Details) o;
        return isExternal == details.isExternal && Objects.equals(title, details.title) && Objects.equals(url, details.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, url, isExternal);
    }

    @Override
    public String toString() {
        return "Details{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", isExternal=" + isExternal +
                '}';
    }
}
