package it.fourn.chatsdk.core.rx;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;

public class RxBus {

    private PublishSubject<RxBusEvent> bus;
    private CopyOnWriteArrayList<Consumer> subscribersList;

    public RxBus() {
        bus = PublishSubject.create();
        subscribersList = new CopyOnWriteArrayList();
    }

    public void send(RxBusEvent o) {
        if (o != null)
            bus.onNext(o);
    }

    public void send(List<RxBusEvent> events) {
        if (events != null) {
            for (RxBusEvent current : events) {
                this.send(current);
            }
        }
    }

    public Disposable subscribeToBus(String eventName, Consumer subscriber) {
        subscribersList.add(subscriber);
        return bus.filter(event -> event.eventName.equals(eventName)
        ).map(event -> event.data).subscribe(subscriber);
    }

    public Disposable subscribeToBus(Consumer<RxBusEvent> subscriber) {
        subscribersList.add(subscriber);
        return bus.subscribe(subscriber);
    }

    public static class RxBusEvent<T> {
        public String eventName;
        public T data;

        public RxBusEvent(String eventName, T data) {
            this.eventName = eventName;
            this.data = data;
        }
    }

    public synchronized boolean contains(Consumer consumer) {
        return subscribersList.contains(consumer);
    }

    public synchronized void dispose(Consumer consumer) {
        if (contains(consumer)) {
            subscribersList.remove(consumer);
        }
    }
}