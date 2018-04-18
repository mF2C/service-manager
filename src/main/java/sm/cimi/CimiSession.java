/**
 * CIMI session class
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package sm.cimi;


public class CimiSession {

    private SessionTemplate sessionTemplate;

    public CimiSession() {
    }

    public CimiSession(SessionTemplate sessionTemplate) {
        this.sessionTemplate = sessionTemplate;
    }

    public SessionTemplate getSessionTemplate() {
        return sessionTemplate;
    }

    public void setSessionTemplate(SessionTemplate sessionTemplate) {
        this.sessionTemplate = sessionTemplate;
    }
}
