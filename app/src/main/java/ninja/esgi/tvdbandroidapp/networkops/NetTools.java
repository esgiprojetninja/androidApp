package ninja.esgi.tvdbandroidapp.networkops;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class NetTools {
    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    private NetTools() {
    }

    public static NetTools getInstance() {
        return new NetTools();
    }

    public void clearSubscription() {
        if (compositeSubscription != null && !compositeSubscription.isUnsubscribed()) {
            compositeSubscription.clear();
        }
    }

    public void unSubscription() {
        if (compositeSubscription != null && !compositeSubscription.isUnsubscribed()) {
            compositeSubscription.unsubscribe();
        }
    }

    public void addSubscription(Subscription subscription) {
        if (compositeSubscription != null) {
            compositeSubscription.add(subscription);
        }
    }
}
