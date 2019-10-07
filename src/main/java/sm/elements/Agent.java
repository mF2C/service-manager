/**
 * Agent class.
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

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Agent {

   @JsonProperty("device_id")
   private String deviceId;
   private boolean allow;

   public Agent() {
   }

   public Agent(String deviceId) {
      this.deviceId = deviceId;
      this.allow = true;
   }

   public boolean isAllow() {
      return allow;
   }

   public void setAllow(boolean allow) {
      this.allow = allow;
   }

   public String getDeviceId() {
      return deviceId;
   }

   public void setDeviceId(String deviceId) {
      this.deviceId = deviceId;
   }
}
