/**
 * Mapping module inside the Service Manager
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */

package src.mapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import src.allocation.Allocator;
import src.categorization.Categorizer;
import src.elements.Category;
import src.elements.Task;
import src.qosprovisioning.QosProvider;

import java.util.LinkedHashMap;

public class Mapper {

    private static Logger log = LoggerFactory.getLogger(Mapper.class);

    private LinkedHashMap<Integer, Task[]> taskMap;
    private Categorizer categorizer;
    private Allocator allocator;
    private QosProvider qosProvider;

    public Mapper() {
        taskMap = new LinkedHashMap<>();
        categorizer = new Categorizer();
        allocator = new Allocator();
        qosProvider = new QosProvider();
    }

    public boolean mapTask(int taskId) {

        log.info("Mapping new task @id-" + taskId);
        boolean error = false;
        Task newTask;

        // Check if the task already exist in the DB
        if (!checkTaskInDB(taskId)) {

            newTask = new Task(taskId);
            // Categorize the task
            Category category = categorizer.categorise(taskId);

            // Assign category to the task
            newTask.setCategory(category);

        } else
            newTask = getTaskFromDB(taskId);


        // Check the QoS Requirements
        if (qosProvider.checkRequirements(taskId)) {
            log.info("The QoS requirements are checked for task @id-" + taskId);

        } else {
            log.info("Something went wrong with the QoS requirements for task @id-" + taskId);
            error = true;
        }

        // Allocate resources
        if (allocator.reserveResources(taskId)) {
            log.info("The resources are allocated for task @id-" + taskId);
        } else {
            log.info("Something went wrong with the allocation of resources for task @id-" + taskId);
            error = true;
        }

        return error;
    }

    private boolean checkTaskInDB(int taskId) {
        log.info("Checking if task already exist in DB @id-" + taskId);
        //TODO
        return false;
    }

    private Task getTaskFromDB(int taskId) {
        log.info("Getting the task from the DB @id-" + taskId);
        Task task = new Task(taskId);
        //TODO
        return task;
    }

    public boolean applyPolicies(Task task) {

        //TODO
        return true;
    }

    public boolean checkUserConstrains(Task task) {

        //TODO
        return true;
    }


}
