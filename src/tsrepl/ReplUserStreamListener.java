package tsrepl;

import twitter4j.*;

public class ReplUserStreamListener implements UserStreamListener {
	Twitter mTwitter;
	String mUserName;
	String mMentionPrefix;
	Repl mRepl;
	
	public ReplUserStreamListener(Twitter twitter) throws TwitterException {
		if (twitter == null) {
			throw new IllegalArgumentException();
		}
		mTwitter = twitter;
		mUserName = twitter.getScreenName();
		mMentionPrefix = "@" + mUserName + " ";
		mRepl = new Repl();
	}
	
    @Override
    public void onStatus(Status status) {
    	String text = status.getText();
    	if (!text.startsWith(mMentionPrefix)) {
    		return;
    	}
    	
    	String code = text.substring(mMentionPrefix.length());
    	String userName = status.getUser().getScreenName();
    	
    	try {
    		mTwitter.updateStatus("@" + userName + " " + mRepl.eval(userName, code));
    	} catch (TwitterException ex) {
            ex.printStackTrace();
            System.out.println("Failed to update status: " + ex.getMessage());
    	}
    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
    }

    @Override
    public void onDeletionNotice(long directMessageId, long userId) {
    }

    @Override
    public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
        System.out.println("Got a track limitation notice:" + numberOfLimitedStatuses);
    }

    @Override
    public void onScrubGeo(long userId, long upToStatusId) {
        System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
    }

    @Override
    public void onStallWarning(StallWarning warning) {
        System.out.println("Got stall warning:" + warning);
    }

    @Override
    public void onFriendList(long[] friendIds) {
    }

    @Override
    public void onFavorite(User source, User target, Status favoritedStatus) {
    }

    @Override
    public void onUnfavorite(User source, User target, Status unfavoritedStatus) {
    }

    @Override
    public void onFollow(User source, User followedUser) {
    }

    @Override
    public void onDirectMessage(DirectMessage directMessage) {
    }

    @Override
    public void onUserListMemberAddition(User addedMember, User listOwner, UserList list) {
    }

    @Override
    public void onUserListMemberDeletion(User deletedMember, User listOwner, UserList list) {
    }

    @Override
    public void onUserListSubscription(User subscriber, User listOwner, UserList list) {
    }

    @Override
    public void onUserListUnsubscription(User subscriber, User listOwner, UserList list) {
    }

    @Override
    public void onUserListCreation(User listOwner, UserList list) {
    }

    @Override
    public void onUserListUpdate(User listOwner, UserList list) {
    }

    @Override
    public void onUserListDeletion(User listOwner, UserList list) {
    }
    @Override
    public void onUserProfileUpdate(User updatedUser) {
    }

    @Override
    public void onBlock(User source, User blockedUser) {
    }

    @Override
    public void onUnblock(User source, User unblockedUser) {
    }

    @Override
    public void onException(Exception ex) {
        ex.printStackTrace();
        System.out.println("onException:" + ex.getMessage());
    }
}
