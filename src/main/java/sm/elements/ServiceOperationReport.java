package sm.elements;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceOperationReport {

   private String id;
   @JsonProperty("requesting_application_id")
   private Href requestingApplicationId;
   @JsonProperty("operation_name")
   private String operationName;
   @JsonProperty("operation_id")
   private String operationId;
   @JsonProperty("start_time")
   private String startTime;
   @JsonProperty("expected_end_time")
   private String expectedEndTime;

   public ServiceOperationReport(){
   }

   public String getId() {
      return id;
   }

   public Href getRequestingApplicationId() {
      return requestingApplicationId;
   }

   public String getStartTime() {
      return startTime;
   }

   public String getExpectedEndTime() {
      return expectedEndTime;
   }

   public String getOperationName() {
      return operationName;
   }

   public String getOperationId() {
      return operationId;
   }

   @JsonIgnoreProperties(ignoreUnknown = true)
   public static class Href {

      private String href;

      public Href() {
      }

      public Href(String href) {
         this.href = href;
      }

      public String getHref() {
         return href;
      }

      public void setHref(String href) {
         this.href = href;
      }
   }
}


