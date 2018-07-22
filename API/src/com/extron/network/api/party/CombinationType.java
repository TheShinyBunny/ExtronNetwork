package com.extron.network.api.party;

public enum CombinationType {
    TO, WITH, ACCEPT;

    public CombinationType opposite() {
        return this == TO ? WITH : this == WITH ? TO : this;
    }
}
