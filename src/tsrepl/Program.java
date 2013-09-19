package tsrepl;

import twitter4j.*;

public class Program {

    public static void main(String[] args) throws Exception {
        ReplConfiguration cfg = new ReplConfiguration();
        cfg.load();
        
        Twitter twitter = new TwitterFactory().getInstance();
        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
        twitterStream.addListener(new ReplUserStreamListener(twitter, cfg));
        twitterStream.user();
    }
}
