/**
 * QoS Enforcement module inside the Service Manager
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package sm.enforcement;

import sm.cimi.CimiInterface;
import sm.elements.Agreement;
import sm.elements.OperationReport;
import sm.elements.ServiceInstance;

import java.time.Instant;

public class QosEnforcer {

   public void checkServiceOperationReport(OperationReport operationReport) {
      ServiceInstance serviceInstance = CimiInterface.getServiceInstance(operationReport.getServiceInstance());
      Agreement agreement = CimiInterface.getAgreement(serviceInstance.getAgreement());
      Instant computedAtInstant = Instant.parse(operationReport.getComputedAt());
      Instant expectedComputationEnd = Instant.parse(operationReport.getExpectedComputationEnd());
      int agreementValue = Integer.valueOf(agreement.getDetails().getGuarantees().get(0).getConstraint());
      if (expectedComputationEnd.getNano() - computedAtInstant.getNano() > agreementValue) {
         // TODO: Notify the LM
      }
   }
}
