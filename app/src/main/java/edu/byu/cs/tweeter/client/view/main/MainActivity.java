package edu.byu.cs.tweeter.client.view.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//import edu.byu.cs.client.R;
import edu.byu.cs.tweeter.R;
import edu.byu.cs.tweeter.client.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.backgroundTask.PostStatusTask;
import edu.byu.cs.tweeter.client.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.presenter.FollowersPresenter;
import edu.byu.cs.tweeter.client.presenter.FollowingPresenter;
import edu.byu.cs.tweeter.client.presenter.MainActivityPresenter;
import edu.byu.cs.tweeter.client.presenter.StoryPresenter;
import edu.byu.cs.tweeter.client.view.login.LoginActivity;
import edu.byu.cs.tweeter.client.view.login.StatusDialogFragment;
import edu.byu.cs.tweeter.client.view.util.ImageUtils;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * The main activity for the application. Contains tabs for feed, story, following, and followers.
 */
public class MainActivity extends AppCompatActivity implements StatusDialogFragment.Observer,
                                                                MainActivityPresenter.View {

    private static final String LOG_TAG = "MainActivity";

    public static final String CURRENT_USER_KEY = "CurrentUser";

    private Toast logOutToast;
    private Toast postingToast;
    private User selectedUser;
    private TextView followeeCount;
    private TextView followerCount;
    private Button followButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectedUser = (User) getIntent().getSerializableExtra(CURRENT_USER_KEY);
        if (selectedUser == null) {
            throw new RuntimeException("User not passed to activity");
        }

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), selectedUser);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StatusDialogFragment statusDialogFragment = new StatusDialogFragment();
                statusDialogFragment.show(getSupportFragmentManager(), "post-status-dialog");
            }
        });

        updateSelectedUserFollowingAndFollowers();

        TextView userName = findViewById(R.id.userName);
        userName.setText(selectedUser.getName());

        TextView userAlias = findViewById(R.id.userAlias);
        userAlias.setText(selectedUser.getAlias());

        ImageView userImageView = findViewById(R.id.userImage);
        System.out.println(selectedUser);
        userImageView.setImageDrawable(ImageUtils.drawableFromByteArray(selectedUser.getImageBytes()));

        followeeCount = findViewById(R.id.followeeCount);
        followeeCount.setText(getString(R.string.followeeCount, "..."));

        followerCount = findViewById(R.id.followerCount);
        followerCount.setText(getString(R.string.followerCount, "..."));

        followButton = findViewById(R.id.followButton);


        MainActivityPresenter mainActivityPresenter = new MainActivityPresenter(this,
                Cache.getInstance().getCurrUserAuthToken(), selectedUser);

        if (selectedUser.compareTo(Cache.getInstance().getCurrUser()) == 0) {
            followButton.setVisibility(View.GONE);
        } else {
            followButton.setVisibility(View.VISIBLE);



            mainActivityPresenter.isFollowing(selectedUser);

        }


        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followButton.setEnabled(false);

                if (followButton.getText().toString().equals(v.getContext().getString(R.string.following))) {
                    mainActivityPresenter.followUnfollow(selectedUser, false);
                    updateFollowButton(true);
                } else {
                    mainActivityPresenter.followUnfollow(selectedUser, true);
                    updateFollowButton(false);

                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logoutMenu) {
            logOutToast = Toast.makeText(this, "Logging Out...", Toast.LENGTH_LONG);
            logOutToast.show();

            MainActivityPresenter mainActivityPresenter = new MainActivityPresenter(this,
                    Cache.getInstance().getCurrUserAuthToken(), selectedUser);
            mainActivityPresenter.logout(Cache.getInstance().getCurrUserAuthToken());
            logoutUser();


            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle oldInstanceState) {
        super.onSaveInstanceState(oldInstanceState);
        oldInstanceState.clear();
    }

    public void logoutUser() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Cache.getInstance().clearCache();
        startActivity(intent);
    }

    @Override
    public void onStatusPosted(String post) {
        try {
            MainActivityPresenter mainActivityPresenter = new MainActivityPresenter(this,
                    Cache.getInstance().getCurrUserAuthToken(), selectedUser);
            mainActivityPresenter.formatStatus(post);
        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
            Toast.makeText(this, "Failed to post the status because of exception: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void updateSelectedUserFollowingAndFollowers() {

        MainActivityPresenter mainActivityPresenter = new MainActivityPresenter(this,
                                                                Cache.getInstance().getCurrUserAuthToken(), selectedUser);
            mainActivityPresenter.followersCount();
            mainActivityPresenter.followingCount();
    }

    public void updateFollowButton(boolean removed) {
        if (removed) {
            followButton.setText(R.string.follow);
            followButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        } else {
            followButton.setText(R.string.following);
            followButton.setBackgroundColor(getResources().getColor(R.color.white));
            followButton.setTextColor(getResources().getColor(R.color.lightGray));
        }
    }



    @Override
    public void displaySuccessMessage(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void displayErrorMessage(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void displayInfoMessage(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void followUnfollow(boolean follow, String message){
        if(message == "") {
            if (follow) {

                Toast.makeText(MainActivity.this, "Following", Toast.LENGTH_SHORT).show();
                updateSelectedUserFollowingAndFollowers();
                updateFollowButton(false);
                followButton.setEnabled(true);
            } else {
                Toast.makeText(MainActivity.this, "Unfollowing", Toast.LENGTH_SHORT).show();
                updateSelectedUserFollowingAndFollowers();
                updateFollowButton(true);
                followButton.setEnabled(true);
            }
        }
        else {
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void displayFollowerCount(int count) {
        followerCount.setText("Followers: " + count);

    }

    @Override
    public void displayFollowingCount(int count) {
        followeeCount.setText("Following: " + count);
    }

    @Override
    public void displayFollowButton(boolean isFollower) {
        if (isFollower) {
            followButton.setText(R.string.following);
            followButton.setBackgroundColor(getResources().getColor(R.color.white));
            followButton.setTextColor(getResources().getColor(R.color.lightGray));
        } else {
            followButton.setText(R.string.follow);
            followButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }
    }


}
