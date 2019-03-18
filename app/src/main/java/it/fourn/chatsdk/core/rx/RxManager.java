package it.fourn.chatsdk.core.rx;

import io.reactivex.disposables.CompositeDisposable;

public class RxManager {
    private static final RxManager mInstance = new RxManager();

    private CompositeDisposable mCompositeDisposable;
    private RxBus mRxBus;
    private SchedulerProvider mSchedulerProvider;

    public static RxManager getInstance() {
        return mInstance;
    }

    private RxManager() {
        mCompositeDisposable = new CompositeDisposable();
        mRxBus = new RxBus();
        mSchedulerProvider = new AppSchedulerProvider();
    }

    public CompositeDisposable getCompositeDisposable() {
        return (mCompositeDisposable == null || mCompositeDisposable.isDisposed()) ?
                new CompositeDisposable() : mCompositeDisposable;
    }

    public RxBus getRxBus() {
        if (mRxBus == null)
            mRxBus = new RxBus();
        return mRxBus;
    }

    public SchedulerProvider getSchedulerProvider() {
        if (mSchedulerProvider == null)
            mSchedulerProvider = new AppSchedulerProvider();
        return mSchedulerProvider;
    }
}