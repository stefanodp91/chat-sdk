package it.fourn.chatsdk.core.models;

import androidx.annotation.NonNull;

public interface User {

    String getId();

    void setId(String id);

    String getUsername();

    void setUsername(String username);

    String getFirstName();

    void setFirstName(String firstName);

    String getLastName();

    void setLastName(String lastName);

    String getEmail();

    void setEmail(String email);

    @NonNull
    @Override
    String toString();
}
