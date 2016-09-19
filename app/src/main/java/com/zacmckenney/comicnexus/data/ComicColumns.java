package com.zacmckenney.comicnexus.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * Created by Zac on 9/16/16.
 */
public class ComicColumns {

    @DataType(DataType.Type.INTEGER) @PrimaryKey @AutoIncrement
    public static final String _ID = "_id";

    @DataType(DataType.Type.INTEGER)
    public static final String COMIC_ID = "comic_id";
    @DataType(DataType.Type.TEXT)
    public static final String TITLE = "title";
    @DataType(DataType.Type.INTEGER)
    public static final String PAGE_COUNT = "page_count";
    @DataType(DataType.Type.REAL)
    public static final String PRICE = "price";
    @DataType(DataType.Type.TEXT)
    public static final String THUMBNAIL_PATH = "thumbnail_path";
    @DataType(DataType.Type.INTEGER)
    public static final String ISSUE_NUMBER = "issue_number";
    @DataType(DataType.Type.TEXT)
    public static final String DESCRIPTION = "description";
    @DataType(DataType.Type.TEXT)
    public static final String ON_SALE_DATE = "on_sale_date";
    @DataType(DataType.Type.TEXT)
    public static final String CREATORS = "creators";
    @DataType(DataType.Type.TEXT)
    public static final String DETAIL_URL = "detail_url";
    @DataType(DataType.Type.INTEGER)
    public static final String WEEK = "week";
    @DataType(DataType.Type.INTEGER)
    public static final String DAY_OF_YEAR = "day_of_year";

}
