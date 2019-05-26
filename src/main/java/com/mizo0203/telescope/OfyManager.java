package com.mizo0203.telescope;

import com.googlecode.objectify.ObjectifyService;

/* package */ class OfyManager {
    /* package */ <E> void save(E entity) {
        ObjectifyService.ofy().save().entity(entity).now();
    }

    /* package */ <E> E load(Class<E> type, String id) {
        return ObjectifyService.ofy().load().type(type).id(id).now();
    }
}
