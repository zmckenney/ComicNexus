package com.zacmckenney.comicnexus.parsers;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.zacmckenney.comicnexus.models.Creators;
import com.zacmckenney.comicnexus.models.SearchResult;

import java.lang.reflect.Type;

/**
 * Created by Zac on 9/12/16.
 */
public class SearchResultParser implements JsonDeserializer<SearchResult[]> {
    @Override
    public SearchResult[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        //Skip the boring parts of the JSON we dont need
        final JsonObject jsonObjectAll = json.getAsJsonObject();
        final JsonObject jsonObjectData = jsonObjectAll.get("data").getAsJsonObject();
        final JsonArray jsonResultsArray = jsonObjectData.get("results").getAsJsonArray();

        SearchResult[] searchResultsList = new SearchResult[jsonResultsArray.size()];

        for (int i = 0; i < searchResultsList.length; i++ ) {
            JsonElement jsonSRElement = jsonResultsArray.get(i);
            JsonObject jsonSRObject = jsonSRElement.getAsJsonObject();
            JsonObject jsonCreatorsObject = jsonSRObject.get("creators").getAsJsonObject();
            JsonArray jsonUrlArray = jsonSRObject.get("urls").getAsJsonArray();

            int id = jsonSRObject.get("id").getAsInt();
            String title = jsonSRObject.get("title").getAsString();
            int startYear = jsonSRObject.get("startYear").getAsInt();
            String type = jsonSRObject.get("type").getAsString();

            JsonElement jsonUrlElement = jsonUrlArray.get(0);
            JsonObject jsonUrlObject = jsonUrlElement.getAsJsonObject();
            String url = jsonUrlObject.get("url").getAsString();

            Creators[] creators = context.deserialize(jsonCreatorsObject.get("items"), Creators[].class);

            SearchResult singleSearchResult = new SearchResult();
            singleSearchResult.setId(id);
            singleSearchResult.setStartYear(startYear);
            singleSearchResult.setTitle(title);
            singleSearchResult.setType(type);
            singleSearchResult.setUrl(url);
            singleSearchResult.setCreators(creators);

            searchResultsList[i] = singleSearchResult;
        }

        return searchResultsList;
    }
}
