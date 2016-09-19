package com.zacmckenney.comicnexus.data;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by Zac on 9/16/16.
 */

@ContentProvider(authority = ComicProvider.AUTHORITY, database = ComicDatabase.class)
public final class ComicProvider {

    public static final String AUTHORITY = "com.zacmckenney.comicnexus.data.ComicProvider";

    interface Path{
        String COMICS = "comics";
    }

    @TableEndpoint(table = ComicDatabase.COMICS) public static class Comics {

        @ContentUri(
                path = Path.COMICS,
                type = "vnd.android.cursor.dir/comic"
        )
        public static final Uri COMICS_URI = Uri.parse("content://" + AUTHORITY + "/" + Path.COMICS);


        @InexactContentUri(
                path = Path.COMICS + "/*",
                name = "COMIC_ID",
                type = "vnd.android.cursor.item/comic",
                whereColumn = ComicColumns.COMIC_ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return Uri.parse("content://" + AUTHORITY + "/" + Path.COMICS + "/" + id);
        }

    }
}
