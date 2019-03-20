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

   @JsonProperty("agent")
   private Href id;
   private boolean allow;

   public Agent() {
   }

   public Agent(String id) {
      this.id = new Href(id);
      this.allow = true;
   }

   public Href getId() {
      return id;
   }

   public boolean isAllow() {
      return allow;
   }

   public void setId(Href id) {
      this.id = id;
   }

   public void setAllow(boolean allow) {
      this.allow = allow;
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
