package sm.enforcement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgentRequest {
   private String type;
   private Data data;

   public AgentRequest(int numAgents, String serviceInstanceId) {
      this.type = "qos_enforcement";
      this.data = new Data(numAgents, serviceInstanceId);
   }

   public String getType() {
      return type;
   }

   public Data getData() {
      return data;
   }

   @JsonInclude(JsonInclude.Include.NON_NULL)
   public static class Data {
      @JsonProperty("service_instance_id")
      private String serviceInstanceId;
      @JsonProperty("num_agents")
      private Integer numAgents;

      public Data(int numAgents, String serviceInstanceId) {
         this.numAgents = numAgents;
         this.serviceInstanceId = serviceInstanceId;
      }

      public String getServiceInstanceId() {
         return serviceInstanceId;
      }

      public Integer getNumAgents() {
         return numAgents;
      }
   }
}
