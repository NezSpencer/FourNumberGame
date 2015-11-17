package com.nuhiara.nezspencer.fourNumbers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class Home extends Activity {


//    private InterstitialAd interstitialAd;
  //  private String InterstitialId="ca-app-pub-6876251827178857/5984142529";

    View nextLayout_view;

    private String[] signs;
    private int gv1;
//    private ArrayList<String> operator =new ArrayList<String>();
    private int gv2;
    private int result;

    private  TextView yours;
    private TextView heading;
    private TextView my_score;
    private TextView hi_score;

    private Handler handler=new Handler();
    ProgressBar progressBar;
    int progressStatus=100;

    Vibrator vibrator;


    int totalScore=0;  //players current score
    TextView score; // textView to display current score

    Thread th;
    TextView answer;

    int one;
    int two;
    int three;
    int four;
//the four editTexts below is where the user types in the correct sequence of numbers
    TextView edit1;
    TextView edit2;
    TextView edit3;
    TextView edit4;

    //This textviews contain the signs
    TextView sign1;
    TextView sign2;
    TextView sign3;

    TextView highscoreBoard;

    Button choice1;
    Button choice2;
    Button choice3;
    Button choice4;

    View dialogView;
    View timeover;
    View wrongans;

    int Highscore=0;
    SharedPreferences highScorePrefs;
    SharedPreferences.Editor edit;

    SharedPreferences played;
    SharedPreferences.Editor playEdit;

    SharedPreferences scorePref;
    int holdScore;



    AlertDialog listen;


    View quit;


    static String Leaderboard_id;
    static int REQUEST_LEADERBOARD=12;

    int signNum=0;



    int sound; //integer to store the sound
    SoundPool soundPool;

    int choice;

    View myView;


    private boolean isGameOver=false;
    private boolean timedout=false;
    private boolean paused=false;
    private boolean resetTime=false;
    private int as;
    private boolean backButtonPressed;
    private int time=0;
    private int backPressed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        backPressed=0;

        vibrator=(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);


        AdView adView =(AdView)findViewById(R.id.bannerAd);
        AdRequest adRequest0=new AdRequest.Builder().build();
        adView.loadAd(adRequest0);



        LayoutInflater inflater =getLayoutInflater();

        nextLayout_view= inflater.inflate(R.layout.game_cover_page,null);

        myView=inflater.inflate(R.layout.highscore_layout,null);


        highscoreBoard=(TextView)findViewById(R.id.highBoard);


        //exitView=inflater.inflate(R.layout.wrong,null);
        quit=inflater.inflate(R.layout.timed_out,null);


        as=0;
        score=(TextView)findViewById(R.id.score);
        answer =(TextView)findViewById(R.id.answer);

        yours=(TextView)myView.findViewById(R.id.your_score);

        heading=(TextView)quit.findViewById(R.id.heading);
        my_score=(TextView)quit.findViewById(R.id.my_score);
        hi_score=(TextView)quit.findViewById(R.id.my_hiscore);

        Leaderboard_id=getString(R.string.LeaderBoard_id);

        edit1 =(TextView)findViewById(R.id.num1);
        edit2 =(TextView)findViewById(R.id.num2);
        edit3 =(TextView)findViewById(R.id.num3);
        edit4 =(TextView)findViewById(R.id.num4);


        sign1 =(TextView)findViewById(R.id.sign1);
        sign2 =(TextView)findViewById(R.id.sign2);
        sign3=(TextView)findViewById(R.id.sign3);

// setting to load saved highscore
        highScorePrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Highscore = highScorePrefs.getInt("highscore",0);

        played=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        playEdit=played.edit();
        playEdit.putInt("play",0);
        playEdit.apply();

        highscoreBoard.setText("BEST SCORE: \n "+Highscore);
        scorePref=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        holdScore=scorePref.getInt("TempScore",0);

        //timer code
        soundPool=new SoundPool(1, AudioManager.STREAM_MUSIC,1);
        sound=soundPool.load(Home.this,R.raw.timer,1);







       // numList =(TextView)findViewById(R.id.numList);
        choice1 =(Button)findViewById(R.id.firstNum);
        choice2=(Button)findViewById(R.id.secondNum);
        choice3=(Button)findViewById(R.id.thirdNum);
        choice4=(Button)findViewById(R.id.fourthNum);

        progressBar=(ProgressBar)findViewById(R.id.timer);


        signs=new String[3];
        paused=true;


        dialogView=inflater.inflate(R.layout.custom,null);
        timeover=inflater.inflate(R.layout.timed_out,null);
        wrongans=inflater.inflate(R.layout.wrong,null);









        paused=false;
        gamePlay();

        Log.e("OnCreate", " 3");

    }

    /**
    private void showInterstitial()
    {
        if (interstitialAd != null && interstitialAd.isLoaded()) {
            interstitialAd.show();
        } else {
            //Toast.makeText(this, "Ad did not load", Toast.LENGTH_SHORT).show();

        }
    }
     **/


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       // if (id == R.id.action_settings) {
            //return true;
        //}

        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onPause() {
        super.onPause();
        paused=true;
        backButtonPressed=true;
        progressBar.onSaveInstanceState();
        if (listen !=null)
            listen.dismiss();
        Log.e("Activity is ","Paused now");
    }

    @Override
    protected void onResume() {
        super.onResume();
        backButtonPressed=false;
        progressBar.onRestoreInstanceState(progressBar.onSaveInstanceState());
    }

    public void gamePlay()
    {

        th =new Thread(new Runnable() {
            @Override
            public void run() {

                for(;!isGameOver && !paused;)
                {


                    one =(int)(Math.random()*5 +1); //gen first random num
                    two =(int)(Math.random()*5 +1); //gen sec random num

                    gv1 = sign(one,two);  // gen random sign nd use on d frst 2 nums

                    three =(int)(Math.random()*5 +1);//gen 3rd random num
                    gv2 = sign(gv1,three);//gen 2nd random sign to use on result 1 nd 3rd random num

                    four =(int)(Math.random()*5 +1);//gen 4th random num
                    result = sign(gv2,four);//gen random sign to use on result2 nd 3rd random num

                    Log.d("run","entering ui thread");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Log.d("run","ui thread run");
                            //numList.setText("");
                            choice1.setText("");
                            choice2.setText("");
                            choice3.setText("");
                            choice4.setText("");

//                            if (totalScore > Highscore)
//                                highscoreBoard.setText("BEST SCORE: \n "+totalScore);

                            score.setText("SCORE: \n"+totalScore);
                            answer.setText(""+result);

                            sign1.setText(signs[0]);
                            sign2.setText(signs[1]);
                            sign3.setText(signs[2]);
                            Log.e("one is"," "+one);
                            Log.e("two is"," "+two);
                            Log.e("three is"," "+three);
                            Log.e("four is"," "+four);



                            //operator.clear();

                            //numList.setText(one+","+two+","+three+","+four);
                            int [] holder ={one,two,three,four};
                            int [] temp=new int[4];
                            int s=0;
                            int p=holder.length;
                            int q=holder.length;
                            for(int l=0; l<p;l++)
                            {
                                int y=(int)(Math.random() *q);
                               // numList.append(" "+holder[y]+" ");
                                temp[s]=holder[y];
                                s++;


                                for(int m=y; m<p; m++)
                                {
                                    if(m!=p-1)
                                        holder[m]=holder[m+1];

                                }

                                q--;
                            }
                            choice1.setText(""+temp[0]);
                            choice2.setText(""+temp[1]);
                            choice3.setText(""+temp[2]);
                            choice4.setText(""+temp[3]);

                            Log.e("here","paused is"+paused);
                        }
                    });





                    while (progressStatus >=0)
                    {
                        //Log.e("here inside while loop","paused is"+paused);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if(!edit1.getText().toString().equals("") &&
                                        !edit2.getText().toString().equals("") &&
                                        !edit3.getText().toString().equals("") &&
                                        !edit4.getText().toString().equals("") )
                                {

                                    checkCorrect();
                                }
                            }
                        });



                        if(resetTime)
                        {
                            progressStatus=100;
                            resetTime=false;
                            break;
                        }

                        if (progressStatus<=50 && progressStatus >0 && !paused)
                            soundPool.play(sound,1,1,1,0,1);

                        if (!backButtonPressed)
                            progressStatus--;
                        try {

                            if (totalScore <=25)
                            {
                                time=200 -((totalScore%10)*2);
                                Thread.sleep(time);
                            }
                            else
                                Thread.sleep(time);


                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                progressBar.setProgress(progressStatus);


                                /**
                                 if(!isAnsCorrect)
                                 {
                                 if (getApiClient().isConnected())
                                 submitAchievement();

                                 if(totalScore >Highscore)
                                 {

                                 if (getApiClient().isConnected())
                                 sendToLeaderboard(totalScore);

                                 as =1;
                                 Log.e("Highscore"," jst inside if");
                                 Highscore=totalScore;
                                 edit =highScorePrefs.edit();
                                 edit.putInt("highscore", totalScore);
                                 edit.apply();

                                 Log.e("Highscore", " dialog begins");
                                 AlertDialog.Builder over =new AlertDialog.Builder(Home.this,R.style.Theme_AppCompat_Light_Dialog_Alert);



                                 Log.e("highscore", " dialog");

                                 over.setView(myView);

                                 yours.setText(""+Highscore);
                                 //over.setMessage("NEW BEST!! : "+Highscore);
                                 //over.setIcon(R.drawable.trophy);
                                 over.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                @Override public void onClick(DialogInterface dialogInterface, int i) {
                                Intent closure = getIntent();

                                dialogInterface.dismiss();
                                finish();

                                startActivity(closure);

                                //showInterstitial();
                                }
                                });

                                 over.setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                                @Override public void onClick(DialogInterface dialogInterface, int i) {

                                dialogInterface.dismiss();
                                finish();

                                }
                                });

                                 over.setNeutralButton("Leaderboard",new DialogInterface.OnClickListener() {
                                @Override public void onClick(DialogInterface dialogInterface, int i) {

                                if (getApiClient().isConnected())
                                startActivityForResult(Games.Leaderboards.getLeaderboardIntent(
                                getApiClient(), getString(R.string.board_id)),
                                REQUEST_LEADERBOARD);
                                //showInterstitial();
                                Home.this.setContentView(nextLayout_view);

                                }
                                });

                                 over.setCancelable(false);
                                 AlertDialog al=over.create();

                                 al.setOwnerActivity(Home.this);

                                 if(Home.this !=null && !Home.this.isFinishing())
                                 {
                                 al.show();
                                 Log.e("showing"," now");
                                 }


                                 paused=true;
                                 resetTime=true;
                                 isGameOver=true;


                                 }



                                 else if (totalScore<= Highscore && as==0){
                                 AlertDialog.Builder over = new AlertDialog.Builder(Home.this);




                                 View xc=getLayoutInflater().inflate(R.layout.timed_out,null);
                                 over.setView(xc);
                                 heading=(TextView)xc.findViewById(R.id.heading);
                                 my_score=(TextView)xc.findViewById(R.id.my_score);
                                 hi_score=(TextView)xc.findViewById(R.id.my_hiscore);

                                 heading.setText("WRONG ANSWER!");
                                 my_score.setText("You Scored:  " + totalScore);
                                 hi_score.setText("Highscore:   "+Highscore);


                                 //over.setMessage("Your Score: " + totalScore + " \n Highscore: " + Highscore);
                                 over.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                @Override public void onClick(DialogInterface dialogInterface, int i) {
                                Intent closure = getIntent();

                                dialogInterface.dismiss();
                                finish();

                                startActivity(closure);

                                //showInterstitial();
                                }
                                });

                                 over.setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                                @Override public void onClick(DialogInterface dialogInterface, int i) {

                                dialogInterface.dismiss();
                                finish();

                                }
                                });

                                 over.setNeutralButton("Leaderboard",new DialogInterface.OnClickListener() {
                                @Override public void onClick(DialogInterface dialogInterface, int i) {

                                if (getApiClient().isConnected())
                                startActivityForResult(Games.Leaderboards.getLeaderboardIntent(
                                getApiClient(), getString(R.string.board_id)),
                                REQUEST_LEADERBOARD);


                                //showInterstitial();
                                Home.this.setContentView(nextLayout_view);
                                }
                                });


                                 over.setCancelable(false);
                                 AlertDialog al = over.create();



                                 al.setOwnerActivity(Home.this);

                                 if(Home.this !=null && !Home.this.isFinishing())

                                 al.show();
                                 if (Home.this.isFinishing())
                                 al.dismiss();

                                 paused = true;
                                 resetTime = true;
                                 isGameOver = true;
                                 }
                                 }
                                 **/
                            }

                        });

                      if (progressStatus==0)
                      {
                          isGameOver=true;
                          paused=true;
                          vibrator.vibrate(500);
                          break;
                      }

                    }
                    //progressStatus=100;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                            if (progressStatus == 0) {
                                paused = true;
                                resetTime=false;
                                edit=scorePref.edit();
                                edit.putInt("TempScore",totalScore);
                                edit.apply();

                                playEdit=played.edit();
                                playEdit.putInt("play",1);
                                playEdit.apply();


                                if (totalScore > Highscore) {
                                    Log.e("Highscore", " place entered");

                                    as = 1;
                                    edit = highScorePrefs.edit();
                                    edit.putInt("highscore", totalScore);
                                    edit.apply();




                                    AlertDialog.Builder over = new AlertDialog.Builder(Home.this, R.style.Theme_AppCompat_Light_Dialog_Alert);


                                    Log.e("highscore", " dialog");

                                    over.setView(myView);

                                    yours.setText("" + totalScore);
                                    //over.setMessage("NEW BEST!! : "+Highscore);
                                    //over.setIcon(R.drawable.trophy);
                                    over.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent closure = getIntent();

                                            dialogInterface.dismiss();
                                            finish();

                                            startActivity(closure);
                                            //showInterstitial();
                                        }
                                    });

                                    over.setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            dialogInterface.dismiss();
                                            finish();

                                        }
                                    });


                                    over.setCancelable(false);
                                    AlertDialog al = over.create();

                                    al.setOwnerActivity(Home.this);

                                    if (Home.this != null && !Home.this.isFinishing()) {
                                        al.show();
                                        Log.e("showing", " now");
                                    }


                                    paused = true;
                                    resetTime = true;
                                    isGameOver = true;
                                } else if (totalScore <= Highscore && as == 0) {

                                    AlertDialog.Builder over = new AlertDialog.Builder(Home.this);

                                    View xc = getLayoutInflater().inflate(R.layout.timed_out, null);
                                    over.setView(xc);
                                    heading = (TextView) xc.findViewById(R.id.heading);
                                    my_score = (TextView) xc.findViewById(R.id.my_score);
                                    hi_score = (TextView) xc.findViewById(R.id.my_hiscore);

                                    heading.setText("GAME OVER");
                                    my_score.setText("You Scored:  " + totalScore);
                                    hi_score.setText("HIGHSCORE:   " + Highscore);


                                    //over.setMessage("Your Score: " + totalScore + " \n Highscore: " + Highscore);
                                    over.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent closure = getIntent();

                                            dialogInterface.dismiss();
                                            finish();

                                            startActivity(closure);
                                            //showInterstitial();
                                        }
                                    });

                                    over.setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            dialogInterface.dismiss();
                                            finish();

                                        }
                                    });



                                    over.setCancelable(false);
                                    AlertDialog al = over.create();


                                    al.setOwnerActivity(Home.this);

                                    if (Home.this != null && !Home.this.isFinishing())

                                        al.show();
                                    if (Home.this.isFinishing())
                                        al.dismiss();

                                    paused = true;
                                    resetTime = true;
                                    isGameOver = true;
                                }



                            }

                        }
                    });
                }
            }
        });
        th.start();




    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {






        if (keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0)
        {
            //backButtonPressed=true;

            backPressed++;
            if (backPressed >=2)
            {
                finish();
                return true;
            }

            Toast.makeText(Home.this,"press return key again to exit",Toast.LENGTH_LONG).show();



            //showExitDialog();


            return true;
        }

        //else if (keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount()==1)
        //{
          //  finish();
        //}
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {





        //paused=true;
        Log.e("back key", "pressed");
        super.onBackPressed();
    }

    /**
    private void showExitDialog() {


        paused=true;

        Log.e("back key", "pressed so paused");

        final AlertDialog.Builder exiter =new AlertDialog.Builder(Home.this);


        exiter.setView(getLayoutInflater().inflate(R.layout.wrong, null));
        exiter.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                dialogInterface.dismiss();
                finish();
            }
        });

        exiter.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                paused=false;
                dialogInterface.dismiss();

                backButtonPressed=false;
                dialogInterface.cancel();
            }
        });

        /**
        exiter.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {

                finish();
            }
        });



        exiter.setCancelable(false);
        AlertDialog alert=exiter.create();
        alert.setOwnerActivity(Home.this);


        alert.show();
    }
**/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("now","HOme activity destroyed");


        /**
        if (alert !=null && alert.isShowing())
            alert.dismiss();
**/
    }



    public int sign(int a, int b)
    {
        signNum++;
        if(signNum >=4)
            signNum=1;
        choice =(int)(Math.random() *3);
        int temp1;




        switch (choice)
        {
            case 0:
                temp1=add(a,b);
                signs[signNum-1]="+";
                Log.d("run","+");
                break;
            case 1:
                temp1=sub(a,b);
                signs[signNum-1]="-";
                Log.d("run","-");
                break;
            default:
                temp1=times(a,b);
                signs[signNum-1]="x";
                Log.d("run","x");
                break;
            }



        Log.d("run","return sign");
        return temp1;

    }

    public int add(int a, int b)
    {
        return a+b;
    }
    public int sub(int a, int b)
    {
        return a-b;
    }
    public int times(int a, int b)
    {
        return a *b;
    }

    public boolean checkCorrect()
    {
        if(!edit1.getText().toString().equals("") &&
                !edit2.getText().toString().equals("") &&
                !edit3.getText().toString().equals("") &&
                !edit4.getText().toString().equals("") )
        {

            int t1;
            int t2;
            int t3;
            //boolean a;
            //boolean b;
            boolean c;

            int tempRes1;
            int tempRes2;
            int tempRes3;


            if(signs[0].equals("+"))
            {
                t1=Integer.parseInt(edit1.getText().toString()) + Integer.parseInt(edit2.getText().toString());
                tempRes1=one + two;
                //a= (t1==tempRes1);
            }

            else if (signs[0].equals("-"))
            {
                t1=Integer.parseInt(edit1.getText().toString()) - Integer.parseInt(edit2.getText().toString());
                tempRes1=one-two;
                //a= (t1==tempRes1);
            }

            else
            {
                t1=Integer.parseInt(edit1.getText().toString()) * Integer.parseInt(edit2.getText().toString());
                tempRes1=one * two;
                //a=(t1==tempRes1);
            }



            if (signs[1].equals("+"))
            {
                t2 = t1 + Integer.parseInt(edit3.getText().toString());
                tempRes2=tempRes1 +three;
                //b=(t2==tempRes2);
            }

            else if (signs[1].equals("-"))
            {
                t2 = t1 - Integer.parseInt(edit3.getText().toString());
                tempRes2=tempRes1 - three;
                //b=(t2==tempRes2);
            }
            else
            {
                t2 = t1 * Integer.parseInt(edit3.getText().toString());
                tempRes2=tempRes1 * three;
                //b=(t2==tempRes2);
            }

            if (signs[2].equals("+"))
            {
                t3 = t2 + Integer.parseInt(edit4.getText().toString());
                tempRes3=tempRes2 + four;
                c=(t3==tempRes3);
            }

            else if (signs[2].equals("-"))
            {
                t3 = t2 - Integer.parseInt(edit4.getText().toString());
                tempRes3=tempRes2 - four;
                c=(t3==tempRes3);
            }
            else
            {
                t3 = t2 * Integer.parseInt(edit4.getText().toString());
                tempRes3=tempRes2 * four;
                c=(t3==tempRes3);
            }


        if ( c && !timedout)
        {
                totalScore++;
                score.setText(""+totalScore);
                resetTime=true;
                edit1.setText("");
                edit2.setText("");
                edit3.setText("");
                edit4.setText("");

            edit1.setBackgroundResource(R.drawable.sss);
            edit2.setBackgroundResource(R.drawable.sss);
            edit3.setBackgroundResource(R.drawable.sss);
            edit4.setBackgroundResource(R.drawable.sss);

            Log.e("button"," Enabled again");
                choice1.setEnabled(true);
                choice2.setEnabled(true);
                choice3.setEnabled(true);
                choice4.setEnabled(true);

            choice1.setBackgroundResource(R.drawable.statt);
            choice2.setBackgroundResource(R.drawable.statt);
            choice3.setBackgroundResource(R.drawable.statt);
            choice4.setBackgroundResource(R.drawable.statt);
                return true;
            } else
                //isGameOver = true;
            return false;
        }

        return false;
    }

    public void clearSelection(View view) {
        /**
        edit1.setText("");
        edit2.setText("");
        edit3.setText("");
        edit4.setText("");

        choice1.setEnabled(true);
        choice2.setEnabled(true);
        choice3.setEnabled(true);
        choice4.setEnabled(true);

         **/

        TextView textView=(TextView)findViewById(view.getId());

        String t=textView.getText().toString();
        textView.setText("");
        textView.setBackgroundResource(R.drawable.sss);

        if (t.equals(choice1.getText().toString()) && !choice1.isEnabled())
        {
            choice1.setEnabled(true);
            choice1.setBackgroundResource(R.drawable.statt);
        }

        else if (t.equals(choice2.getText().toString()) && !choice2.isEnabled())
        {
            choice2.setEnabled(true);
            choice2.setBackgroundResource(R.drawable.statt);
        }

        else if (t.equals(choice3.getText().toString()) && !choice3.isEnabled())
        {

            choice3.setEnabled(true);
            choice3.setBackgroundResource(R.drawable.statt);
        }

        else
        {
            choice4.setEnabled(true);
            choice4.setBackgroundResource(R.drawable.statt);
        }








    }

    public void inputNumber(View view) {
        Button i=(Button)findViewById(view.getId());
        String number =i.getText().toString();
        i.setEnabled(false);

        i.setBackgroundResource(R.drawable.cover);
        if(edit1.getText().toString().equals(""))
        {
            edit1.setText(number);
            edit1.setBackgroundResource(R.drawable.text);
        }
        else if(edit2.getText().toString().equals(""))
        {
            edit2.setText(number);
            edit2.setBackgroundResource(R.drawable.text);
        }
        else if (edit3.getText().toString().equals(""))
        {
            edit3.setText(number);
            edit3.setBackgroundResource(R.drawable.text);
        }
        else if (edit4.getText().toString().equals(""))
        {
            edit4.setText(number);
            edit4.setBackgroundResource(R.drawable.text);
        }
        else
            return;
    }



}
