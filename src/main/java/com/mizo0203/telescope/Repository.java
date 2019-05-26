package com.mizo0203.telescope;

import org.apache.commons.lang3.ArrayUtils;
import twitter4j.*;

import java.util.*;
import java.util.logging.Logger;

public class Repository implements AutoCloseable {
    private static final Logger LOG = Logger.getLogger(Repository.class.getName());

    /**
     * Twitter API - Access Token
     */
    private static final String mizo0203 = "212164063-ZI9hEMxxvWpOmLw0FuQKIevMUIGVdR4OhYDTlNWO";

    private final OfyManager mOfy = new OfyManager();

    /* package */ Twitter getVerifiedTwitter() throws TwitterException {
        TwitterAccessToken twitterAccessToken = mOfy.load(TwitterAccessToken.class, mizo0203);
        if (twitterAccessToken == null) {
            twitterAccessToken = new TwitterAccessToken(mizo0203);
            mOfy.save(twitterAccessToken);
        }
        Twitter twitter = twitterAccessToken.getTwitter();
        twitter.verifyCredentials();
        return twitter;
    }

    @SuppressWarnings("unused")
        /* package */ void getUserListsOwnerships(Twitter twitter) throws TwitterException {
        PagableResponseList<UserList> userLists = twitter.getUserListsOwnerships(twitter.getId(), -1L);
        for (UserList userList : userLists) {
            LOG.info("userList.getId(): " + userList.getId() + "\n" + "userList.getFullName(): " + userList.getFullName());
        }
    }

    /* package */ void updateUserListMembers(Twitter twitter) throws TwitterException {
        Set<Long> set = userIds(twitter);
        if (!set.isEmpty()) {
            twitter.list().createUserListMembers(1132086183348002816L, ArrayUtils.toPrimitive(set.toArray(new Long[0])));
        }
    }

    private Set<Long> userIds(Twitter twitter) {
        Set<Long> set = new HashSet<>();

        List<Long> userList = new ArrayList<>();
        try {
            for (User user : getUserListMembers(twitter)) {
                userList.add(user.getId());
            }
        } catch (TwitterException e) {
            LOG.warning(e.getMessage());
            return set;
        }
        Collections.shuffle(userList);

        Set<Long> candidateIdSet = new HashSet<>();
        for (int i = 0; i < 15 && i < userList.size(); i++) {
            try {
                IDs followersIDs = twitter.getFollowersIDs(userList.get(i), -1L, 5000);
                candidateIdSet.addAll(Arrays.asList(ArrayUtils.toObject(followersIDs.getIDs())));
                IDs friendsIDs = twitter.getFriendsIDs(userList.get(i), -1L, 5000);
                candidateIdSet.addAll(Arrays.asList(ArrayUtils.toObject(friendsIDs.getIDs())));
            } catch (TwitterException e) {
                // 非公開ツイートやブロック設定によって UNAUTHORIZED (= 401) が発生しうる
                if (e.getStatusCode() != HttpResponseCode.UNAUTHORIZED) {
                    LOG.warning(e.getMessage());
                }
            }
        }
        candidateIdSet.removeAll(userList);

        List<Long> candidateIdList = new ArrayList<>(candidateIdSet);
        Collections.shuffle(candidateIdList);

        for (int j = 0; j < 900 && j < candidateIdList.size(); j++) {
            try {
                ResponseList<Status> statusResponseList = twitter.getUserTimeline(candidateIdList.get(j), new Paging(1, 200));
                for (Status status : statusResponseList) {
                    int score = calc(status.getUser().getDescription(), status.getUser().getLocation(), status.getText());
                    if (score >= 5) {
                        LOG.info("score: " + score + " " + "user: " + status.getUser().getName() + " @" + status.getUser().getScreenName() + " (" + status.getUser().getId() + ")\n"
                                + "getDescription(): " + status.getUser().getDescription() + "\n"
                                + "getLocation(): " + status.getUser().getLocation() + "\n"
                                + "getText(): " + status.getText());
                        set.add(status.getUser().getId());
                        if (set.size() == 100) {
                            return set;
                        }
                        break;
                    }
                }
            } catch (TwitterException e) {
                // 非公開ツイートやブロック設定によって UNAUTHORIZED (= 401) が発生しうる
                if (e.getStatusCode() != HttpResponseCode.UNAUTHORIZED) {
                    LOG.warning(e.getMessage());
                }
            }
        }

        return set;
    }

    private int calc(String description, String location, String text) {
        int ret = 0;
        String str = description + "\n" + location + "\n" + text;

        if (str.contains("大阪工業大学")) {
            ret += 3;
        } else if (str.contains("大工大")) {
            ret += 3;
        } else if (str.contains("OIT")) {
            ret += 2;
        }

        if (str.contains("情報科学部")) {
            ret += 2;
        } else if (str.contains("枚方キャンパス")) {
            ret += 2;
        } else if (str.contains("枚方")) {
            ret += 1;
        } else if (str.contains("北山中央")) {
            ret += 1;
        } else if (str.contains("北山祭")) {
            ret += 1;
        } else if (str.contains("樟葉")) {
            ret += 1;
        } else if (str.contains("長尾")) {
            ret += 1;
        }

        if (str.contains("情報知能学科")) {
            ret += 3;
        } else if (str.contains("情報システム学科")) {
            ret += 3;
        } else if (str.contains("情報メディア学科")) {
            ret += 3;
        } else if (str.contains("ネットワークデザイン学科")) {
            ret += 3;
        } else if (str.contains("IC科")) {
            ret += 3;
        } else if (str.contains("IS科")) {
            ret += 3;
        } else if (str.contains("IM科")) {
            ret += 3;
        } else if (str.contains("IN科")) {
            ret += 3;
        } else if (str.contains("情報知能")) {
            ret += 2;
        } else if (str.contains("情報システム")) {
            if (!str.contains("電子情報システム工学")) {
                ret += 2;
            }
        } else if (str.contains("情報メディア")) {
            ret += 2;
        } else if (str.contains("ネットワークデザイン")) {
            ret += 2;
        } else if (str.contains("IC")) {
            ret += 1;
        } else if (str.contains("IS")) {
            ret += 1;
        } else if (str.contains("IM")) {
            ret += 1;
        } else if (str.contains("IN")) {
            ret += 1;
        }

        return ret;
    }

    private List<User> getUserListMembers(Twitter twitter) throws TwitterException {
        List<User> userList = new ArrayList<>();
        PagableResponseList<User> userResponseList = twitter.getUserListMembers(1132086183348002816L, 5000, -1L);
        while (true) {
            userList.addAll(userResponseList);
            if (userResponseList.hasNext()) {
                userList = twitter.getUserListMembers(1132086183348002816L, 5000, userResponseList.getNextCursor());
            } else {
                break;
            }
        }
        return Collections.unmodifiableList(userList);
    }

    @Override
    public void close() {
    }
}
