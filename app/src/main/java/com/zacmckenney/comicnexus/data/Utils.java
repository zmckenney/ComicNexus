package com.zacmckenney.comicnexus.data;

import android.content.ContentProviderOperation;

import com.zacmckenney.comicnexus.models.NewComic;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Created by Zac on 9/16/16.
 */
public class Utils {

    public static ContentProviderOperation buildComicBatchOperation(NewComic newComic, int whatWeek, int dayOfYear) {
        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
                ComicProvider.Comics.COMICS_URI);

        //Add all of our data to the DB
        builder.withValue(ComicColumns.COMIC_ID, newComic.getId());
        builder.withValue(ComicColumns.TITLE, newComic.getTitle());
        builder.withValue(ComicColumns.DESCRIPTION, newComic.getDescription());
        builder.withValue(ComicColumns.ISSUE_NUMBER, newComic.getIssueNumber());
        builder.withValue(ComicColumns.ON_SALE_DATE, newComic.getOnSaleDate());
        builder.withValue(ComicColumns.PAGE_COUNT, newComic.getPageCount());
        builder.withValue(ComicColumns.PRICE, newComic.getPrice());
        builder.withValue(ComicColumns.DETAIL_URL, newComic.getDetailUrl());
        builder.withValue(ComicColumns.THUMBNAIL_PATH, newComic.getThumbnailPath());
        builder.withValue(ComicColumns.CREATORS, Arrays.toString(newComic.getCreators()));
        builder.withValue(ComicColumns.WEEK, whatWeek);
        builder.withValue(ComicColumns.DAY_OF_YEAR, dayOfYear);

        return builder.build();
    }


    //Used to get our data from the Marvel API - uses a timestamp, public key and private key with a hash made from these
    public static String getAuthorizationKey(String tS, String privKey, String pubKey){
        String combinedString = tS + privKey + pubKey;
        try {
            MessageDigest d = MessageDigest.getInstance("MD5");
            d.update(combinedString.getBytes());
            byte digest[] = d.digest();

            BigInteger bigInteger = new BigInteger(1, digest);
            String hashString = bigInteger.toString(16);
            while (hashString.length() < 32) {
                hashString = "0" + hashString;
            }
            return "&ts=" + tS + "&apikey=" + pubKey + "&hash=" + hashString;

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "brokenhash";
    }
}
