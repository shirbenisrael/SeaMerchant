package com.shirbi.seamerchant;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends Activity {
    Logic mLogic;
    BackEndGoogleApi mBackEndGoogleApi;
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
    FrontEndMoreDamage mFrontEndMoreDamage;
    FrontEndHighScore mFrontEndHighScore;
    FrontEndMedal mFrontEndMedal;
    FrontEndOpenWindow mFrontEndOpenWindow;
    FrontEndFortuneTeller mFrontEndFortuneTeller;
    StartTutorial mStartTutorial;

    boolean mIsGameEnded = false;
    boolean mIsSoundEnable = false;
    boolean mIsFastAnimation = false;
    boolean mIsGoogleSignIn = false;

    boolean mIsStartTutorialActive = false;

    String mCurrentLanguage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLogic = new Logic(this);
        restoreState();

        mBackEndGoogleApi = new BackEndGoogleApi(this);

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
        mFrontEndMoreDamage = new FrontEndMoreDamage(this);
        mFrontEndHighScore = new FrontEndHighScore(this);
        mFrontEndMedal = new FrontEndMedal(this);
        mFrontEndOpenWindow = new FrontEndOpenWindow(this);
        mFrontEndFortuneTeller = new FrontEndFortuneTeller(this);
        mStartTutorial = new StartTutorial(this);

        mFrontEnd.showWindow(Window.OPEN_WINDOW);
        mFrontEndOpenWindow.startAnimate();
        playSound(R.raw.open_screen);

        if (mCurrentLanguage != null) {
            mFrontEnd.setLocale(mCurrentLanguage);
        }

        ((ProgressGraphCanvas)findViewById(R.id.progress_canvas_layout)).mLogic = mLogic;
    }

    public void onExitOpenScreen(View view) {
        mFrontEnd.showWindow(Window.MAIN_WINDOW);
        mFrontEnd.showState();
        if (mIsStartTutorialActive) {
            startTutorial();
        }
    }

    public void onMarketClick(View view) {
        mFrontEnd.stopBlinks();

        Goods goods = mFrontEnd.viewToGoods(view);

        if (mIsStartTutorialActive) {
            mStartTutorial.onMarketClick(goods);
            return;
        }

        if (!mLogic.canGoToMarket()) {
            mFrontEnd.showErrorMessage(getString(R.string.MARKET_CLOSED),
                    getString(R.string.MARKET_FORBIDDEN));

            mFrontEnd.blinkSleep();
            return;
        }

        mLogic.initMarketDeal(goods);
        mFrontEndMarket.onMarketClick();
    }

    public void onMarketMinusTenClick(View view) {
        if (mLogic.mMarketDeal != null) {
            mLogic.mMarketDeal.removeGoods(10);
            mFrontEndMarket.showDealState();
        }
    }

    public void onMarketMinusOneClick(View view) {
        if (mLogic.mMarketDeal != null) {
            mLogic.mMarketDeal.removeGoods(1);
            mFrontEndMarket.showDealState();
        }
    }

    public void onMarketPlusOneClick(View view) {
        if (mLogic.mMarketDeal != null) {
            mLogic.mMarketDeal.addGoods(1);
            mFrontEndMarket.showDealState();
        }
    }

    public void onMarketPlusTenClick(View view) {
        if (mLogic.mMarketDeal != null) {
            mLogic.mMarketDeal.addGoods(10);
            mFrontEndMarket.showDealState();
        }
    }

    public void onMarketBuyAllClick(View view) {
        if (mLogic.mMarketDeal != null) {
            mLogic.mMarketDeal.buyAll();
            mFrontEndMarket.showDealState();
        }
    }

    public void onMarketSellAllClick(View view) {
        if (mLogic.mMarketDeal != null) {
            mLogic.mMarketDeal.sellAll();
            mFrontEndMarket.showDealState();
        }
    }

    public void onMarketFillCapacityClick(View view) {
        if (mLogic.mMarketDeal != null) {
            mLogic.mMarketDeal.fillCapacity();
            mFrontEndMarket.showDealState();
        }
    }

    public void onMarketPercentageForGuardsClick(View view) {
        if (mLogic.mMarketDeal != null) {
            mLogic.mMarketDeal.leaveCashForGuards();
            mFrontEndMarket.showDealState();
        }
    }

    public void onDealDoneClick(View view) {
        playSound(R.raw.deal_done);
        mLogic.applyMarketDeal();
        mFrontEnd.showState();

        if (checkForNewMedal()) {
            return;
        }

        mFrontEnd.showWindow(Window.MAIN_WINDOW);

        if (mIsStartTutorialActive) {
            mStartTutorial.onMarketDealDoneClick();
        } else {
            mFrontEnd.blinkButtons();
            storeState();
        }
    }

    public void onDealCancelClick(View view) {
        mFrontEnd.showWindow(Window.MAIN_WINDOW);
    }

    public void onFlagClick(View view) {
        mFrontEnd.stopBlinks();

        State destination = mFrontEnd.viewToState(view);

        if (mLogic.getSailDuration(destination) == 0) {
            return;
        }

        if (mLogic.getSailDuration(destination) == -1) {
            mFrontEnd.showErrorMessage(getString(R.string.GREECE_IS_TOO_FAR),
                    getString(R.string.CANNOT_SAIL_TITLE));
            return;
        }

        if (mIsStartTutorialActive) {
            mStartTutorial.onFlagClick(destination);
            return;
        }

        if (mLogic.getDayPart() == DayPart.NIGHT) {
            mFrontEnd.showErrorMessage(getString(R.string.CANNOT_SAIL_AT_NIGHT),
                    getString(R.string.CANNOT_SAIL_TITLE));
            return;
        }

        if (mLogic.isDamagePreventSail()) {
            mFrontEnd.showErrorMessage(getString(R.string.CANNOT_SAIL_WITH_HEAVY_DAMAGE),
                    getString(R.string.CANNOT_SAIL_TITLE));
            return;
        }

        if (!mLogic.canReachToDestinationBeforeSleepTime(destination)) {
            mFrontEnd.showErrorMessage(getString(R.string.CANNOT_REACH_BEFORE_SLEEP_TIME),
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
        if (mIsStartTutorialActive) {
            if (!mStartTutorial.checkIfCanSail()) {
                return;
            }
            mFrontEnd.cancelDelayedMessage();
        } else {
            if ((mLogic.mSail.mSelectedNumGuardShips == 0) && (mLogic.getDefaultNumGuards() != 0)) {
                mFrontEndSail.showSailWithNoGuardsDialog();
                return;
            }
        }

        startSail();
    }

    public void startSail() {
        storeInvalidState();

        playSound(R.raw.sail);
        mLogic.startSail();
        mFrontEndSail.startSail();
    }

    public void onSailEndClick(View view) {
        mFrontEnd.showWindow(Window.MAIN_WINDOW);
        mFrontEnd.showState();

        if (mIsStartTutorialActive) {
            mStartTutorial.onSailEnd();
            return;
        }

        if (checkForNewMedal()) {
            return;
        }

        mFrontEnd.blinkMarket(null);

        if (mLogic.canFixShip()) {
            mFrontEnd.blinkFixShip();
        }

        storeState();
    }

    public void onFortuneTellerClick(View view) {
        mLogic.callFortuneTeller();

        if (mLogic.canAskFortuneTeller()) {
            mFrontEnd.showWindow(Window.FORTUNE_TELLER_WINDOW);
            mFrontEndFortuneTeller.showInitialMessage();
        } else {
            if (mLogic.isInfoFromFortuneTellerAvailable()) {
                mFrontEndFortuneTeller.showFortune();
            } else {
                mFrontEndFortuneTeller.showFortuneTellerNotAvailable();
            }
        }
    }

    public void onCancelFortuneTeller(View view) {
        mFrontEnd.showWindow(Window.MAIN_WINDOW);
    }

    public void onApproveFortuneTeller(View view) {
        mLogic.askFortuneTeller();
        mFrontEndFortuneTeller.showFortune();
        mFrontEnd.showWindow(Window.MAIN_WINDOW);
        mFrontEnd.showState();
        storeState();
    }

    public void onSleepClick(View view) {
        mFrontEnd.showSleepQuestion();
        mFrontEnd.showWindow(Window.SLEEP_WINDOW);
    }

    public void onCancelSleep(View view) {
        mFrontEnd.showWindow(Window.MAIN_WINDOW);
    }

    public void onApproveContinuePlayAfterEnd() {
        playSound(R.raw.new_day);
        mLogic.startNewDay();
        mFrontEnd.showWindow(Window.WEATHER_WINDOW);
        mFrontEnd.showNewWeather();
    }

    public void onApproveSleep(View view) {
        storeInvalidState();

        mFrontEnd.stopBlinks();

        if (mLogic.isAfterEnd() || !mLogic.mCurrentDay.isLastDay()) {
            playSound(R.raw.new_day);
            mLogic.startNewDay();
            if (mIsStartTutorialActive) {
                mStartTutorial.startNewDay();
            }
            mFrontEnd.showWindow(Window.WEATHER_WINDOW);
            mFrontEnd.showNewWeather();
        } else {
            mIsGameEnded = true;
            playSound(R.raw.game_end);
            mFrontEndHighScore.showHighScoreAtGameEnd();
            mFrontEnd.showWindow(Window.HIGH_SCORE_WINDOW);

            long currentScore = mLogic.calculateTotalValue();
            mLogic.setNewHighScore(currentScore);
            mLogic.setNewHighCapacity(mLogic.mCapacity);

            mBackEndGoogleApi.submitScore();
        }
    }

    public static final int RC_SIGN_IN = 9001;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            mBackEndGoogleApi.handleSignInResult(task);
        }
    }

    public void startNewGame() {
        if (mIsStartTutorialActive) {
            endTutorial();
        }
        playSound(R.raw.new_day);
        mLogic.startNewGame();
        mFrontEnd.showWindow(Window.WEATHER_WINDOW);
        mFrontEnd.showNewWeather();
    }

    public void onHighScoreClick(View view) {
        mFrontEnd.showWindow(Window.HIGH_SCORE_WINDOW);
        mFrontEndHighScore.showHighScoreWhilePlaying();

        if (!mIsGoogleSignIn) {
            mFrontEnd.showSignGoogleDialog();
        }
    }

    public void onExistHighScore(View view) {
        if (mIsGameEnded) {
            mIsGameEnded = false;
            mFrontEnd.showNewGameOrContinuePlay();
        } else {
            mFrontEnd.showWindow(Window.MENU_WINDOW);
        }
    }

    public void onExitWeatherWindow(View view) {
        if (mIsStartTutorialActive) {
            mFrontEnd.showWindow(Window.MAIN_WINDOW);
            mStartTutorial.startNewDayAfterWeather();
            return;
        }
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
            storeState();
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
                mLogic.loseDaysByStrike();
                mFrontEnd.showWindow(Window.WEATHER_WINDOW);
                mFrontEnd.showNewWeather();
            } else {
                mFrontEnd.showWindow(Window.MAIN_WINDOW);
                mFrontEnd.showState();
                storeState();
            }
        } else {
            mFrontEnd.showWindow(Window.MAIN_WINDOW);
            mFrontEnd.showState();
            storeState();
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
        storeState();

        if (checkForNewMedal()) {
            return;
        }
    }

    public void onGuardShipClick(View view) {
        if (view.getParent() == findViewById(R.id.guards_layout)) {
            mFrontEndSail.guardShipClick(view);
        } else {
            mFrontEnd.setDefaultGuardShipClick(view);
            // default guard layout from menu.
        }
    }

    public void onCapturePiratesClick(View view) {
        mLogic.mSail.calculateCapturePiratesPrize();
        mFrontEndPirates.showCapturePrize();
    }

    public void onRobPiratesClick(View view) {
        mLogic.mSail.calculateRobPiratesPrize();
        mFrontEndPirates.showRobPrize();
    }

    public void onAttackClick(View view) {
        playSound(R.raw.battle);

        if (mIsStartTutorialActive) {
            mStartTutorial.attackPirates();
            return;
        }

        mLogic.mSail.calculateBattleResult();

        if (mLogic.mSail.mBattleResult == Sail.BattleResult.LOSE) {
            mFrontEndPirates.showLoseToPiratesMessage();
        } else {
            mFrontEndPirates.showWinPiratesMessage();
        }
        mFrontEnd.showWindow(Window.PIRATES_ATTACK_WINDOW);
    }

    public void onEscapeClick(View view) {
        if (mIsStartTutorialActive || mLogic.mSail.isEscapePiratesSucceeds()) {
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
        playSound(R.raw.fish_boat);
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
        if (mIsStartTutorialActive) {
            mStartTutorial.createSink();
        } else {
            mLogic.mSail.createSink();
        }
        mFrontEnd.showWindow(Window.SINK_WINDOW);
        mFrontEndSink.showSink();
    }

    public void showMoreDamage() {
        playSound(R.raw.fish_boat);
        mLogic.mSail.createMoreDamage();
        mFrontEnd.showWindow(Window.MORE_DAMAGE_WINDOW);
        mFrontEndMoreDamage.showDamage();
    }

    public void onSinkProgress(View view) {
        if ((!mIsStartTutorialActive) && mLogic.mSail.isSinkInSail()) {
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

    public void onExitBadWeatherClick(View view) {
        mFrontEnd.showWindow(Window.SAIL_WINDOW);
        mFrontEndSail.continueSail();
    }

    public void onSelectNewStateAfterFogClick(View view) {
        mFrontEnd.showWindow(Window.SAIL_WINDOW);
        mFrontEndBadWeatherInSail.selectDestinationAfterFog(view);
        mFrontEndSail.continueSail();
    }


    public void onBankClick(View view) {
        mFrontEnd.stopBlinks();

        if (mIsStartTutorialActive) {
            mStartTutorial.onBankClick();
            return;
        }

        if (!mLogic.canGoToBank()) {
            mFrontEnd.showErrorMessage(getString(R.string.BANK_CLOSED),
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

        if (mIsStartTutorialActive) {
            mStartTutorial.onBankDealDoneClick();
        } else {
            storeState();
            if (checkForNewMedal()) {
                return;
            }
        }
    }

    public void onCapacityButtonClick(View view) {
        mFrontEnd.showCapacityMessage();
    }

    public void onFixButtonClick(View view) {
        if (mLogic.isShipBroken()) {
            if (!mLogic.isEnoughTimeForFixShip()) {
                mFrontEnd.showErrorMessage(getString(R.string.CANNOT_FIX_AT_NIGHT),
                        getString(R.string.CANNOT_FIX_TITLE));
                return;
            }

            if (mIsStartTutorialActive) {
                mStartTutorial.onFixButtonClick();
                return;
            }

            mLogic.initFixShipDeal();
            mFrontEndFixShip.onFixShipClick();
            mFrontEnd.showWindow(Window.FIX_SHIP_WINDOW);
        } else {
            mFrontEnd.stopBlinks();
            mFrontEnd.showErrorMessage(getString(R.string.CANNOT_FIX_FIXED_SHIP),
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

        if (mIsStartTutorialActive) {
            mStartTutorial.fixDone();
        } else {
            storeState();
        }
    }

    public void onFixFreeClick(View view) {
        mFrontEndFixShip.showFreeFixDialog();
    }

    public void freeFix() {
        playSound(R.raw.fix);
        mLogic.applyFreeShipFixDeal();
        mFrontEnd.showState();
        mFrontEnd.showWindow(Window.MAIN_WINDOW);
        storeState();
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

    public void onGoogleSignInCheckBoxClick(View view) {
        mIsGoogleSignIn = ((CheckBox)view).isChecked();
        if (mIsGoogleSignIn) {
            mFrontEnd.showSignGoogleDialog();
        }
    }

    // Set of all media players which are currently working. Used to prevent garbage collector from
    // clean them and stop the sounds.
    private final Set<MediaPlayer> mMediaPlayers = new HashSet<MediaPlayer>();

    public void onSoundCheckBoxClick(View view) {
        mIsSoundEnable = ((CheckBox)view).isChecked();
    }

    public void onFastAnimationCheckBoxClick(View view) {
        mIsFastAnimation = ((CheckBox)view).isChecked();
    }

    public void onLanguageButtonClick(View view) {
        mFrontEnd.showLanguageDialog();
    }

    public void playSound(int soundId) {
        if (!mIsSoundEnable) {
            return;
        }

        if (soundId == 0) {
            return;
        }

        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        switch (am.getRingerMode()) {
            case AudioManager.RINGER_MODE_SILENT:
            case AudioManager.RINGER_MODE_VIBRATE:
                return;
            case AudioManager.RINGER_MODE_NORMAL:
                break;
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
        if (mIsStartTutorialActive) {
            return;
        }
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
                onExitBadWeatherClick(null);
                break;
            case SINK_WINDOW:
                onSinkProgress(null);
                break;
            case SHOAL_WINDOW:
            case ABANDONED_SHIP_WINDOW:
            case MORE_DAMAGE_WINDOW:
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
            case FORTUNE_TELLER_WINDOW:
                onCancelFortuneTeller(null);
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
        mFrontEndMedal.updateAllMedalImages();
        mFrontEnd.showWindow(Window.MEDAL_WINDOW);
    }

    public void onExitMedalClick(View view) {
        mFrontEnd.showWindow(Window.MAIN_WINDOW);
    }

    public void onExitMenu(View view) {
        mFrontEnd.showWindow(Window.MAIN_WINDOW);
        if (mIsStartTutorialActive) {
            mFrontEnd.showLastDelayedMessage();
        }
    }

    public void onMenuClick(View view) {
        mFrontEnd.showWindow(Window.MENU_WINDOW);
    }

    public void onStartNewGameClick(View view) {
        mFrontEnd.showNewGameDialog();
    }

    public void storeInvalidState() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        mLogic.storeInvalidState(editor);
        editor.apply();
    }

    public void storeState() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        mLogic.storeState(editor);
        editor.apply();
    }

    private void restoreState() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        mLogic.restoreState(sharedPref);

        ((CheckBox)findViewById(R.id.fast_animation_checkbox)).setChecked(mIsFastAnimation);
        ((CheckBox)findViewById(R.id.enable_sound_check_box)).setChecked(mIsSoundEnable);
        ((CheckBox)findViewById(R.id.connect_google_play_games)).setChecked(mIsGoogleSignIn);

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
        if (mIsStartTutorialActive) {
            return false;
        }

        Medal medal = mLogic.acquireNewMedal();
        if (medal != null) {
            playSound(R.raw.agreement);
            mFrontEnd.showWindow(Window.NEW_MEDAL_WINDOW);
            mFrontEndMedal.showNewMedal(medal);

            if (medal == Medal.GREECE_VISITOR || medal == Medal.ISLAMIC_STATE) {
                // These specific medals update the sail duration on the main screen so we need to do
                // it immediately when we get the medal.
                mFrontEnd.showState();
            }

            mBackEndGoogleApi.unlockMedal(medal);

            // it is important to store the medal now so if app is
            // being killed somehow, the medal won't lose.
            storeState();

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

    public void onScorePageClick(View view) {
        if (view == findViewById(R.id.high_capacity_button)) {
            mFrontEndHighScore.showScores(Logic.ScoreType.HIGH_CAPACITY_TABLE_INDEX);
        } else {
            mFrontEndHighScore.showScores(Logic.ScoreType.HIGH_SCORE_TABLE_INDEX);
        }
    }

    public void onStartTutorialButtonClick(View view) {
        mFrontEnd.showWindow(Window.MAIN_WINDOW);
        if (!mIsStartTutorialActive) {
            mFrontEnd.showStartingTutorialDialog();
        } else {
            mFrontEnd.showEndTutorialDialog();
        }
    }

    public void startTutorial() {
        playSound(R.raw.new_day);
        mIsStartTutorialActive = true;
        mStartTutorial.showStage1();
        mFrontEnd.setStartingTutorialButtonText();
    }

    public void endTutorial() {
        mIsStartTutorialActive = false;
        mStartTutorial.endTutorial();
        mFrontEnd.setStartingTutorialButtonText();
    }
}