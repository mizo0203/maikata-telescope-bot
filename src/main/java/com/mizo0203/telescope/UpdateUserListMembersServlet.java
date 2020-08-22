package com.mizo0203.telescope;

import twitter4j.Twitter;
import twitter4j.TwitterException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

@SuppressWarnings("serial")
public class UpdateUserListMembersServlet extends HttpServlet {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(UpdateUserListMembersServlet.class.getName());

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try (Repository repository = new Repository()) {
            Twitter twitter = repository.getTwitter();
            repository.updateUserListMembers(twitter);
        } catch (TwitterException e) {
            throw new RuntimeException("fail", e);
        }
    }
}
