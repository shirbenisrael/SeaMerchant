package com.shirbi.seamerchant;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.games.AchievementsClient;
import com.google.android.gms.games.AnnotatedData;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.LeaderboardsClient;
import com.google.android.gms.games.achievement.Achievement;
import com.google.android.gms.games.achievement.AchievementBuffer;
import com.google.android.gms.games.leaderboard.LeaderboardScore;
import com.google.android.gms.games.leaderboard.LeaderboardScoreBuffer;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Iterator;
import java.util.StringTokenizer;

import static android.content.ContentValues.TAG;

public class BackEndGoogleApi {
    MainActivity mActivity;
    Logic mLogic;
    GoogleSignInClient mGoogleSignInClient;

    protected final String getString(@StringRes int resId) {
        return mActivity.getString(resId);
    }

    public BackEndGoogleApi(MainActivity activity) {
        mActivity = activity;
        mLogic = activity.mLogic;

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN).
                requestScopes(Games.SCOPE_GAMES).
                requestScopes(Games.SCOPE_GAMES_LITE).
                build();

        mGoogleSignInClient = GoogleSignIn.getClient(mActivity, gso);

        if (mActivity.mIsGoogleSignIn) {
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(mActivity);
            if (account == null) {
                signIn();
            } else {
                getAllDataFromGoogle();
            }
        }
    }

    private void getAllDataFromGoogle() {
        getLeaderBoard();
        getTopScore();
        getCenteredScore();
        loadAchievements();
    }

    public void submitScore() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(mActivity);
        if (account == null) {
            signIn();
        } else {
            LeaderboardsClient client = Games.getLeaderboardsClient(mActivity, account);
            client.submitScore(getString(R.string.leaderboard_highscore), mLogic.mHighScore);
            client.submitScore(getString(R.string.leaderboard_capacity), mLogic.mHighCapacity);
        }
    }

    public void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        mActivity.startActivityForResult(signInIntent, mActivity.RC_SIGN_IN);
    }

    public void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            getAllDataFromGoogle();
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            mActivity.mFrontEnd.showSignGoogleDialog();
        }
    }

    private void getCenteredScore() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(mActivity);
        LeaderboardsClient client = Games.getLeaderboardsClient(mActivity, account);

        for (final Logic.ScoreType scoreType : Logic.ScoreType.values()) {
            client.loadPlayerCenteredScores(getString(scoreType.getGoogleId()), LeaderboardVariant.TIME_SPAN_ALL_TIME,
                    LeaderboardVariant.COLLECTION_PUBLIC, 5, true).
                    addOnSuccessListener(new OnSuccessListener<AnnotatedData<LeaderboardsClient.LeaderboardScores>>() {
                        @Override
                        public void onSuccess(AnnotatedData<LeaderboardsClient.LeaderboardScores>
                                                      leaderboardScoresAnnotatedData) {
                            LeaderboardScoreBuffer scoreBuffer = leaderboardScoresAnnotatedData.get().getScores();
                            Iterator<LeaderboardScore> it = scoreBuffer.iterator();
                            int i = 0;
                            while (((Iterator) it).hasNext()) {
                                LeaderboardScore temp = it.next();
                                long score = temp.getRawScore();
                                int rank = (int) temp.getRank();
                                if (rank != LeaderboardScore.LEADERBOARD_RANK_UNKNOWN) {
                                    String name = temp.getScoreHolderDisplayName();
                                    mLogic.setCenterScore(rank, name, score, i, scoreType);
                                    i++;
                                }
                            }

                            scoreBuffer.release();
                            mActivity.mFrontEndHighScore.fillScores();
                        }
                    });
        }
    }

    private void getTopScore() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(mActivity);
        LeaderboardsClient client = Games.getLeaderboardsClient(mActivity, account);

        for (final Logic.ScoreType scoreType : Logic.ScoreType.values()) {
            client.loadTopScores(getString(scoreType.getGoogleId()), LeaderboardVariant.TIME_SPAN_ALL_TIME,
                    LeaderboardVariant.COLLECTION_PUBLIC, 10, true).
                    addOnSuccessListener(new OnSuccessListener<AnnotatedData<LeaderboardsClient.LeaderboardScores>>() {
                        @Override
                        public void onSuccess(AnnotatedData<LeaderboardsClient.LeaderboardScores>
                                                      leaderboardScoresAnnotatedData) {

                            LeaderboardScoreBuffer scoreBuffer = leaderboardScoresAnnotatedData.get().getScores();
                            Iterator<LeaderboardScore> it = scoreBuffer.iterator();
                            while (((Iterator) it).hasNext()) {
                                LeaderboardScore temp = it.next();
                                long score = temp.getRawScore();
                                int rank = (int) temp.getRank();
                                if (rank != LeaderboardScore.LEADERBOARD_RANK_UNKNOWN) {
                                    String name = temp.getScoreHolderDisplayName();
                                    mLogic.setTopScore(rank, name, score, scoreType);
                                    mActivity.mFrontEndHighScore.fillScores();
                                }
                            }

                            scoreBuffer.release();
                        }
                    });
        }
    }

    private void getLeaderBoard() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(mActivity);
        LeaderboardsClient client = Games.getLeaderboardsClient(mActivity, account);

        for (final Logic.ScoreType scoreType : Logic.ScoreType.values()) {
            client.loadCurrentPlayerLeaderboardScore(getString(scoreType.getGoogleId()),
                    LeaderboardVariant.TIME_SPAN_ALL_TIME, LeaderboardVariant.COLLECTION_PUBLIC)
                    .addOnSuccessListener(mActivity, new OnSuccessListener<AnnotatedData<LeaderboardScore>>() {
                        @Override
                        public void onSuccess(AnnotatedData<LeaderboardScore> leaderboardScoreAnnotatedData) {
                            long score = 0;
                            if (leaderboardScoreAnnotatedData != null) {
                                LeaderboardScore leaderBoardscore = leaderboardScoreAnnotatedData.get();
                                if (leaderBoardscore != null) {
                                    score = leaderBoardscore.getRawScore();
                                    int rank = (int) leaderBoardscore.getRank();
                                    if (rank != LeaderboardScore.LEADERBOARD_RANK_UNKNOWN) {
                                        String name = leaderBoardscore.getScoreHolderDisplayName();
                                        mLogic.setUserScore(rank, name, score, scoreType);
                                        mActivity.mFrontEndHighScore.fillScores();
                                    }
                                } else {
                                    submitScore();
                                }
                            } else {
                                submitScore();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mActivity.mFrontEnd.showAlertDialogMessage("FAILURE " + e, "");
                            mActivity.mFrontEnd.showSignGoogleDialog();
                        }
                    });
        }
    }

    public void unlockMedal(Medal medal) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(mActivity);
        if (account != null) {
            Games.getAchievementsClient(mActivity, account).unlock(getString(medal.getGoogleId()));
        }
    }

    public void loadAchievements() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(mActivity);
        AchievementsClient client = Games.getAchievementsClient(mActivity, account);
        Task<AnnotatedData<AchievementBuffer>> task = client.load(true);
        task.addOnCompleteListener(new OnCompleteListener<AnnotatedData<AchievementBuffer>>() {
            @Override
            public void onComplete(@NonNull Task<AnnotatedData<AchievementBuffer>> task) {
                try {
                    AchievementBuffer buff = task.getResult(ApiException.class).get();
                    Log.d("BUFF", "onComplete: ");
                    int bufSize = buff.getCount();
                    for (int i = 0; i < bufSize; i++) {
                        Achievement ach = buff.get(i);
                        String id = ach.getAchievementId();
                        String description = ach.getDescription();
                        boolean unlocked = ach.getState() == Achievement.STATE_UNLOCKED;

                        StringTokenizer st = new StringTokenizer(description, " ");
                        st.nextToken(); // skip the string Medal.
                        String medalNumber = st.nextToken();

                        int medal_number;
                        try {
                            medal_number = Integer.parseInt(medalNumber);
                            if (medal_number >= Medal.values().length) {
                                continue;
                            }
                        } catch (Exception e) {
                            continue;
                        }
                        Medal medal = Medal.values()[medal_number];

                        if (unlocked) {
                            // This can happen when reinstall the app - restore the medal from google games.
                            if (!mLogic.hasMedal(medal)) {
                                mLogic.restoreMedal(medal, ach.getLastUpdatedTimestamp());
                                mActivity.mFrontEndMedal.setMedalIcon(medal);
                            } else {
                                mLogic.restoreMedal(medal, ach.getLastUpdatedTimestamp());
                            }
                        } else {
                            // This can happen if didn't have internet connection when achieved the medal.
                            // send it now again to google.
                            if (mLogic.hasMedal(medal)) {
                                unlockMedal(medal);
                            }
                        }
                    }
                    buff.release();
                } catch (ApiException e) {
                    Log.w(TAG, "Failed to get medal :failed code=" + e.getStatusCode());
                }
            }
        });
    }
}
