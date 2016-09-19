package com.zacmckenney.comicnexus.parsers;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.zacmckenney.comicnexus.models.Creators;
import com.zacmckenney.comicnexus.models.NewComic;

import java.lang.reflect.Type;

/**
 * Created by Zac on 9/11/16.
 */
public class NewComicParser implements JsonDeserializer<NewComic[]> {


    @Override
    public NewComic[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        //Skip the boring parts of the JSON we dont need (yet?)
        final JsonObject jsonObjectAll = json.getAsJsonObject();
        final JsonObject jsonObjectData = jsonObjectAll.get("data").getAsJsonObject();
        final JsonArray jsonResultsArray = jsonObjectData.get("results").getAsJsonArray();



        //NewComic list to host all our data
        NewComic[] newComicsList = new NewComic[jsonResultsArray.size()];

        //Iterate through all results
        for (int i = 0; i < newComicsList.length; i++ ) {
            JsonElement jsonNewComicElement = jsonResultsArray.get(i);
            JsonObject jsonNewComicObject = jsonNewComicElement.getAsJsonObject();
            JsonObject jsonCreatorsObject = jsonNewComicObject.get("creators").getAsJsonObject();

            //Our ID and Page Number are both inside the object
            int jsonID = jsonNewComicObject.get("id").getAsInt();
            int jsonPageNumbers = jsonNewComicObject.get("pageCount").getAsInt();
            String title = jsonNewComicObject.get("title").getAsString();
            int issueNumber = jsonNewComicObject.get("issueNumber").getAsInt();
            String description;
            try {
                description = jsonNewComicObject.get("description").getAsString();
            } catch (Exception e){
                description = "Unavailable";
            }

            //Price is in an array
            JsonArray jsonPricesArray = jsonNewComicObject.get("prices").getAsJsonArray();
            JsonElement jsonPriceElement = jsonPricesArray.get(0);
            JsonObject jsonPriceObject = jsonPriceElement.getAsJsonObject();
            double jsonPrice = jsonPriceObject.get("price").getAsDouble();

            //detail url is in an array
            JsonArray jsonDetailArray = jsonNewComicObject.get("urls").getAsJsonArray();
            JsonElement jsonDetailElement = jsonDetailArray.get(0);
            JsonObject jsonDetailObject = jsonDetailElement.getAsJsonObject();
            String detailUrl = jsonDetailObject.get("url").getAsString();

            //On sale date is inside an array with 2 objects
            JsonArray jsonDateArray = jsonNewComicObject.get("dates").getAsJsonArray();
            //its always the first date in the array
            JsonElement jsonDateElement = jsonDateArray.get(0);
            JsonObject jsonDateObject = jsonDateElement.getAsJsonObject();
            String dateString = jsonDateObject.get("date").getAsString();

            //Thumbnails are in an Object
            JsonObject jsonThumbnailObject = jsonNewComicObject.get("thumbnail").getAsJsonObject();
            String jsonThumbPath = jsonThumbnailObject.get("path").getAsString();
            String jsonThumbExtension = jsonThumbnailObject.get("extension").getAsString();


            Creators[] creators = context.deserialize(jsonCreatorsObject.get("items"), Creators[].class);
            String[] creatorStrings = new String[creators.length];

            if (creators != null) {
                for (int j = 0; j < creators.length; j++){
                    creatorStrings[j] = creators[j].getRole() + ": " + creators[j].getName();
                }
            }



            //Create a new NewComic object
            NewComic newComic = new NewComic();
            newComic.setId(jsonID);
            newComic.setPageCount(jsonPageNumbers);
            newComic.setPrice(jsonPrice);
            newComic.setThumbnailParameters(jsonThumbPath, jsonThumbExtension);
            newComic.setTitle(title);
            newComic.setIssueNumber(issueNumber);
            newComic.setDescription(description);
            newComic.setOnSaleDate(dateString);
            newComic.setCreators(creatorStrings);
            newComic.setDetailUrl(detailUrl);

            //Add the NewComic into our array
            newComicsList[i] = newComic;
        }

        return newComicsList;
        }
    }

