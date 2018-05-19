/**
 * Agreement class.
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package sm.elements;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Agreement {

    private String id;
    private Details details;

    public Agreement() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Details getDetails() {
        return details;
    }

    public void setDetails(Details details) {
        this.details = details;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Details{

        private List<Guarantee> guarantees;

        public Details(){
            guarantees = new ArrayList<>();
        }

        public List<Guarantee> getGuarantees() {
            return guarantees;
        }

        public void setGuarantees(List<Guarantee> guarantees) {
            this.guarantees = guarantees;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Guarantee {
            private String name;
            private String constraint;

            public Guarantee() {
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getConstraint() {
                return constraint;
            }

            public void setConstraint(String constraint) {
                this.constraint = constraint;
            }
        }
    }
}
