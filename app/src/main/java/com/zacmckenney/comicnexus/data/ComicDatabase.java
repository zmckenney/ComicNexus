package com.zacmckenney.comicnexus.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by Zac on 9/16/16.
 */

@Database(version = ComicDatabase.VERSION)
public final class ComicDatabase {

    public static final int VERSION = 1;

    @Table(ComicColumns.class) public static final String COMICS = "comics";
}
