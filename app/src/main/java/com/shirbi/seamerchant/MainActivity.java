package com.shirbi.seamerchant;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

import java.util.HashSet;
import java.util.Set;

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

    boolean mIsSoundEnable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLogic = new Logic();
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

        mLogic.startNewGame();
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
        mFrontEnd.showWindow(Window.MAIN_WINDOW);
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
            mFrontEnd.showAlertDialogMessage(getString(R.string.GREECE_IS_TOO_FAR),
                    getString(R.string.CANNOT_SAIL_TITLE));
            return;
        }

        if (mLogic.getDayPart() == DayPart.NIGHT) {
            mFrontEnd.showAlertDialogMessage(getString(R.string.CANNOT_SAIL_AT_NIGHT),
                    getString(R.string.CANNOT_SAIL_TITLE));
            return;
        }

        if (mLogic.isDamagePreventSail()) {
            mFrontEnd.showAlertDialogMessage(getString(R.string.CANNOT_SAIL_WITH_HEAVY_DAMAGE),
                    getString(R.string.CANNOT_SAIL_TITLE));
            return;
        }

        if (!mLogic.canReachToDestinationBeforeSleepTime(destination)) {
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
        mFrontEndSail.startSail();
    }

    public void onSailEndClick(View view) {
        mFrontEnd.showWindow(Window.MAIN_WINDOW);
        mFrontEnd.showState();
    }

    public void onSleepClick(View view) {
        mFrontEnd.showWindow(Window.SLEEP_WINDOW);
    }

    public void onCancelSleep(View view) {
        mFrontEnd.showWindow(Window.MAIN_WINDOW);
    }

    public void onApproveSleep(View view) {
        playSound(R.raw.new_day);
        mLogic.startNewDay();
        mFrontEnd.showWindow(Window.WEATHER_WINDOW);
        mFrontEnd.showNewWeather();
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
    }

    public void onAcceptOffer(View view) {
        mLogic.acceptOffer();
        mFrontEnd.showWindow(Window.MAIN_WINDOW);
        mFrontEnd.showState();
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
    }

    public void onFixButtonClick(View view) {
        mLogic.initFixShipDeal();
        mFrontEndFixShip.onFixShipClick();
        mFrontEnd.showWindow(Window.FIX_SHIP_WINDOW);
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
}