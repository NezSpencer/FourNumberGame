package com.nuhiara.nezspencer.fourNumbers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameActivity;


public class Loader extends BaseGameActivity {


    int totalScore;
    int tempScore;

    SharedPreferences.Editor ed;
    int exitCount;

    SignInButton signin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.game_cover_page);




        exitCount=0;

        //getGameHelper().setMaxAutoSignInAttempts(1);

        signin=(SignInButton)findViewById(R.id.signbutton);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isSignedIn())
                    Toast.makeText(Loader.this,"Already Signed in",Toast.LENGTH_LONG).show();
                else
                {
                    beginUserInitiatedSignIn();
                }

            }
        });



        ImageButton game=(ImageButton)findViewById(R.id.new_game_button);
        game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                startActivity(new Intent(Loader.this,Home.class));
                exitCount=0;

            }
        });

        ImageButton high=(ImageButton)findViewById(R.id.leaderboard_button);
        high.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                exitCount=0;
                SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                totalScore = preferences.getInt("highscore",0);
                if (getApiClient().isConnected())
                {

                        sendToLeaderboard();


                    startActivityForResult(Games.Leaderboards.getLeaderboardIntent(
                                    getApiClient(), getString(R.string.board_id)),
                            2);
                }

                else
                    Toast.makeText(Loader.this, "You need to be signed in to view this", Toast.LENGTH_LONG).show();
            }
        });

        ImageButton achieve=(ImageButton)findViewById(R.id.achievement_button);
        achieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                exitCount=0;
                SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                tempScore = preferences.getInt("TempScore",0);
                if (getApiClient().isConnected())
                {

                        //submitAchievement(tempScore);
                    startActivityForResult(Games.Achievements.getAchievementsIntent(
                            getApiClient()), 1);

                }

                else
                    Toast.makeText(Loader.this,"You need to be signed in to view this",Toast.LENGTH_LONG).show();
            }
        });

        ImageButton rate=(ImageButton)findViewById(R.id.likeButton);
        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitCount=0;
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id="+getApplicationContext().getPackageName())));
            }
        });





    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("destroyer","destroying");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_loader, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
          //  return true;
        //}

        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0)
        {
            exitCount++;

            if (exitCount==1)
                Toast.makeText(Loader.this,"Press return key again to exit",Toast.LENGTH_SHORT).show();

            if (exitCount>=2)
                finish();
            //showExitDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

//    private void showExitDialog() {
//
//
//
//
//        Log.e("back key", "pressed so paused");
//
//        final AlertDialog.Builder exiter =new AlertDialog.Builder(Loader.this);
//
//
//        exiter.setView(getLayoutInflater().inflate(R.layout.wrong, null));
//        exiter.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//
//
//                dialogInterface.dismiss();
//                finish();
//            }
//        });
//
//        exiter.setNegativeButton("No",new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//
//                dialogInterface.dismiss();
//
//
//                dialogInterface.cancel();
//            }
//        });
//
//        /**
//         exiter.setOnCancelListener(new DialogInterface.OnCancelListener() {
//        @Override
//        public void onCancel(DialogInterface dialogInterface) {
//
//        finish();
//        }
//        });
//         **/
//
//
//        exiter.setCancelable(false);
//        AlertDialog alert=exiter.create();
//        alert.setOwnerActivity(Loader.this);
//
//
//        alert.show();
//    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int sub= preferences.getInt("play",1);




        if (getApiClient().isConnected())
        {

            sendToLeaderboard();

            if (sub==1)
            {
                SharedPreferences p1=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                int score=p1.getInt("TempScore",0);

                submitAchievement(score);
                SharedPreferences pp=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                ed=pp.edit();
                ed.putInt("play",0);
                ed.apply();
            }
        }
    }

    @Override
    public void onSignInFailed() {


    }


    @Override
    public void onSignInSucceeded() {

        //signin.setVisibility(View.GONE);


    }

    private void sendToLeaderboard() {

        Games.Leaderboards.submitScore(getApiClient(),
                getString(R.string.board_id),
                totalScore);

    }

    private void submitAchievement(int totalScore) {

        if (totalScore ==10)
            Games.Achievements.unlock(getApiClient(),
                    getString(R.string.achievement_1));
        if (totalScore ==20)
            Games.Achievements.unlock(getApiClient(),
                    getString(R.string.achievement_2));
        if (totalScore >=50)
            Games.Achievements.unlock(getApiClient(),
                    getString(R.string.achievement_3));
        if (totalScore >=100)
            Games.Achievements.unlock(getApiClient(),
                    getString(R.string.achievement_4));
        if (totalScore >=200)
            Games.Achievements.unlock(getApiClient(),
                    getString(R.string.achievement_5));
        if ((totalScore % 5) ==0)
            Games.Achievements.unlock(getApiClient(),
                    getString(R.string.achievement_factor_of_five));
        if (totalScore ==12)
            Games.Achievements.unlock(getApiClient(),
                    getString(R.string.achievement_dozen));
        if (totalScore % 2==0)
            Games.Achievements.unlock(getApiClient(),  //get an even score 20 times
                    getString(R.string.achievement_even));
        if (totalScore % 2 !=0)
            Games.Achievements.unlock(getApiClient(),
                    getString(R.string.achievement_odd)); //get an odd score 30 times
        else {

        }

    }
}
