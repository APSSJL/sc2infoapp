package interfaces;

import android.util.Pair;

public interface IPredictable {
    Pair<Integer, Integer> getDistribution();
    Boolean getPredicted();
    void predict1();
    void predict2();
}
