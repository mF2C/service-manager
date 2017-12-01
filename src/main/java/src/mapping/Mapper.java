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
import src.Task;
import src.allocation.Allocator;
import src.categorization.Categorizer;
import src.categorization.elements.Category;
import src.qosprovisioning.QosProvider;

import java.util.LinkedHashMap;

public class Mapper {

    private static Logger log = LoggerFactory.getLogger(Mapper.class);

    private LinkedHashMap<String, Task> tasksMap;
    private Categorizer categorizer;
    private Allocator allocator;
    private QosProvider qosProvider;

    public Mapper() {
        tasksMap = new LinkedHashMap<>();
        categorizer = new Categorizer();
        allocator = new Allocator();
        qosProvider = new QosProvider();
    }

    /**
     * Method to compute a task received from the PM
     *
     * @param task
     */
    public boolean submitTask(Task task) {

        log.info("Received task with @id-" + task.getId());

        if (tasksMap.containsKey(task.getId()))
            return true;
        else
            tasksMap.put(task.getId(), task);
        return false;
    }

    public boolean applyOperationToTask(String taskId, String operation) {

        boolean error = false;
        switch (operation) {
            case "START":
                error = mapTask(taskId);
                break;
            case "STOP":
                break;
            case "RESTART":
                break;
            case "DELETE":
                break;
        }
        return error;
    }

    private boolean mapTask(String taskId) {

        log.info("Mapping new task @id-" + taskId);
        Task task = tasksMap.get(taskId);
        boolean error = false;


        // Check if the task already exist in the DB
        if (!checkTaskInDB(task.getId())) {

            // Categorize the task
            Category category = categorizer.categorise(task);

            // Assign category to the task
            task.setCategory(category);

        } else
            task = getTaskFromDB(task.getId());


        // Check the QoS Requirements
        if (qosProvider.checkRequirements(task.getId())) {
            log.info("The QoS requirements are checked for task @id-" + task.getId());

        } else {
            log.info("Something went wrong with the QoS requirements for task @id-" + task.getId());
            error = true;
        }

        // Allocate resources
        if (allocator.reserveResources(task.getId())) {
            log.info("The resources are allocated for task @id-" + task.getId());
        } else {
            log.info("Something went wrong with the allocation of resources for task @id-" + task.getId());
            error = true;
        }

        return error;
    }

    private boolean checkTaskInDB(String taskId) {
        log.info("Checking if task already exist in DB @id-" + taskId);
        //TODO
        return false;
    }

    private Task getTaskFromDB(String taskId) {
        log.info("Getting the task from the DB @id-" + taskId);
        //TODO
        Task task = new Task();
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

    public boolean checkIfTaskExistOnMemory(String taskId) {
        return tasksMap.containsKey(taskId);
    }


}
