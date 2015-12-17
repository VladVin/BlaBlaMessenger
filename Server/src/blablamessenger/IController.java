package blablamessenger;

import coreutilities.ResultData;

import java.util.concurrent.ConcurrentLinkedQueue;

public interface IController
{
    void
    createController(
        IConnectible                        listener,
        ConcurrentLinkedQueue< Task       > tasks,
        ConcurrentLinkedQueue< ResultData > results
    );
    void
    destroyController();
}
