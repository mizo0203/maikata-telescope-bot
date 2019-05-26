package com.mizo0203.telescope;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import java.io.Serializable;

/**
 * The @Entity tells Objectify about our entity. We also register it in {@link OfyHelper} Our
 * primary key @Id is set automatically by the Google Datastore for us.
 *
 * <p>We add a @Parent to tell the object about its ancestor. We are doing this to support many
 * guestbooks. Objectify, unlike the AppEngine library requires that you specify the fields you want
 * to index using @Index. Only indexing the fields you need can lead to substantial gains in
 * performance -- though if not indexing your data from the start will require indexing it later.
 *
 * <p>NOTE - all the properties are PUBLIC so that can keep the code simple.
 */
@Entity
public class TwitterAccessToken implements Serializable {

    /**
     * Access Token
     */
    @Id
    private final String accessToken;

    /**
     * Access Token Secret
     */
    private String accessTokenSecret;

    /**
     * Consumer Key (API Key)
     */
    private String consumerKey;

    /**
     * Consumer Secret (API Secret)
     */
    private String consumerSecret;

    @SuppressWarnings("unused")
    public TwitterAccessToken() {
        // CommitCommentEventEntity must have a no-arg constructor
        this.accessToken = "";
        this.accessTokenSecret = "";
        this.consumerKey = "";
        this.consumerSecret = "";
    }

    /* package */ TwitterAccessToken(
            String accessToken) {
        this.accessToken = accessToken;
        this.accessTokenSecret = "";
        this.consumerKey = "";
        this.consumerSecret = "";
    }

    /* package */ Twitter getTwitter() {
        Twitter twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(consumerKey, consumerSecret);
        twitter.setOAuthAccessToken(new AccessToken(accessToken, accessTokenSecret));
        return twitter;
    }
}
