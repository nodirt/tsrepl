package tsrepl;

import org.joda.time.DateTime;

import twitter4j.*;

public class ReplUserStreamListener implements UserStreamListener {
	static final int statusUpdateLimit = 140;
	
	Twitter mTwitter;
	String mUserName;
	String mMentionPrefix;
	Repl mRepl;
	
	ReplConfiguration mConfig;
	DateTime mNextCacheCleanup;
	
	public ReplUserStreamListener(Twitter twitter, ReplConfiguration config) throws TwitterException {
		if (twitter == null || config == null) {
			throw new IllegalArgumentException();
		}
		mTwitter = twitter;
		mUserName = twitter.getScreenName();
		mMentionPrefix = "@" + mUserName + " ";
		mRepl = new Repl();
		mConfig = config;
		mNextCacheCleanup = DateTime.now().plusHours(config.getCacheLifetime());
	}
	
    @Override
    public void onStatus(Status status) {
    	cleaupCacheIfNeeded();
    	String text = status.getText();
    	if (!text.startsWith(mMentionPrefix)) {
    		return;
    	}
    	
    	String code = text.substring(mMentionPrefix.length());
    	String userName = status.getUser().getScreenName();
    	
    	String result = mRepl.eval(userName, code);
    	if (result == null) {
    		return;
    	}
    	
    	String newStatus = "@" + userName + " " + result;
    	if (newStatus.length() > statusUpdateLimit) {
    		newStatus = newStatus.substring(0, statusUpdateLimit);
    	}
    	
    	StatusUpdate update = new StatusUpdate(newStatus);
    	update.inReplyToStatusId(status.getId());
    	
    	try {
    		mTwitter.updateStatus(update);
    	} catch (TwitterException ex) {
            ex.printStackTrace();
            System.out.println("Failed to update status: " + ex.getMessage());
    	}
    }
    
    void cleaupCacheIfNeeded() {
    	if (DateTime.now().compareTo(mNextCacheCleanup) < 0) {
    		return;
    	}
    	
    	mRepl.cleanup(mConfig.getCacheLifetime());
    	mNextCacheCleanup = DateTime.now().plusHours(mConfig.getCacheLifetime());
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
