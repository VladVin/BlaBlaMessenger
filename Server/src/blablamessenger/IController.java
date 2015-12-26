package blablamessenger;

import coreutilities.ResultData;

import java.util.concurrent.ConcurrentLinkedQueue;

public interface IController
{
    void
    start(
        ICommunicable                       listener,
        ConcurrentLinkedQueue< Task       > tasks,
        ConcurrentLinkedQueue< ResultData > results
    );

    void
    stop();

    boolean
    addTask(
        Task task
    );
}
