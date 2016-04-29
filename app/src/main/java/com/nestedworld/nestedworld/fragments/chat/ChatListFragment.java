package com.nestedworld.nestedworld.fragments.chat;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.api.http.callback.Callback;
import com.nestedworld.nestedworld.api.http.implementation.NestedWorldHttpApi;
import com.nestedworld.nestedworld.api.http.models.response.users.friend.FriendsResponse;
import com.nestedworld.nestedworld.fragments.base.BaseFragment;
import com.nestedworld.nestedworld.helper.log.LogHelper;
import com.rey.material.widget.ProgressView;

import java.util.ArrayList;

import butterknife.Bind;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit.Response;
import retrofit.Retrofit;

public class ChatListFragment extends BaseFragment {

    @Bind(R.id.listView_chat_list)
    ListView listView;
    @Bind(R.id.progressView)
    ProgressView progressView;

    /*
    ** Public method
     */
    public static void load(@NonNull final FragmentManager fragmentManager) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, new ChatListFragment());
        fragmentTransaction.commit();
    }

    /*
    ** Life cycle
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_action_chat;
    }

    @Override
    protected void init(View rootView, Bundle savedInstanceState) {
        retrieveFriendsList();
    }

    private void retrieveFriendsList() {

        if (mContext == null) {
            return;
        }

        //start loading animation
        progressView.start();

        NestedWorldHttpApi.getInstance(mContext).getFriends(new Callback<FriendsResponse>() {
            @Override
            public void onSuccess(Response<FriendsResponse> response, Retrofit retrofit) {

                //TODO display a message when friends is empty
                populateListView(response.body().friends);

                //stop loading animation
                if (progressView != null) {
                    progressView.stop();
                }
            }

            @Override
            public void onError(@NonNull KIND errorKind, @Nullable Response<FriendsResponse> response) {
                LogHelper.d(TAG, "cannot retrieve friends");
                //TODO display an error message
            }
        });
    }

    private void populateListView(final ArrayList<FriendsResponse.Friend> friends) {
        if (mContext == null) {
            return;
        }

        //init adapter for our listview
        final FriendsAdapter friendAdapter = new FriendsAdapter(mContext, friends);
        listView.setAdapter(friendAdapter);

        //add listener on the listview
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChatFragment.load(getFragmentManager(), friendAdapter.getItem(position));
            }
        });

    }

    /**
     * * Custom adapter for displaying friend on the listView
     **/
    private class FriendsAdapter extends ArrayAdapter<FriendsResponse.Friend> {

        private static final int resource = R.layout.item_friend;
        private final Context mContext;

        public FriendsAdapter(@NonNull final Context context, @NonNull final ArrayList<FriendsResponse.Friend> friendList) {
            super(context, resource, friendList);
            this.mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view;
            FriendHolder friendHolder;

            if (convertView == null) {
                LayoutInflater layoutInflater = ((Activity) mContext).getLayoutInflater();
                view = layoutInflater.inflate(resource, parent, false);

                friendHolder = new FriendHolder();
                friendHolder.friendPicture = (ImageView) view.findViewById(R.id.imageView_item_friend);
                friendHolder.friendName = (TextView) view.findViewById(R.id.textView_item_friend);

                view.setTag(friendHolder);
            } else {
                friendHolder = (FriendHolder) convertView.getTag();
                view = convertView;
            }

            //get the currentFriend
            FriendsResponse.Friend currentFriend = getItem(position);

            //display the friend name
            friendHolder.friendName.setText(currentFriend.info.pseudo);

            //display a rounded placeHolder for friend's avatar
            Resources resources = mContext.getResources();
            Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.default_avatar);
            RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(resources, bitmap);
            roundedBitmapDrawable.setCornerRadius(Math.max(bitmap.getWidth(), bitmap.getHeight()) / 2.0f);

            //display friend's avatar
            Glide.with(mContext)
                    .load(currentFriend.info.avatar)
                    .placeholder(roundedBitmapDrawable)
                    .bitmapTransform(new CropCircleTransformation(mContext))
                    .centerCrop()
                    .into(friendHolder.friendPicture);

            return view;
        }

        private class FriendHolder {
            ImageView friendPicture;
            TextView friendName;
        }
    }
}