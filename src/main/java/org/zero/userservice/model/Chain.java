package org.zero.userservice.model;

public interface Chain {
    Chain next(Chain chain);
}
