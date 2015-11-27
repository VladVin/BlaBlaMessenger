package blablamessenger;

import coreutilities.ResultData;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ServerController
{
    public
    ServerController(
        Model model
    )
    {
        this.model = model;
    }

    public class Controller extends Thread
    {
        public
        Controller(
            Listener                            myListener,
            ConcurrentLinkedQueue< Task >       tasks,
            ConcurrentLinkedQueue< ResultData > results
        )
        {
            this.myListener = myListener;
            this.tasks      = tasks;
            this.results    = results;

            running = true;
        }

        public boolean
        addTask(
            Task task
        )
        {
            return tasks.add( task );
        }

        public boolean
        addResult(
            ResultData result
        )
        {
            return results.add( result );
        }

        @Override
        public void
        run()
        {
            while ( running ) {
                execute( getTask() );
            }
        }

        private Task
        getTask()
        {
            Task task;

            do {
                task = tasks.poll();
            } while( task == null );

            return task;
        }

        private void
        execute(
            Task task
        )
        {
            ResultData result = null;

            switch ( task.getCommand() ) {
                case RegisterContact:
                    if ( (result = model.registerContact( task, this )) == null ) return;
                    myID = (UUID) result.data;
                break;
                case Disconnect:
                    if ( !model.disconnect(myID, myConferences) ) errorLog( "error on disconnecting" );
                    myListener.close();
                    running = false;
                break;
                case RefreshContacts:
                    if ( (result = model.refreshContacts()) == null ) return;
                break;
                case CreateConference:
                    if ( (result = model.createConference       ( task, myID, myConferences )) == null ) return;
                break;
                case AddToConference:
                    if ( (result = model.addToConference        ( task, myID, myConferences )) == null ) return;
                break;
                case RemoveFromConference:
                    if ( (result = model.removeFromConference   ( task, myID, myConferences )) == null ) return;
                break;
                case DeleteConference:
                    if ( (result = model.deleteConference       ( task, myID, myConferences )) == null ) return;
                break;
                case SendMessageToConference:
                    if ( (result = model.sendMessageToConference( task, myID, myConferences )) == null ) return;
                break;
                case SendMessageToContact:
                    if ( (result = model.sendMessageToContact( task, myID )) == null ) return;
                break;
                case RefreshStorage:
                    if ( (result = model.refreshStorage()) == null ) return;
                break;
                case UploadFile:
                    if ( (result = model.uploadFile( task )) == null ) return;
                break;
                case DownloadFile:
                    if ( (result = model.downloadFile( task )) == null ) return;
                break;
                case RemoveFile:
                    if ( (result = model.removeFile( task )) == null ) return;
                break;
            }

            if ( !addResult( result ) ) {
                errorLog( "error on adding result to queue" );
            }
        }

        private void
        addLog(
            String log
        )
        {
            System.out.println( Controller.class.getName() + ": " + log );
        }

        private void
        errorLog(
            String message
        )
        {
            final int CALLING_METHOD = 1;
            Throwable t = new Throwable();
            StackTraceElement trace[] = t.getStackTrace();
            if ( 1 < trace.length  ) {
                StackTraceElement element = trace[ CALLING_METHOD ];
                addLog( element.getMethodName() + " " + element.getLineNumber() + " " + message );
            }
        }

        private boolean running = false;

        private       UUID              myID;
        private final Listener          myListener;
        private final ArrayList< UUID > myConferences = new ArrayList<>();


        private final ConcurrentLinkedQueue< Task >       tasks;
        private final ConcurrentLinkedQueue< ResultData > results;
     }

    private final Model model;
}
