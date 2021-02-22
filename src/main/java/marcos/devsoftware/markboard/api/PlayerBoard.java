package marcos.devsoftware.markboard.api;

import java.util.Map;

public interface PlayerBoard<V, N, S> {
    V get(N paramN);

    void set(V paramV, N paramN);

    void setAll(V... paramVarArgs);

    void clear();

    void remove(N paramN);

    void delete();

    S getName();

    void setName(S paramS);

    Map<N, V> getLines();
}
