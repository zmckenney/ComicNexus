package com.zacmckenney.comicnexus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zacmckenney.comicnexus.models.NewComic;
import com.zacmckenney.comicnexus.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Zac on 9/11/16.
 */
public class ComicsGridAdapter extends ArrayAdapter<NewComic>{

    @BindView(R.id.grid_item_comic_cover)
    ImageView coverImageView;

    @BindView(R.id.grid_item_comic_price)
    TextView priceTextView;

    public ComicsGridAdapter(Context context, ArrayList<NewComic> resource) {
        super(context, 0, resource);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NewComic newComic = getItem(position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_comic_cover, parent, false);
        }

        ButterKnife.bind(this, convertView);

        Glide.with(getContext()).load(newComic.getThumbnailPath()).placeholder(R.drawable.loading).error(R.drawable.error).into(coverImageView);
        double newComicPrice = newComic.getPrice();
        if (newComicPrice != 0) {
            priceTextView.setText(getContext().getString(R.string.price_on_gridview, newComic.getPrice()));
        } else {
            priceTextView.setText(R.string.free);
        }

        convertView.setContentDescription(getContext().getString(R.string.a11y_cover, newComic.getTitle(), newComic.getPrice()));

        return convertView;
    }
}
