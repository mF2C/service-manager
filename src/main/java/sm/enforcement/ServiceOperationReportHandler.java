package sm.enforcement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.MessageEvent;
import sm.elements.ServiceOperationReport;

import java.io.IOException;

public class ServiceOperationReportHandler implements EventHandler {
   @Override
   public void onOpen() {
   }

   @Override
   public void onClosed() {
   }

   @Override
   public void onMessage(String event, MessageEvent messageEvent) {
      String rawData = messageEvent.getData();
      ServiceOperationReport serviceOperationReport = null;
      try {
         serviceOperationReport = new ObjectMapper().readValue(rawData, ServiceOperationReport.class);
      } catch (IOException e) {
         e.printStackTrace();
      }
      QosEnforcer.checkServiceOperationReport(serviceOperationReport);
   }

   @Override
   public void onComment(String s) {
   }

   @Override
   public void onError(Throwable throwable) {
   }
}
