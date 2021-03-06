package com.nestedworld.nestedworld.ui.adapter.array;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.data.database.entities.friend.Friend;
import com.nestedworld.nestedworld.data.database.entities.friend.FriendData;
import com.nestedworld.nestedworld.ui.view.base.BaseAppCompatActivity;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.CropCircleTransformation;


public class ChatAdapter extends ArrayAdapter<Friend> {

    @LayoutRes
    private static final int mResource = R.layout.item_chat_list;

    /*
     * #############################################################################################
     * # Constructor
     * #############################################################################################
     */
    public ChatAdapter(@NonNull final Context context) {
        super(context, mResource);
    }

    /*
     * #############################################################################################
     * # ArrayAdapter<Friend> implementation
     * #############################################################################################
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        View view = convertView;
        ChatHolder chatHolder;

        //Check if an existing view is being reused, otherwise inflate the view
        if (view == null) {
            final LayoutInflater layoutInflater = ((BaseAppCompatActivity) getContext()).getLayoutInflater();
            view = layoutInflater.inflate(mResource, parent, false);

            chatHolder = new ChatHolder();
            chatHolder.chatPicture = (CircleImageView) view.findViewById(R.id.imageView_chat_picture);
            chatHolder.chatPictureOverlay = (CircleImageView) view.findViewById(R.id.imageView_chat_picture_overlay);
            chatHolder.chatName = (TextView) view.findViewById(R.id.textView_chat_name);

            view.setTag(chatHolder);
        } else {
            chatHolder = (ChatHolder) convertView.getTag();
            view = convertView;
        }

        //get the currentFriend
        final Friend currentFriend = getItem(position);
        if (currentFriend == null) {
            return view;
        }

        //get current friend information
        final FriendData friendData = currentFriend.getData();
        if (friendData == null) {
            return view;
        }

        populateView(chatHolder, friendData);

        return view;
    }

    /*
     * #############################################################################################
     * # Internal method
     * #############################################################################################
     */
    private void populateView(@NonNull final ChatHolder chatHolder,
                              @NonNull final FriendData friendData) {
        final Context context = getContext();

        //display the friend name
        chatHolder.chatName.setText(friendData.pseudo);

        //display a rounded placeHolder for friend's avatar
        final Resources resources = context.getResources();
        final Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.default_avatar);
        final RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(resources, bitmap);
        roundedBitmapDrawable.setCornerRadius(Math.max(bitmap.getWidth(), bitmap.getHeight()) / 2.0f);

        //display friend's avatar
        Glide.with(context)
                .load(friendData.avatar)
                .placeholder(roundedBitmapDrawable)
                .centerCrop()
                .bitmapTransform(new CropCircleTransformation(context))
                .into(chatHolder.chatPicture);

        //set overlay and stroke
        if (friendData.isConnected) {
            chatHolder.chatPicture.setBorderColor(ContextCompat.getColor(context, R.color.apptheme_color));
            chatHolder.chatPictureOverlay.setVisibility(View.VISIBLE);
        } else {
            chatHolder.chatPicture.setBorderColor(ContextCompat.getColor(context, R.color.apptheme_accent));
            chatHolder.chatPictureOverlay.setVisibility(View.GONE);
        }
    }

    /*
    ** Inner class
     */
    private static class ChatHolder {
        public CircleImageView chatPicture;
        public TextView chatName;
        public CircleImageView chatPictureOverlay;
    }
}