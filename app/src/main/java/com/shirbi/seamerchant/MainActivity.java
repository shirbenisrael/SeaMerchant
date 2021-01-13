package com.shirbi.seamerchant;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.games.AnnotatedData;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.LeaderboardsClient;
import com.google.android.gms.games.leaderboard.LeaderboardScore;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.HashSet;
import java.util.Set;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {
    Logic mLogic;
    FrontEnd mFrontEnd;
    FrontEndMarket mFrontEndMarket;
    FrontEndBank mFrontEndBank;
    FrontEndSail mFrontEndSail;
    FrontEndPirates mFrontEndPirates;
    FrontEndAbandonedShip mFrontEndAbandonedShip;
    FrontEndBadWeatherInSail mFrontEndBadWeatherInSail;
    FrontEndNegotiation mFrontEndNegotiation;
    FrontEndFixShip mFrontEndFixShip;
    FrontEndShoal mFrontEndShoal;
    FrontEndSink mFrontEndSink;
    FrontEndHighScore mFrontEndHighScore;
    FrontEndMedal mFrontEndMedal;
    FrontEndOpenWindow mFrontEndOpenWindow;

    boolean mIsGameEnded = false;
    boolean mIsSoundEnable = true;

    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLogic = new Logic(this);
        restoreState();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN).
                requestScopes(Games.SCOPE_GAMES).
                requestScopes(Games.SCOPE_GAMES_LITE).
                build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account == null) {
            signIn();
        } else {
            getLeaderBoard();
        }

        mFrontEnd = new FrontEnd(this);
        mFrontEndMarket = new FrontEndMarket(this);
        mFrontEndBank = new FrontEndBank(this);
        mFrontEndSail = new FrontEndSail(this);
        mFrontEndPirates = new FrontEndPirates(this);
        mFrontEndAbandonedShip = new FrontEndAbandonedShip(this);
        mFrontEndBadWeatherInSail = new FrontEndBadWeatherInSail(this);
        mFrontEndNegotiation = new FrontEndNegotiation(this);
        mFrontEndFixShip = new FrontEndFixShip(this);
        mFrontEndShoal = new FrontEndShoal(this);
        mFrontEndSink = new FrontEndSink(this);
        mFrontEndHighScore = new FrontEndHighScore(this);
        mFrontEndMedal = new FrontEndMedal(this);
        mFrontEndOpenWindow = new FrontEndOpenWindow(this);

        mFrontEnd.showWindow(Window.OPEN_WINDOW);
        mFrontEndOpenWindow.startAnimate();
        playSound(R.raw.open_screen);
    }

    public void onExitOpenScreen(View view) {
        mFrontEnd.showWindow(Window.MAIN_WINDOW);
        mFrontEnd.showState();
    }

    public void onMarketClick(View view) {
        Goods goods = mFrontEnd.viewToGoods(view);
        mLogic.initMarketDeal(goods);

        mFrontEndMarket.onMarketClick();
    }

    public void onMarketMinusTenClick(View view) {
        mLogic.mMarketDeal.removeGoods(10);
        mFrontEndMarket.showDealState();
    }

    public void onMarketMinusOneClick(View view) {
        mLogic.mMarketDeal.removeGoods(1);
        mFrontEndMarket.showDealState();
    }

    public void onMarketPlusOneClick(View view) {
        mLogic.mMarketDeal.addGoods(1);
        mFrontEndMarket.showDealState();
    }

    public void onMarketPlusTenClick(View view) {
        mLogic.mMarketDeal.addGoods(10);
        mFrontEndMarket.showDealState();
    }

    public void onMarketBuyAllClick(View view) {
        mLogic.mMarketDeal.buyAll();
        mFrontEndMarket.showDealState();
    }

    public void onMarketSellAllClick(View view) {
        mLogic.mMarketDeal.sellAll();
        mFrontEndMarket.showDealState();
    }

    public void onMarketFillCapacityClick(View view) {
        mLogic.mMarketDeal.fillCapacity();
        mFrontEndMarket.showDealState();
    }

    public void onMarketPercentageForGuardsClick(View view) {
        mLogic.mMarketDeal.leaveCashForGuards();
        mFrontEndMarket.showDealState();
    }

    public void onDealDoneClick(View view) {
        playSound(R.raw.deal_done);
        mLogic.applyMarketDeal();
        mFrontEnd.showState();

        if (checkForNewMedal()) {
            return;
        }

        mFrontEnd.showWindow(Window.MAIN_WINDOW);
        if (mLogic.calculateLoad() > 0) {
            mFrontEnd.blinkSail();
        } else {
            mFrontEnd.blinkMarket();
        }
    }

    public void onDealCancelClick(View view) {
        mFrontEnd.showWindow(Window.MAIN_WINDOW);
    }

    public void onFlagClick(View view) {
        State destination = mFrontEnd.viewToState(view);
        if (mLogic.getSailDuration(destination) == 0) {
            return;
        }

        if (mLogic.getSailDuration(destination) == -1) {
            playSound(R.raw.cannot);
            mFrontEnd.showAlertDialogMessage(getString(R.string.GREECE_IS_TOO_FAR),
                    getString(R.string.CANNOT_SAIL_TITLE));
            return;
        }

        if (mLogic.getDayPart() == DayPart.NIGHT) {
            playSound(R.raw.cannot);
            mFrontEnd.showAlertDialogMessage(getString(R.string.CANNOT_SAIL_AT_NIGHT),
                    getString(R.string.CANNOT_SAIL_TITLE));
            return;
        }

        if (mLogic.isDamagePreventSail()) {
            playSound(R.raw.cannot);
            mFrontEnd.showAlertDialogMessage(getString(R.string.CANNOT_SAIL_WITH_HEAVY_DAMAGE),
                    getString(R.string.CANNOT_SAIL_TITLE));
            return;
        }

        if (!mLogic.canReachToDestinationBeforeSleepTime(destination)) {
            playSound(R.raw.cannot);
            mFrontEnd.showAlertDialogMessage(getString(R.string.CANNOT_REACH_BEFORE_SLEEP_TIME),
                    getString(R.string.CANNOT_SAIL_TITLE));
            return;
        }

        mLogic.initSail(destination);
        mFrontEnd.showSailWarning();
    }

    public void onDangerPiratesClick(View view) {
        mFrontEndSail.showAlertDialogMessage(getString(R.string.DANGER_PIRATES_MESSAGE), getString(R.string.PIRATES));
    }

    public void onDangerOverloadClick(View view) {
        mFrontEndSail.showAlertDialogMessage(getString(R.string.DANGER_OVERLOAD_MESSAGE),
                getString(R.string.DANGER_OVERLOAD_TITLE));
    }

    public void onDangerNightClick(View view) {
        mFrontEndSail.showAlertDialogMessage(getString(R.string.DANGER_NIGHT_SAIL_SHORT_MESSAGE),
                getString(R.string.DANGER_NIGHT_SAIL_TITLE));
    }

    public void onDangerBrokenShipClick(View view) {
        mFrontEndSail.showAlertDialogMessage(getString(R.string.DANGER_BROKEN_SHIP_MESSAGE),
                getString(R.string.DANGER_BROKEN_SHIP_TITLE));
    }

    public void onDangerWeatherClick(View view) {
        mFrontEndSail.showAlertWeatherMessage();
    }

    public void onSailCancelClick(View view) {
        mFrontEnd.showWindow(Window.MAIN_WINDOW);
    }

    public void onSailClick(View view) {
        playSound(R.raw.sail);
        mLogic.startSail();
        mFrontEndSail.startSail();
    }

    public void onSailEndClick(View view) {
        mFrontEnd.showWindow(Window.MAIN_WINDOW);
        mFrontEnd.showState();

        if (checkForNewMedal()) {
            return;
        }

        mFrontEnd.blinkMarket();
    }

    public void onSleepClick(View view) {
        mFrontEnd.showSleepQuestion();
        mFrontEnd.showWindow(Window.SLEEP_WINDOW);
    }

    public void onCancelSleep(View view) {
        mFrontEnd.showWindow(Window.MAIN_WINDOW);
    }

    public void onApproveSleep(View view) {
        if (!mLogic.mCurrentDay.isLastDay()) {
            playSound(R.raw.new_day);
            mLogic.startNewDay();
            mFrontEnd.showWindow(Window.WEATHER_WINDOW);
            mFrontEnd.showNewWeather();
        } else {
            mIsGameEnded = true;
            playSound(R.raw.game_end);
            mFrontEndHighScore.showHighScoreAtGameEnd();
            mFrontEnd.showWindow(Window.HIGH_SCORE_WINDOW);

            int currentScore = mLogic.calculateTotalValue();
            mLogic.setNewHighScore(currentScore);

            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
            if (account == null) {
                signIn();
            } else {
                LeaderboardsClient client = Games.getLeaderboardsClient(this, account);
                client.submitScore(getString(R.string.leaderboard_highscore), mLogic.mHighScore);
            }
        }
    }

    private static final int RC_SIGN_IN = 9001;

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            getLeaderBoard();
            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());

            mFrontEnd.showAlertDialogMessage("signInResult:failed code=" + e.getStatusCode(), "");
            updateUI(null);
        }
    }

    private void updateUI(GoogleSignInAccount account) {

    }

    private void getLeaderBoard() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        LeaderboardsClient client = Games.getLeaderboardsClient(this, account);

        client.loadCurrentPlayerLeaderboardScore(getString(R.string.leaderboard_highscore),
                LeaderboardVariant.TIME_SPAN_ALL_TIME, LeaderboardVariant.COLLECTION_PUBLIC)
                .addOnSuccessListener(this, new OnSuccessListener<AnnotatedData<LeaderboardScore>>() {
                    @Override
                    public void onSuccess(AnnotatedData<LeaderboardScore> leaderboardScoreAnnotatedData) {
                        int score = 0;
                        if (leaderboardScoreAnnotatedData != null) {
                            if (leaderboardScoreAnnotatedData.get() != null) {
                                score = (int)leaderboardScoreAnnotatedData.get().getRawScore();
                                mLogic.setNewHighScore(score);
                            } else {
                                Toast.makeText(MainActivity.this, "no data at .get()", Toast.LENGTH_SHORT).show();
                                mFrontEnd.showAlertDialogMessage("LeaderBoard: .get() is null","");
                            }
                        } else {
                            mFrontEnd.showAlertDialogMessage("no data","");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mFrontEnd.showAlertDialogMessage("FAILURE " + e,"");
                    }
                });
    }

    public void startNewGame() {
        playSound(R.raw.new_day);
        mLogic.startNewGame();
        mFrontEnd.showWindow(Window.WEATHER_WINDOW);
        mFrontEnd.showNewWeather();
    }

    public void onHighScoreClick(View view) {
        mFrontEnd.showWindow(Window.HIGH_SCORE_WINDOW);
        mFrontEndHighScore.showHighScoreWhilePlaying();
    }

    public void onExistHighScore(View view) {
        if (mIsGameEnded) {
            mIsGameEnded = false;
            startNewGame();
        } else {
            mFrontEnd.showWindow(Window.MENU_WINDOW);
        }
    }

    public void onExitWeatherWindow(View view) {
        if (mLogic.mCurrentDay != WeekDay.SUNDAY) {
            mLogic.generateNewDayEvent();
            if (mLogic.mNewDayEvent == Logic.NewDayEvent.STRIKE) {
                playSound(R.raw.protest);
                mFrontEnd.showWindow(Window.STRIKE_WINDOW);
            } else {
                mFrontEnd.showWindow(Window.SIMPLE_NEW_DAY_EVENT_WINDOW);
                mFrontEnd.showNewEvent();
            }
        } else {
            mFrontEnd.showWindow(Window.MAIN_WINDOW);
            mFrontEnd.showState();
        }
    }

    public void onNewCrewClick(View view) {
        mFrontEnd.showWindow(Window.SIMPLE_NEW_DAY_EVENT_WINDOW);
        mFrontEnd.showNewCrewNextDay();
        mLogic.findNewCrew();
    }

    public void onExitSimpleNewDayEventWindow(View view) {
        if (mLogic.mNewDayEvent == Logic.NewDayEvent.STRIKE) {
            if (mLogic.mLoseDayByStrike > 0) {
                mLogic.mCurrentDay = mLogic.mCurrentDay.add(mLogic.mLoseDayByStrike);
                mLogic.mLoseDayByStrike = 0;
                mFrontEnd.showWindow(Window.WEATHER_WINDOW);
                mFrontEnd.showNewWeather();
            } else {
                mFrontEnd.showWindow(Window.MAIN_WINDOW);
                mFrontEnd.showState();
            }
        } else {
            mFrontEnd.showWindow(Window.MAIN_WINDOW);
            mFrontEnd.showState();
        }

        if (checkForNewMedal()) {
            return;
        }
    }

    public void onAcceptOffer(View view) {
        switch (mLogic.mNewDayEvent) {
            case MERCHANT:
                playSound(R.raw.deal_done);
                break;
            case BIGGER_SHIP_OFFER:
                playSound(R.raw.fix);
                break;
        }
        mLogic.acceptOffer();
        mFrontEnd.showWindow(Window.MAIN_WINDOW);
        mFrontEnd.showState();

        if (checkForNewMedal()) {
            return;
        }
    }

    public void onGuardShipClick(View view) {
        mFrontEndSail.guardShipClick(view);
    }

    public void onAttackClick(View view) {
        playSound(R.raw.battle);
        mLogic.mSail.calculateBattleResult();
        if (mLogic.mSail.mBattleResult == Sail.BattleResult.LOSE) {
            mFrontEndPirates.showLoseToPiratesMessage();
        } else {
            mFrontEndPirates.showWinPiratesMessage();
        }
        mFrontEnd.showWindow(Window.PIRATES_ATTACK_WINDOW);
    }

    public void onEscapeClick(View view) {
        if (mLogic.mSail.isEscapePiratesSucceeds()) {
            playSound(R.raw.escape);
            mFrontEnd.showWindow(Window.ESCAPE_WINDOW);
        } else {
            mFrontEndPirates.showFailEscapeMessage();
        }
    }

    public void onNegotiateClick(View view) {
        playSound(R.raw.negotiate);
        mFrontEnd.showWindow(Window.NEGOTIATE_WINDOW);
        mFrontEndNegotiation.showNegotiation();
    }

    public void onCancelOffer(View view) {
        if (mLogic.mNegotiationType == Logic.NegotiationType.PIRATES) {
            mFrontEnd.showWindow(Window.PIRATES_WINDOW);
        } else {
            mFrontEnd.showWindow(Window.STRIKE_WINDOW);
        }
    }

    public void onSendOfferToPirates(View view) {
        if (mFrontEndNegotiation.sendOffer()) {
            playSound(R.raw.agreement);
            if (mLogic.mNegotiationType == Logic.NegotiationType.PIRATES) {
                mFrontEnd.showWindow(Window.PIRATE_ACCEPT_OFFER_WINDOW);
                mLogic.mSail.negotiationSucceeds();
            } else {
                mFrontEnd.showWindow(Window.SIMPLE_NEW_DAY_EVENT_WINDOW);
                mFrontEnd.showCrewNegotiationSucceed();
            }
        } else {
            playSound(R.raw.booing);
            if (mLogic.mNegotiationType == Logic.NegotiationType.PIRATES) {
                mFrontEndPirates.showPiratesRefuseOffer();
                mFrontEnd.showWindow(Window.PIRATES_WINDOW);
            } else {
                mFrontEnd.showWindow(Window.SIMPLE_NEW_DAY_EVENT_WINDOW);
                mFrontEnd.showCrewNegotiationFail();
            }
        }
    }

    public void onEscapeDoneClick(View view) {
        mFrontEnd.showWindow(Window.SAIL_WINDOW);
        mFrontEndSail.continueSail();
    }

    public void onOfferAcceptedClick(View view) {
        mFrontEnd.showWindow(Window.SAIL_WINDOW);
        mFrontEndSail.continueSail();
    }

    public void onBattleWinDoneClick(View view) {
        mFrontEnd.showWindow(Window.SAIL_WINDOW);
        mFrontEndSail.continueSail();
    }

    public void showPirates() {
        playSound(R.raw.pirates);
        mLogic.mNegotiationType = Logic.NegotiationType.PIRATES;
        mFrontEnd.showWindow(Window.PIRATES_WINDOW);
        mFrontEndPirates.showChances();
    }

    public void showAbandonedShip() {
        playSound(R.raw.abandoned_ship);
        mLogic.mSail.createAbandonedShip();
        mFrontEnd.showWindow(Window.ABANDONED_SHIP_WINDOW);
        mFrontEndAbandonedShip.showAbandonedShip();
    }

    public void showShoal() {
        mLogic.mSail.createShoal();
        mFrontEnd.showWindow(Window.SHOAL_WINDOW);
        mFrontEndShoal.showShoal();
    }

    public void onExitSimpleSeaEvent(View view) {
        mFrontEnd.showWindow(Window.SAIL_WINDOW);
        mFrontEndSail.continueSail();
    }

    public void showSink() {
        playSound(R.raw.sink);
        mLogic.mSail.createSink();
        mFrontEnd.showWindow(Window.SINK_WINDOW);
        mFrontEndSink.showSink();
    }

    public void onSinkProgress(View view) {
        if (mLogic.mSail.isSinkInSail()) {
            showSink();
        } else {
            onExitSimpleSeaEvent(view);
        }
    }

    public void showBadWeatherInSail() {
        mLogic.mSail.createBadWeatherInSail();
        mFrontEnd.showWindow(Window.BAD_WEATHER_IN_SAIL_WINDOW);
        mFrontEndBadWeatherInSail.showBadWeather();
    }

    public void onExistBadWeatherClick(View view) {
        mFrontEnd.showWindow(Window.SAIL_WINDOW);
        mFrontEndSail.continueSail();
    }

    public void onBankClick(View view) {
        if (!mLogic.canGoToBank()) {
            mFrontEnd.showAlertDialogMessage(getString(R.string.BANK_CLOSED),
                    getString(R.string.BANK_FORBIDDEN));

            mFrontEnd.blinkSleep();
            return;
        }

        mLogic.initBankDeal();
        mFrontEnd.showWindow(Window.BANK_WINDOW);
        mFrontEndBank.onBankClick();
    }

    public void onBankMinus10000Click(View view) {
        mLogic.mBankDeal.addDeposit(10000);
        mFrontEndBank.showDealState();
    }

    public void onBankMinus1000Click(View view) {
        mLogic.mBankDeal.addDeposit(1000);
        mFrontEndBank.showDealState();
    }

    public void onBankPlus1000Click(View view) {
        mLogic.mBankDeal.removeDeposit(1000);
        mFrontEndBank.showDealState();
    }

    public void onBankPlus10000Click(View view) {
        mLogic.mBankDeal.removeDeposit(10000);
        mFrontEndBank.showDealState();
    }

    public void onDrawAllClick(View view) {
        mLogic.mBankDeal.drawAll();
        mFrontEndBank.showDealState();
    }

    public void onCashForGuardsClick(View view) {
        mLogic.mBankDeal.leaveCashForGuards();
        mFrontEndBank.showDealState();
    }

    public void onDepositAllClick(View view) {
        mLogic.mBankDeal.depositAll();
        mFrontEndBank.showDealState();
    }

    public void onBankDealCancelClick(View view) {
        mFrontEnd.showWindow(Window.MAIN_WINDOW);
    }

    public void onBankDealDoneClick(View view) {
        playSound(R.raw.deal_done);
        mLogic.applyBankDeal();
        mFrontEnd.showState();
        mFrontEnd.showWindow(Window.MAIN_WINDOW);

        if (checkForNewMedal()) {
            return;
        }
    }

    public void onFixButtonClick(View view) {
        if (mLogic.isShipBroken()) {
            if (!mLogic.isEnoughTimeForFixShip()) {
                mFrontEnd.showAlertDialogMessage(getString(R.string.CANNOT_FIX_AT_NIGHT),
                        getString(R.string.CANNOT_FIX_TITLE));
                return;
            }

            mLogic.initFixShipDeal();
            mFrontEndFixShip.onFixShipClick();
            mFrontEnd.showWindow(Window.FIX_SHIP_WINDOW);
        } else {
            mFrontEnd.showAlertDialogMessage(getString(R.string.CANNOT_FIX_FIXED_SHIP),
                    getString(R.string.CANNOT_FIX_TITLE));
        }
    }

    public void fixAsPossible(View view) {
        mFrontEndFixShip.fixAsPossible();
    }

    public void onFixCancelClick(View view) {
        mFrontEnd.showWindow(Window.MAIN_WINDOW);
    }

    public void onFixDoneClick(View view) {
        playSound(R.raw.fix);
        mLogic.applyShipFixDeal();
        mFrontEnd.showState();
        mFrontEnd.showWindow(Window.MAIN_WINDOW);
    }

    public void onCancelBecauseOfDanger(View view) {
        mFrontEnd.showWindow(Window.MAIN_WINDOW);
    }

    public void onApproveDanger(View view) {
        mLogic.mSail.setNextWarning();
        if (mLogic.mSail.mWarning == null) {
            mFrontEndSail.initSailRoute();
            mFrontEnd.showWindow(Window.SAIL_WINDOW);
        } else {
            mFrontEnd.showSailWarning();
        }
    }

    // Set of all media players which are currently working. Used to prevent garbage collector from
    // clean them and stop the sounds.
    private final Set<MediaPlayer> mMediaPlayers = new HashSet<MediaPlayer>();

    public void playSound(int soundId) {
        if (!mIsSoundEnable) {
            return;
        }

        if (soundId == 0) {
            return;
        }

        MediaPlayer mediaPlayer;
        mediaPlayer = MediaPlayer.create(this, soundId);
        mMediaPlayers.add(mediaPlayer);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
                mMediaPlayers.remove(mp);
            }
        });
        mediaPlayer.start();
    }

    public void onTutorialClick(View view) {
        mFrontEnd.showTutorial();
    }

    void exit() {
        // TODO: Store state?
        super.onBackPressed();
    }

    public void onExitClick(View view) {
        mFrontEnd.showExitDialog();
    }

    @Override
    public void onBackPressed() {
        Window window = mFrontEnd.getCurrentVisibleWindow();

        switch (window)  {
            case MAIN_WINDOW:
                mFrontEnd.showExitDialog();
                break;
            case MARKET_WINDOW:
                onDealCancelClick(null);
                break;
            case BANK_WINDOW:
                onBankDealCancelClick(null);
                break;
            case DANGER_WINDOW:
            case SAIL_WINDOW:
                onSailCancelClick(null);
                break;
            case SAIL_END_WINDOW:
                onSailEndClick(null);
                break;
            case SLEEP_WINDOW:
                onCancelSleep(null);
                break;
            case BAD_WEATHER_IN_SAIL_WINDOW:
                onExistBadWeatherClick(null);
                break;
            case SINK_WINDOW:
                onSinkProgress(null);
                break;
            case SHOAL_WINDOW:
            case ABANDONED_SHIP_WINDOW:
                onExitSimpleSeaEvent(null);
                break;
            case ESCAPE_WINDOW:
                onEscapeDoneClick(null);
                break;
            case WEATHER_WINDOW:
                onExitWeatherWindow(null);
                break;
            case FIX_SHIP_WINDOW:
                onFixCancelClick(null);
                break;
            case PIRATE_ACCEPT_OFFER_WINDOW:
                onOfferAcceptedClick(null);
                break;
            case HIGH_SCORE_WINDOW:
                onExistHighScore(null);
                break;
            case PIRATES_ATTACK_WINDOW:
                onBattleWinDoneClick(null);
                break;
            case SIMPLE_NEW_DAY_EVENT_WINDOW:
                onExitSimpleNewDayEventWindow(null);
                break;
            case NEGOTIATE_WINDOW:
                onCancelOffer(null);
                break;
            case MENU_WINDOW:
                onExitMenu(null);
                break;
            case MEDAL_WINDOW:
                onExitMedalClick(null);
                break;
            case ABOUT_WINDOW:
                onExitAboutWindow(null);
                break;
            case HELP_WINDOW:
                onExitHelpClick(null);
                break;
            case STRIKE_WINDOW:
            case PIRATES_WINDOW:
                break;
        }
    }

    public void onExitAboutWindow(View view) {
        mFrontEnd.showWindow(Window.MENU_WINDOW);
    }

    public void onPriceClick(View view) {
        mFrontEnd.priceClick(view);
    }

    public void onMedalClick(View view) {
        mFrontEnd.showWindow(Window.MEDAL_WINDOW);
    }

    public void onExitMedalClick(View view) {
        mFrontEnd.showWindow(Window.MAIN_WINDOW);
    }

    public void onExitMenu(View view) {
        mFrontEnd.showWindow(Window.MAIN_WINDOW);
    }

    public void onMenuClick(View view) {
        mFrontEnd.showWindow(Window.MENU_WINDOW);
    }

    public void onStartNewGameClick(View view) {
        mFrontEnd.showNewGameDialog();
    }

    private void storeState() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        mLogic.storeState(editor);
        editor.apply();
    }

    private void restoreState() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        mLogic.restoreState(sharedPref);
    }

    @Override
    protected void onDestroy() {
        storeState();
        super.onDestroy();
    }

    public void onOneMedalClick(View view) {
        mFrontEndMedal.showMedalCondition(view);
    }

    private boolean checkForNewMedal() {
        Medal medal = mLogic.acquireNewMedal();
        if (medal != null) {
            playSound(R.raw.agreement);
            mFrontEnd.showWindow(Window.NEW_MEDAL_WINDOW);
            mFrontEndMedal.showNewMedal(medal);
            return true;
        } else {
            return false;
        }
    }

    public void onExitNewMedal(View view) {
        if (checkForNewMedal()) {
            return;
        } else {
            mFrontEnd.showWindow(Window.MAIN_WINDOW);
        }
    }

    public void onAboutClick(View view) {
        mFrontEnd.showWindow(Window.ABOUT_WINDOW);
    }

    public void onHelpClick(View view) {
        mFrontEnd.showWindow(Window.HELP_WINDOW);
    }

    public void onExitHelpClick(View view) {
        mFrontEnd.showWindow(Window.MENU_WINDOW);
    }

    public void onStatsClick(View view) {
        mFrontEnd.showStats();
    }

    public void onGameRulesClick(View view) {
        mFrontEnd.showGameRules();
    }
}