package sm.elements;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OperationReport {

   @JsonProperty("service_instance")
   private String serviceInstance;
   @JsonProperty("expected_computation_end")
   private String expectedComputationEnd;
   @JsonProperty("computed_at")
   private String computedAt;
   private String data;

   public OperationReport(){
   }

   public String getServiceInstance() {
      return serviceInstance;
   }

   public void setServiceInstance(String serviceInstance) {
      this.serviceInstance = serviceInstance;
   }

   public String getExpectedComputationEnd() {
      return expectedComputationEnd;
   }

   public void setExpectedComputationEnd(String expectedComputationEnd) {
      this.expectedComputationEnd = expectedComputationEnd;
   }

   public String getComputedAt() {
      return computedAt;
   }

   public void setComputedAt(String computedAt) {
      this.computedAt = computedAt;
   }

   public String getData() {
      return data;
   }

   public void setData(String data) {
      this.data = data;
   }
}


