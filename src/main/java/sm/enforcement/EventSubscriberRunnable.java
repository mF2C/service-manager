package sm.enforcement;

import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.EventSource;

import java.net.URI;

public class EventSubscriberRunnable implements Runnable {
   private String url;

   EventSubscriberRunnable(String url) {
      this.url = url;
   }

   public void run() {
      subscribeToEvents(url);
   }

   private void subscribeToEvents(String url) {
      EventHandler eventHandler = new ServiceOperationReportHandler();
      EventSource.Builder builder = new EventSource.Builder(eventHandler, URI.create(url));
      EventSource eventSource = builder.build();
      eventSource.start();
   }
}
