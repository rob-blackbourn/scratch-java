package net.jetblack.authfeedbus.distributor.interactors;

import java.net.InetAddress;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import javax.net.ssl.SSLSocket;

import net.jetblack.authfeedbus.distributor.roles.DistributorRole;
import net.jetblack.authfeedbus.distributor.roles.RoleManager;
import net.jetblack.authfeedbus.messages.Message;
import net.jetblack.authfeedbus.messages.MessageInputStream;
import net.jetblack.authfeedbus.messages.MessageOutputStream;
import net.jetblack.util.Disposable;
import net.jetblack.util.concurrent.EventQueue;

public class Interactor implements Comparable<Interactor>, Runnable, Disposable {
	
	private static final Logger logger = Logger.getLogger(Interactor.class.getName());

    private final BlockingQueue<Message> _writeQueue = new ArrayBlockingQueue<Message>();
    private final EventQueue<InteractorEventArgs> _eventQueue;
    private final MessageInputStream _inputStream;
    private final MessageOutputStream _outputStream;
    private final AtomicBoolean _isCancelled;
    private final RoleManager _roleManager;

    public static Interactor create(Socket socket, DistributorRole distributorRole, EventQueue<InteractorEventArgs> eventQueue, int writeQueueCapacity, AtomicBoolean isCancelled)
    {
        var interactor = new Interactor(socket, distributorRole, eventQueue, isCancelled);
        return interactor;
    }

    private Interactor(Socket socket, DistributorRole distributorRole, EventQueue<InteractorEventArgs> eventQueue, AtomicBoolean isCancelled)
    {
    	_inputStream = new MessageInputStream(socket.getInputStream());
    	_outputStream = new MessageOutputStream(socket.getOutputStream());
        Id = UUID.randomUUID();
        //User = name;
        Address = socket.getInetAddress();
        _isCancelled = isCancelled;
        _eventQueue = eventQueue;
        //_roleManager = new RoleManager(distributorRole, address, name);
    }

    public final UUID Id;
    public final InetAddress Address;

    
    private String _user;
    public String getUser() {
    	return _user;
    }
    public void setUser(String value) {
    	_user = value;
    }

    
    public void Start()
    {
        Task.Run(() => QueueReceivedMessages(), _token);
        Task.Run(() => WriteQueuedMessages(), _token);
    }

    private void QueueReceivedMessages()
    {
        while (!_token.IsCancellationRequested)
        {
            try
            {
                _eventQueue.Enqueue(new InteractorMessageEventArgs(this, ReceiveMessage()));
            }
            catch (OperationCanceledException)
            {
                break;
            }
            catch (Exception error)
            {
                _eventQueue.Enqueue(new InteractorErrorEventArgs(this, error));
                break;
            }
        }

        Log.Debug($"Exited read loop for {this}");
    }

    private void WriteQueuedMessages()
    {
        while (!_token.IsCancellationRequested)
        {
            try
            {
                var message = _writeQueue.Take(_token);
                message.Write(_stream);
            }
            catch (OperationCanceledException)
            {
                break;
            }
            catch (Exception error)
            {
                _eventQueue.Enqueue(new InteractorErrorEventArgs(this, error));
                break;
            }
        }

        Log.Debug($"Exited write loop for {this}");
    }

    public void SendMessage(Message message)
    {
        _writeQueue.Add(message, _token);
    }

    public Message ReceiveMessage()
    {
        return Message.Read(_stream);
    }

    public bool HasRole(string feed, Role role)
    {
        return _roleManager.HasRole(feed, role);
    }

    public int CompareTo(IInteractor other)
    {
        return other == null ? 1 : Id.CompareTo(other.Id);
    }

    public override bool Equals(object obj)
    {
        return Equals(obj as Interactor);
    }

    public bool Equals(IInteractor other)
    {
        return other != null && other.Id == Id;
    }

    public override int GetHashCode()
    {
        return Id.GetHashCode();
    }

    public override string ToString()
    {
        return $"{Id}: {User}@{Address}";
    }

    public void Dispose()
    {
        _stream.Close();
    }

    public static bool operator ==(Interactor a, Interactor b)
    {
        return (ReferenceEquals(a, null) && ReferenceEquals(b, null)) || (!ReferenceEquals(a, null) && a.Equals(b));
    }

    public static bool operator !=(Interactor a, Interactor b)
    {
        return !(a == b);
    }
}
