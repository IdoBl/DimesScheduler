package dimes.scheduler;

import dimes.scheduler.services.EntityCreatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * <p>
 * The SchedulerStarter starts the Spring framework which reads the configuration files
 * from both this application and DimesDAL and configures the relevant beans / components.
 * </p>
 *
 * @author idob
 */

public class SchedulerStarter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerStarter.class);

    /**
     * The main method in this class is for starting the scheduler - i.e. loading the application context in order to schedule the tasks which defined in the components.
     * @param args
     */
    public static void main(String[] args) {

        LOGGER.info("Starting Dimes Scheduler");
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:scheduler-config.xml");
        EntityCreatorService asEdgesCreatorService = (EntityCreatorService) context.getBean("asEdgesCreatorService");
        asEdgesCreatorService.createWeeklyEntity(2015, 3);
        LOGGER.info("Finishing Dimes Scheduler");
        // Closing the context and releasing sources gracefully on JVM shutdown
        context.registerShutdownHook();

    }

}
