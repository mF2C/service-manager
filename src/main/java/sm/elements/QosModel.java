/**
 * QoS model class
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package sm.elements;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QosModel {

   private String id;
   @JsonProperty("service")
   private Href serviceId;
   @JsonProperty("agreement")
   private Href agreementId;
   @JsonProperty("agents")
   private List<Href> agentsIds;
   private String config;
   @JsonProperty("num_service_instances")
   private Integer numServiceInstances;
   @JsonProperty("num_service_failures")
   private Integer numServiceFailures;
   @JsonProperty("current_state")
   private float[] state;
   @JsonProperty("next_state")
   private float[] nextState;

   public QosModel() {
   }

   public QosModel(String serviceId, String agreementId, List<String> agentsIds, int environmentSize) {
      this.serviceId = new Href(serviceId);
      this.agreementId = new Href(agreementId);
      List<Href> agents = new ArrayList<>();
      for (String s : agentsIds)
         agents.add(new Href(s));
      this.agentsIds = agents;
      this.config = "config";
      this.numServiceInstances = 0;
      this.numServiceFailures = 0;
      this.state = new float[environmentSize];
      this.nextState = new float[environmentSize];
   }

   public void increaseNumServiceInstanceValue() {
      numServiceInstances++;
   }

   public void increaseNumServiceFailuresValue() {
      numServiceFailures++;
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public Href getServiceId() {
      return serviceId;
   }

   public void setServiceId(Href serviceId) {
      this.serviceId = serviceId;
   }

   public Href getAgreementId() {
      return agreementId;
   }

   public void setAgreementId(Href agreementId) {
      this.agreementId = agreementId;
   }

   public List<Href> getAgentsIds() {
      return agentsIds;
   }

   public void setAgentsIds(List<Href> agentsIds) {
      this.agentsIds = agentsIds;
   }

   public String getConfig() {
      return config;
   }

   public void setConfig(String config) {
      this.config = config;
   }

   public Integer getNumServiceInstances() {
      return numServiceInstances;
   }

   public void setNumServiceInstances(Integer numServiceInstances) {
      this.numServiceInstances = numServiceInstances;
   }

   public Integer getNumServiceFailures() {
      return numServiceFailures;
   }

   public void setNumServiceFailures(Integer numServiceFailures) {
      this.numServiceFailures = numServiceFailures;
   }

   public float[] getState() {
      return state;
   }

   public void setState(float[] state) {
      this.state = state;
   }

   public float[] getNextState() {
      return nextState;
   }

   public void setNextState(float[] nextState) {
      this.nextState = nextState;
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
